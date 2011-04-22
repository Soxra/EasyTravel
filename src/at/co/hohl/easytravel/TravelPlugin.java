package at.co.hohl.easytravel;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.Permissions.PermissionsHandler;
import at.co.hohl.easytravel.commands.DepartCommandExecutor;
import at.co.hohl.easytravel.commands.TravelCommandExecutor;
import at.co.hohl.easytravel.data.FlatFileTravelPortContainer;
import at.co.hohl.easytravel.data.PlayerInformation;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.easytravel.data.TravelPortContainer;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.economy.EconomyHandler;
import at.co.hohl.economy.iConomyHandler;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Location;
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
    /** Listener for player events. */
    private final TravelPlayerListener playerListener = new TravelPlayerListener(this);

    /** CommandExecutor for /travel. */
    private final TravelCommandExecutor travelCommandExecutor = new TravelCommandExecutor(this);

    /** CommandExecutor for /depart. */
    private final DepartCommandExecutor departCommandExecutor = new DepartCommandExecutor(this);

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
        setupPermissions();
        setupEventHandler();

        logger.info(String.format("%s is enabled!", getDescription().getFullName()));
    }

    /** Disables this plugin. */
    public void onDisable() {
        travelPortContainer.save();

        logger.info(String.format("%s is disabled!", getDescription().getName()));
    }

    /** Reloads the configuration of this application. */
    public void onReload() {
        getConfiguration().load();
        loadConfiguration();
    }

    /**
     * Teleports the player.
     *
     * @param player      the player to port.
     * @param currentPort the current port to warp from.
     * @throws WarpException thrown if the player couldn't get warped.
     */
    public void teleportPlayer(Player player, TravelPort currentPort) {
        Integer targetId = currentPort.getTargetId();

        if (targetId != null) {
            TravelPort targetPort = travelPortContainer.get(targetId);

            Location currentPlayer = player.getLocation();
            Location currentEdge1 = currentPort.getEdge1();
            Location targetEdge1 = targetPort.getEdge1();
            double playerOffsetX = currentEdge1.getX() - currentPlayer.getX();
            double playerOffsetY = currentEdge1.getY() - currentPlayer.getY();
            double playerOffsetZ = currentEdge1.getZ() - currentPlayer.getZ();
            Location targetPlayer = new Location(targetEdge1.getWorld(), targetEdge1.getX() - playerOffsetX,
                    targetEdge1.getY() - playerOffsetY, targetEdge1.getZ() - playerOffsetZ);
            player.teleport(targetPlayer);

            PlayerInformation playerInformation = getPlayerInformation(player);
            playerInformation.setCurrentPort(targetPort);
            playerInformation.setAlreadyTravelled(true);

            playerListener.onPlayerTraveled(player, currentPort, targetPort);
        } else {
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
    public Selection getSelection(Player player) {
        return worldEditPlugin.getSelection(player);
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
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Low, this);

        getCommand("travel").setExecutor(travelCommandExecutor);
        getCommand("depart").setExecutor(departCommandExecutor);
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
            logger.severe(String.format("%s requires WorldEdit! Please install first.", getDescription().getName()));
            this.setEnabled(false);
        }
    }

    /** Setups the economy system. */
    private void setupEconomy() {
        Plugin plugin = getServer().getPluginManager().getPlugin("iConomy");
        if (plugin != null) {
            economyHandler = new iConomyHandler();
            logger.info(String.format("%s connected to iConomy successfully!", getDescription().getName()));
        }
    }
}

