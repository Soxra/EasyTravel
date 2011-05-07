package at.co.hohl.easytravel;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.Permissions.PermissionsHandler;
import at.co.hohl.easytravel.commands.DepartCommandExecutor;
import at.co.hohl.easytravel.commands.PortCommandExecutor;
import at.co.hohl.easytravel.data.*;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.economy.EconomyHandler;
import at.co.hohl.economy.iConomyHandler;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.entity.Player;
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
    /** Listener for player events. */
    private final TravelPlayerListener playerListener = new TravelPlayerListener(this);

    /** Player Information storage. */
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
        getConfiguration().load();
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
     * Teleports the player.
     *
     * @param player      the player to port.
     * @param currentPort the current port to warp from.
     * @throws WarpException thrown if the player couldn't get warped.
     */
    public void teleportPlayer(Player player, TravelPort currentPort) {
        try {
            Integer targetId = currentPort.getTargetId();

            TravelPort targetPort = travelPortContainer.get(targetId);
            targetPort.getDestination().teleport(player);

            PlayerInformation playerInformation = getPlayerInformation(player);
            playerInformation.setCurrentPort(targetPort);
            playerInformation.setAlreadyTravelled(true);

            playerListener.onPlayerTraveled(player, currentPort, targetPort);
        } catch (TravelPortNotFound exception) {
            String exceptionMessage = String.format("Port '%s' (ID:%d) is linked to an invalid port (ID:%d)!",
                    currentPort.getName(), currentPort.getId(), currentPort.getTargetId());
            throw new WarpException(exceptionMessage);
        }
    }

    /**
     * Returns the selection of the player.
     *
     * @param player the player
     * @return the selection of the passed player.
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

    /** @return the used player listener. */
    public TravelPlayerListener getPlayerListener() {
        return playerListener;
    }

    /**
     * Returns the PlayerInformation data holder, for the passed player. Automatically creates a new one, if the player
     * isn't in database yet.
     *
     * @param player the player.
     * @return the PlayerInformation.
     */
    public PlayerInformation getPlayerInformation(Player player) {
        if (!playerInformationMap.containsKey(player)) {
            playerInformationMap.put(player, new PlayerInformation());
        }

        return playerInformationMap.get(player);
    }

    /** @return the logger of this application. */
    public Logger getLogger() {
        return logger;
    }

    /** Loads the configuration. */
    private void loadConfiguration() {
        // Properties...
        Configuration configuration = getConfiguration();

        // Messages...
        Configuration messages = new Configuration(new File(getDataFolder(), "messages.yml"));
        Messages.load(messages);

        // TravelPorts...
        travelPortContainer = new FlatFileTravelPortContainer(this, new File(getDataFolder(), "ports.csv"));
        travelPortContainer.load();
    }

    /** Setups all event handlers, used by this plugin. */
    private void setupEventHandler() {
        int locationUpdateInterval = getConfiguration().getInt("location-update-interval", 25);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                playerListener.onPlayerLocationUpdate();
            }
        }, locationUpdateInterval * 3, locationUpdateInterval);

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

