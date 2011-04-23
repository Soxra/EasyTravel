package at.co.hohl.easytravel.commands;

import at.co.hohl.easytravel.TravelPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/** Abstract class for all subclasses. */
public abstract class SubCommandExecutor implements CommandExecutor {
    /** Plugin which holds the instance of the subcommand. */
    protected TravelPlugin plugin;

    /** Parent CommandExecutor which holds this SubCommandExecutor. */
    protected CommandExecutor parent;

    /** Number of arguments which are allowed. */
    private int minimumNumberOfArguments, maximumNumberOfArguments;

    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public SubCommandExecutor(TravelPlugin plugin, CommandExecutor parent, int minimumNumberOfArguments,
                              int maximumNumberOfArguments) {
        this.plugin = plugin;
        this.parent = parent;
        this.minimumNumberOfArguments = minimumNumberOfArguments;
        this.maximumNumberOfArguments = maximumNumberOfArguments;
    }

    /**
     * Returns true, if the number of passed arguments is valid.
     *
     * @param numberOfArguments
     * @return
     */
    public boolean isValidNumberOfArguments(int numberOfArguments) {
        return minimumNumberOfArguments <= numberOfArguments &&
                (numberOfArguments <= maximumNumberOfArguments || maximumNumberOfArguments == -1);
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
    public abstract boolean onCommand(CommandSender sender, Command parentCommand, String label, String[] args);

    /** @return string which describes the valid usage. */
    public abstract String getUsage();

    /** @return description of the command. */
    public abstract String getDescription();
}
