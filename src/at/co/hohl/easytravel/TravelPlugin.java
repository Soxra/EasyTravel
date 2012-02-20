/*
 * EasyTravel
 * Copyright (C) 2011 Michael Hohl <http://www.hohl.co.at/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.co.hohl.easytravel;

import at.co.hohl.easytravel.commands.DepartCommandExecutor;
import at.co.hohl.easytravel.commands.PortCommandExecutor;
import at.co.hohl.easytravel.listener.TravelPlayerListener;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.Area;
import at.co.hohl.easytravel.ports.CuboidArea;
import at.co.hohl.easytravel.ports.TravelPortContainer;
import at.co.hohl.easytravel.ports.implementation.file.FlatFileTravelPortContainer;
import at.co.hohl.permissions.PermissionHandler;
import at.co.hohl.utils.network.Download;
import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * EasyTravel for Bukkit
 *
 * @author hohl
 */
public class TravelPlugin extends JavaPlugin {
    /**
     * Ticks used for waiting after sending the arrived notification.
     */
    public static int ARRIVED_NOTIFICATION_DELAY;

    /**
     * Ticks waited after departing.
     */
    public static int DEPART_DELAY;

    /**
     * Flag if owner should get paid for TravelPorts.
     */
    public static boolean PAY_OWNER;

    /**
     * Flag if owner should get notified.
     */
    public static boolean NOTIFY_OWNER;

    /**
     * URL to check for updates.
     */
    private static final String RELEASE_REPOSITORY_INFORMATION =
        "http://github.com/hohl/EasyTravel/raw/master/res/updates.yml";

    /**
     * Listener for players events.
     */
    private final TravelPlayerListener playerListener = new TravelPlayerListener(this);

    /**
     * Player Information implementation.
     */
    private final Map<Player, PlayerInformation> playerInformationMap = new HashMap<Player, PlayerInformation>();

    /**
     * Logger used for outputting debug information.
     */
    private final Logger logger = Logger.getLogger("Minecraft.EasyTravel");

    /**
     * The permissions handler.
     */
    private PermissionHandler permissionHandler;

    /**
     * The WorldEdit plugin.
     */
    private WorldEditPlugin worldEditPlugin;

    /**
     * The container which holds the TravelPorts.
     */
    private FlatFileTravelPortContainer travelPortContainer;

    /**
     * Downloaded configuration file which contains information about the latest version.
     */
    private FileConfiguration releaseRepository;

    /**
     * Configuration for the messages.
     */
    private FileConfiguration messagesConfig = null;

    /**
     * File for the messages configuration.
     */
    private File messagesConfigurationFile = null;

    /**
     * Enables this plugin.
     */
    public void onEnable() {
        loadConfiguration();
        setupWorldEdit();
        setupPermissions();
        setupEventHandler();

        logger.info(String.format("%s is enabled!", getDescription().getFullName()));

        if (isVersionPreview()) {
            logger.info("Notice: You are using a PREVIEW build! Not recommended for production server!");
        } else if (getConfig().getBoolean("check-for-updates", true)) {
            checkForOutdated();
        }
    }

    /**
     * Disables this plugin.
     */
    public void onDisable() {
        if (getConfig().getBoolean("auto-save", true)) {
            save();
        }

        logger.info(String.format("%s is disabled!", getDescription().getName()));
    }

    /**
     * Called when forcing a reload.
     */
    public void onReload() {
        reloadMessagesConfig();
        reloadConfig();

        loadConfiguration();
    }

    /**
     * Called when forcing a save.
     */
    public void onSave() {
        logger.info("Save TravelPorts...");
        travelPortContainer.save();
    }

    /**
     * Saves the configuration an the TravelPorts.
     */
    public final void save() {
        logger.info("EasyTravel forcing a save...");
        onSave();
        logger.info("EasyTravel ports and configuration saved!");
    }

    /**
     * Reloads the server.
     */
    public final void reload() {
        onReload();
        logger.info("EasyTravel reloaded!");
    }

    /**
     * Checks if the server software is a preview version.
     *
     * @return true, if this is a preview version.
     */
    public final boolean isVersionPreview() {
        return getDescription().getVersion().contains("preview");
    }

    /**
     * Checks if the server software outdated.
     *
     * @return true, if the server software is outdated.
     */
    public final boolean isVersionOutdated() {
        if (!isVersionPreview() && releaseRepository != null) {
            String currentVersion = getDescription().getVersion();
            return !currentVersion.equals(releaseRepository.getString("latest.version", currentVersion));
        } else {
            return false;
        }
    }

    /**
     * Returns the selection of the players.
     *
     * @param player the players
     * @return the selection of the passed players.
     */
    public Area getSelectedArea(Player player) {
        if (worldEditPlugin != null) {
            Selection weSelection = worldEditPlugin.getSelection(player);

            if (weSelection != null && weSelection.getArea() > 0) {
                return new CuboidArea(weSelection.getMinimumPoint(), weSelection.getMaximumPoint());
            } else {
                return null;
            }
        } else {
            return null; // ToDo: Implement own selection tool!
        }
    }

