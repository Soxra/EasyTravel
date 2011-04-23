package at.co.hohl.easytravel.commands;

import at.co.hohl.easytravel.TravelPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collection;

/**
 * SubCommandExecutor for the port help command.
 *
 * @author Michael Hohl
 */
public class PortHelpCommandExecutor extends SubCommandExecutor {
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
     * Called when the player uses the parentCommand.
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
        sender.sendMessage(ChatColor.GREEN + String.format("= = = %s [Version %s] = = =",
                plugin.getDescription().getName(), plugin.getDescription().getVersion()));

        Collection<SubCommandExecutor> commands = ((PortCommandExecutor) this.parent).getSubCommands().values();

        for (SubCommandExecutor command : commands) {
            String helpLine = String.format("%s%s%s - %s", ChatColor.GRAY,
                    command.getUsage().replace("<command>", label), ChatColor.WHITE, command.getDescription());
            sender.sendMessage(helpLine);
        }

        sender.sendMessage("");
        sender.sendMessage(
                ChatColor.GRAY + "As ID you can pass the unique id or (a part of) the name of the TravelPort.");

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> help";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Shows this help.";
    }
}
