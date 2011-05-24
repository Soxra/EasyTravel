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

import at.co.hohl.Permissions.Permission;
import at.co.hohl.Permissions.PermissionsHandler;
import at.co.hohl.easytravel.commands.DepartCommandExecutor;
import at.co.hohl.easytravel.commands.PortCommandExecutor;
import at.co.hohl.easytravel.listener.EconomyPluginListener;
import at.co.hohl.easytravel.listener.TravelPlayerListener;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.Area;
import at.co.hohl.easytravel.ports.CuboidArea;
import at.co.hohl.easytravel.ports.TravelPortContainer;
import at.co.hohl.easytravel.ports.implementation.file.FlatFileTravelPortContainer;
import at.co.hohl.utils.network.Download;
import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
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
    /** Ticks used for waiting after sending the arrived notification. */
    public static int ARRIVED_NOTIFICATION_DELAY;

    /** Ticks waited after departing. */
    public static int DEPART_DELAY;

    /** URL to check for updates. */
    private static final String RELEASE_REPOSITORY_INFORMATION =
            "https://github.com/hohl/EasyTravel/raw/master/res/updates.yml";

    /** Listener for players events. */
    private final TravelPlayerListener playerListener = new TravelPlayerListener(this);

    /** Listener for economy plugins. */
    private final EconomyPluginListener economyPluginListener = new EconomyPluginListener(this);

    /** Player Information implementation. */
    private final Map<Player, PlayerInformation> playerInformationMap = new HashMap<Player, PlayerInformation>();

    /** Logger used for outputting debug information. */
    private final Logger logger = Logger.getLogger("Minecraft.EasyTravel");

    /** The permissions handler. */
    private PermissionsHandler permissionsHandler;

    /** The economy handler. */
    private Methods methods = new Methods();

    /** The WorldEdit plugin. */
    private WorldEditPlugin worldEditPlugin;

    /** The container which holds the TravelPorts. */
    private FlatFileTravelPortContainer travelPortContainer;

    /** Downloaded configuration file which contains information about the latest version. */
    private Configuration releaseRepository;

    /** Enables this plugin. */
    public void onEnable() {
        loadConfiguration();
        setupWorldEdit();
        setupPermissions();
        setupEventHandler();

        logger.info(String.format("%s is enabled!", getDescription().getFullName()));

        if (isVersionPreview()) {
            logger.info("Notice: You are using a PREVIEW build! Not recommended for production server!");
        } else if (getConfiguration().getBoolean("check-for-updates", true)) {
            checkForNewVersions();
        }
    }

    /** Disables this plugin. */
    public void onDisable() {
        save();

        logger.info(String.format("%s is disabled!", getDescription().getName()));
    }

    /** Called when forcing a reload. */
    public void onReload() {
        loadConfiguration();
    }

    /** Called when forcing a save. */
    public void onSave() {
        logger.info("Save TravelPorts...");
        travelPortContainer.save();
    }

    /** Saves the configuration an the TravelPorts. */
    public final void save() {
        logger.info("EasyTravel forcing a save...");
        onSave();
        logger.info("EasyTravel ports and configuration saved!");
    }

    /** Reloads the server. */
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

    /** @return the current permissions handler. */
    public PermissionsHandler getPermissionsHandler() {
        return permissionsHandler;
    }

    /** @return the logger of this application. */
    public Logger getLogger() {
        return logger;
    }

    /** @return the payment method. */
    public Methods getMethods() {
        return methods;
    }

    /** @return true, if there is at least one payment method! */
    public boolean hasPaymentMethods() {
        return methods.hasMethod();
    }

    /** @return the payment method. */
    public Method getPaymentMethod() {
        return methods.getMethod();
    }

    /** @return the container for the travel ports. */
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

    /** Loads the configuration. */
    private void loadConfiguration() {
        // Properties...
        getConfiguration().load();
        ARRIVED_NOTIFICATION_DELAY = getConfiguration().getInt("arrived-notification-delay", 10);
        DEPART_DELAY = getConfiguration().getInt("depart-delay", 0);

        // Messages...
        Configuration messages = new Configuration(new File(getDataFolder(), "messages.yml"));
        Messages.load(messages);

        // TravelPorts...
        travelPortContainer = new FlatFileTravelPortContainer(this, new File(getDataFolder(), "ports.csv"));
        travelPortContainer.load();
    }

    /** Setups all event handlers, used by this plugin. */
    private void setupEventHandler() {
        // Get the plugin manager.
        PluginManager pluginManager = getServer().getPluginManager();

        // Listen to plugins enabling, used for finding an economy plugin.
        pluginManager.registerEvent(Event.Type.PLUGIN_ENABLE, economyPluginListener, Event.Priority.Monitor, this);
        pluginManager.registerEvent(Event.Type.PLUGIN_DISABLE, economyPluginListener, Event.Priority.Monitor, this);

        // Remove player information on quit.
        pluginManager.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Low, this);
        pluginManager.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Low, this);

        // Update player information controlled by an scheduler.
        int locationUpdateInterval = getConfiguration().getInt("location-update-interval", 40);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                playerListener.onPlayerLocationUpdate();
            }
        }, locationUpdateInterval * 3, locationUpdateInterval);

        // Register commands.
        getCommand("port").setExecutor(new PortCommandExecutor(this));
        getCommand("depart").setExecutor(new DepartCommandExecutor(this));
    }

    /** Setups the permissions handler. */
    private void setupPermissions() {
        permissionsHandler = Permission.getHandler(this);
    }

    /** Setups the WorldEdit client. */
    private void setupWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin != null) {
            worldEditPlugin = (WorldEditPlugin) plugin;
            logger.info(String.format("%s connected to WorldEdit successfully!", getDescription().getName()));
        } else {
            logger.severe(String.format("%s requires WorldEdit! Please install first!", getDescription().getName()));
        }
    }

    /** Downloads information about latest release. */
    private void checkForNewVersions() {
        try {
            new Download(new URL(RELEASE_REPOSITORY_INFORMATION), new File(getDataFolder(), "updates.yml")) {
                /**
                 * Called when the download completed.
                 *
                 * @param downloadedFile the downloaded file.
                 */
                @Override
                public void onComplete(File downloadedFile) {
                    logger.info("[EasyTravel] Connected to update server.");
                    releaseRepository = new Configuration(downloadedFile);
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