    /**
     * @return the current permissions handler.
     */
    public PermissionHandler getPermissionsHandler() {
        return permissionHandler;
    }

    /**
     * @return the logger of this application.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @return true, if there is at least one payment method!
     */
    public boolean hasPaymentMethods() {
        return Methods.hasMethod();
    }

    /**
     * @return the payment method.
     */
    public Method getPaymentMethod() {
        return Methods.getMethod();
    }

    /**
     * @return the container for the travel ports.
     */
    public TravelPortContainer getTravelPorts() {
        return travelPortContainer;
    }

    /**
     * Returns the PlayerInformation ports holder, for the passed players. Automatically creates a new one, if the
     * players isn't in database yet.
     *
     * @param player the players.
     * @return the PlayerInformation.
     */
    public PlayerInformation getPlayerInformation(Player player) {
        if (!playerInformationMap.containsKey(player)) {
            playerInformationMap.put(player, new PlayerInformation(player));
        }

        return playerInformationMap.get(player);
    }

    /**
     * Removes all information, connected to the passed player. (Like stored passwords, current port, and so on).
     *
     * @param player the player which information should be removed.
     */
    public void removePlayerInformation(Player player) {
        if (!playerInformationMap.containsKey(player)) {
            playerInformationMap.remove(player);
        }
    }

    /**
     * Reloads the messages configuration.
     */
    public void reloadMessagesConfig() {
        if (messagesConfigurationFile == null) {
            messagesConfigurationFile = new File(getDataFolder(), "messages.yml");
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigurationFile);

        // Look for defaults in the jar
        InputStream defConfigStream = getResource("customConfig.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            messagesConfig.setDefaults(defConfig);
        }
    }

    /**
     * @return the messages configuration.
     */
    public FileConfiguration getMessagesConfig() {
        if (messagesConfig == null) {
            reloadMessagesConfig();
        }
        return messagesConfig;
    }


    /**
     * Loads the configuration.
     */
    private void loadConfiguration() {
        // Properties...
        ARRIVED_NOTIFICATION_DELAY = getConfig().getInt("arrived-notification-delay", 10);
        DEPART_DELAY = getConfig().getInt("depart-delay", 0);
        PAY_OWNER = getConfig().getBoolean("pay-owner", true);
        NOTIFY_OWNER = getConfig().getBoolean("notify-owner", false);

        // Messages...
        Messages.load(getMessagesConfig());

        // TravelPorts...
        travelPortContainer = new FlatFileTravelPortContainer(this, new File(getDataFolder(), "ports.csv"));
        travelPortContainer.load();
    }

    /**
     * Setups all event handlers, used by this plugin.
     */
    private void setupEventHandler() {
        // Listen to plugins enabling, used for finding an economy plugin.
        Methods.setMethod(getServer().getPluginManager());

        // Remove player information on quit.
        getServer().getPluginManager().registerEvents(playerListener, this);

        // Update player information controlled by an scheduler.
        int locationUpdateInterval = getConfig().getInt("location-update-interval", 60);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                playerListener.onPlayerLocationUpdate();
            }
        }, locationUpdateInterval * 3, locationUpdateInterval);

        // Register commands.
        getCommand("port").setExecutor(new PortCommandExecutor(this));
        getCommand("depart").setExecutor(new DepartCommandExecutor(this));
    }

    /**
     * Setups the permissions handler.
     */
    private void setupPermissions() {
        permissionHandler = new PermissionHandler(this);
    }

    /**
     * Setups the WorldEdit client.
     */
    private void setupWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin != null) {
            worldEditPlugin = (WorldEditPlugin) plugin;
            logger.info(String.format("%s connected to WorldEdit successfully!", getDescription().getName()));
        } else {
            logger.severe(String.format("%s requires WorldEdit! Please install first!", getDescription().getName()));
        }
    }

    /**
     * Downloads information about latest release.
     */
    private void checkForOutdated() {
        try {
            new Download(new URL(RELEASE_REPOSITORY_INFORMATION), new File(getDataFolder(), "updates.yml")) {
                /**
                 * Called when the download completed.
                 *
                 * @param downloadedFile the downloaded file.
                 */
                @Override
                public void onComplete(File downloadedFile) {
                    releaseRepository = YamlConfiguration.loadConfiguration(downloadedFile);

                    if (isVersionOutdated()) {
                        logger.info("[EasyTravel] version outdated date.");
                    } else {
                        logger.info("[EasyTravel] version is up to date.");
                    }
                }

                /** Called when an error occurs on downloading. */
                @Override
                public void onError() {
                    logger.warning("[EasyTravel] Can't connect to update server.");
                }
            };
        } catch (MalformedURLException e) {
            logger.severe("[EasyTravel] Error when creating url for checking for updates!" +
                "Please contact the developer of this plugin.");
        }
    }
}

