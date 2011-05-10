package at.co.hohl.easytravel;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.Permissions.PermissionsHandler;
import at.co.hohl.easytravel.commands.DepartCommandExecutor;
import at.co.hohl.easytravel.commands.PortCommandExecutor;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.players.PlayerInformation;
import at.co.hohl.easytravel.players.TravelPlayerListener;
import at.co.hohl.easytravel.ports.Area;
import at.co.hohl.easytravel.ports.CuboidArea;
import at.co.hohl.easytravel.ports.TravelPortContainer;
import at.co.hohl.easytravel.ports.implementation.file.FlatFileTravelPortContainer;
import at.co.hohl.economy.EconomyHandler;
import at.co.hohl.economy.iConomyHandler;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
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

    /** Listener for players events. */
    private final TravelPlayerListener playerListener = new TravelPlayerListener(this);

    /** Player Information implementation. */
    private final Map<Player, PlayerInformation> playerInformationMap = new HashMap<Player, PlayerInformation>();

    /** Logger used for outputting debug information. */
    private final Logger logger = Logger.getLogger("Minecraft.EasyTravel");

    /** The permissions handler. */
    private PermissionsHandler permissionsHandler;

    /** The economy handler. */
    private EconomyHandler economyHandler;

    /** The WorldEdit plugin. */
    private WorldEditPlugin worldEditPlugin;

    /** The container which holds the TravelPorts. */
    private FlatFileTravelPortContainer travelPortContainer;

    /** Enables this plugin. */
    public void onEnable() {
        loadConfiguration();
        setupWorldEdit();
        setupEconomy();
        setupPermissions();
        setupEventHandler();

        logger.info(String.format("%s is enabled!", getDescription().getFullName()));

        if (getDescription().getVersion().contains("preview")) {
            logger.info("Notice: You are using a PREVIEW build! Not recommended for production server!");
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

    /** @return the current economy handler. */
    public EconomyHandler getEconomyHandler() {
        return economyHandler;
    }

    /** @return the container for the travel ports. */
    public TravelPortContainer getTravelPorts() {
        return travelPortContainer;
    }

    /** @return the used players listener. */
    public TravelPlayerListener getPlayerListener() {
        return playerListener;
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
            playerInformationMap.put(player, new PlayerInformation());
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

    /** @return the logger of this application. */
    public Logger getLogger() {
        return logger;
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
        // Remove player information on quit.
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Low, this);

        // Update player information controlled by an scheduler.
        int locationUpdateInterval = getConfiguration().getInt("location-update-interval", 15);
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

    /** Setups the economy system. */
    private void setupEconomy() {
        PluginManager pm = getServer().getPluginManager();

        if (pm.getPlugin("iConomy") != null) {
            economyHandler = new iConomyHandler();
            logger.info(String.format("%s connected to iConomy successfully!", getDescription().getName()));
        } else {
            logger.warning(String.format("No economy system found by %s!", getDescription().getName()));
        }
    }
}

