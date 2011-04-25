package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.Permissions.PermissionsHandler;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * CommandExecutor for /port command.
 *
 * @author Michael Hohl
 */
public class PortCommandExecutor implements CommandExecutor {
    /** The plugin which holds the instance. */
    private final TravelPlugin plugin;

    /** Map with the sub commands. */
    private final Map<String, SubCommandExecutor> subCommands;

    /**
     * Creates a new instance of the PortCommandExecutor.
     *
     * @param plugin the plugin which holds the instance.
     */
    public PortCommandExecutor(TravelPlugin plugin) {
        this.plugin = plugin;

        subCommands = new HashMap<String, SubCommandExecutor>();
        subCommands.put("help", new PortHelpCommandExecutor(plugin, this));
        subCommands.put("create", new PortCreateCommandExecutor(plugin, this));
        subCommands.put("remove", new PortRemoveCommandExecutor(plugin, this));
        subCommands.put("link", new PortLinkCommandExecutor(plugin, this));
        subCommands.put("unlink", new PortUnlinkCommandExecutor(plugin, this));
        subCommands.put("destination", new PortDestinationCommandExecutor(plugin, this));
        subCommands.put("info", new PortInfoCommandExecutor(plugin, this));
        subCommands.put("list", new PortListCommandExecutor(plugin, this));
        subCommands.put("search", new PortSearchCommandExecutor(plugin, this));
        subCommands.put("compass", new PortCompassCommandExecutor(plugin, this));
        subCommands.put("price", new PortPriceCommandExecutor(plugin, this));
        subCommands.put("owner", new PortOwnerCommandExecutor(plugin, this));
        subCommands.put("save", new PortSaveCommandExecutor(plugin, this));
        subCommands.put("reload", new PortReloadCommandExecutor(plugin, this));
    }

    /**
     * Called when the player use the port command.
     *
     * @param sender  the sender of the command.
     * @param command the command itself.
     * @param label   the label used for calling the command.
     * @param args    the arguments passed to the command.
     * @return true, if the CommandExecutor could handle the command.
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only use the /depart command as player! Doesn't make sense anyway for you ;)");

            return true;
        }

        if (args.length > 0) {
            SubCommandExecutor subCommandExecutor = subCommands.get(args[0]);

            if (subCommandExecutor != null) {
                if (subCommandExecutor.isValidNumberOfArguments(args.length - 1)) {
                    PermissionsHandler permissionsHandler = plugin.getPermissionsHandler();
                    Permission requiredPermission = subCommandExecutor.getRequiredPermission();

                    if (requiredPermission == null ||
                            permissionsHandler.hasPermission(sender, requiredPermission)) {
                        return subCommandExecutor.onCommand(sender, command, label, args);
                    }
                } else {
                    ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-num-args"));
                }
            } else {
                ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-use"));
            }

            return true;
        } else {
            return false;
        }
    }

    /** @return sub commands by this plugin. */
    public Map<String, SubCommandExecutor> getSubCommands() {
        return subCommands;
    }
}
