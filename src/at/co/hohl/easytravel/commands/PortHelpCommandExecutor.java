package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.Permissions.PermissionsHandler;
import at.co.hohl.easytravel.TravelPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * SubCommandExecutor for the port help command.
 *
 * @author Michael Hohl
 */
public class PortHelpCommandExecutor extends SubCommandExecutor {
    /** Number of help entries per page. */
    private static final int ENTRIES_PER_PAGE = 7;

    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortHelpCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
        super(plugin, parent, 0, 1);
    }

    /**
     * Called when the players uses the parentCommand.
     *
     * @param sender        the sender of the command.
     * @param parentCommand the parent command.
     * @param label         the label of the parent command.
     * @param args          the arguments passed to the parentCommand. (Index 0 = the label of the sub parentCommand
     *                      itself!)
     * @return true, if the SubCommandExecutor could handle the parentCommand.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command parentCommand, String label, String[] args) {
        PermissionsHandler permissionsHandler = plugin.getPermissionsHandler();

        Map<String, SubCommandExecutor> commandMap = ((PortCommandExecutor) this.parent).getSubCommands();
        List<String> commands = new LinkedList<String>(commandMap.keySet());
        Collections.sort(commands);

        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException exception) {
                sender.sendMessage(ChatColor.RED + "Invalid Page!");
            }
        }

        sender.sendMessage(ChatColor.GREEN + String.format("= = = %s [Page %d/%d] = = =",
                plugin.getDescription().getName(), page, (commands.size() / ENTRIES_PER_PAGE) + 1));

        int start = (page - 1) * ENTRIES_PER_PAGE;
        int end = Math.min(page * ENTRIES_PER_PAGE, commands.size());
        for (int index = start; index < end; ++index) {
            SubCommandExecutor commandExecutor = commandMap.get(commands.get(index));
            Permission permission = commandExecutor.getRequiredPermission();
            if (permission == null || permissionsHandler.hasPermission(sender, permission)) {
                String helpLine = String.format("%s%s%s - %s", ChatColor.GRAY,
                        commandExecutor.getUsage().replace("<command>", label), ChatColor.WHITE,
                        commandExecutor.getDescription());
                sender.sendMessage(helpLine);
            }
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> help [<page>]";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Shows this help.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return null;
    }
}
