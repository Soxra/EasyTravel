package at.co.hohl.easytravel.commands;

import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.data.InvalidLinkException;
import at.co.hohl.easytravel.data.InvalidPortIdException;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.easytravel.data.TravelPortContainer;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.utils.ChatHelper;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * CommandExecutor for the /travel-command.
 *
 * @author Michael Hohl
 */
public class TravelCommandExecutor implements CommandExecutor {
    /** The plugin which holds the instance. */
    private final TravelPlugin plugin;

    /** The Container which holds the travel ports. */
    private TravelPortContainer container;

    /**
     * Creates a new /travel-CommandExecutor.
     *
     * @param plugin the plugin, which holds the instance.
     */
    public TravelCommandExecutor(TravelPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when the user uses the /travel command. (Moderators only)
     *
     * @param sender  the sender which sends the command.
     * @param command the command itself.
     * @param label   the label used for the command.
     * @param args    the arguments passed to the command.
     * @return
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.getPermissionsHandler().hasPermission(sender, TravelPermissions.MODERATE_PERMISSION)) {
            if (args.length == 0 || "help".equals(args[0])) {
                onHelpCommand(sender);
                return true;
            } else if ("reload".equals(args[0])) {
                plugin.onReload();
                sender.sendMessage(ChatColor.GREEN + "Reloaded successfully!");
                return true;
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    container = plugin.getTravelPorts();

                    if ("create".equals(args[0])) {
                        if (args.length > 1) {
                            StringBuilder nameBuilder = new StringBuilder();

                            for (int index = 1; index < args.length; ++index) {
                                nameBuilder.append(args[index]);
                                nameBuilder.append(" ");
                            }

                            String nameOfPort = nameBuilder.toString().trim();
                            onCreateCommand(player, nameOfPort);
                        } else {
                            ChatHelper.sendMessage(player, Messages.needToPassNameForPort);
                        }
                    } else if ("remove".equals(args[0])) {
                        if (args.length == 1) {
                            onRemoveCommand(player);
                        } else if (args.length == 2) {
                            try {
                                onRemoveCommand(player, Integer.parseInt(args[1]));
                            } catch (NumberFormatException exception) {
                                ChatHelper.sendMessage(player, Messages.invalidUseOfCommand);
                            }
                        } else {
                            ChatHelper.sendMessage(player, Messages.invalidUseOfCommand);
                        }
                    } else if ("link".equals(args[0])) {
                        if (args.length == 2) {
                            try {
                                Integer port1 = Integer.valueOf(args[1]);

                                onLinkCommand(player, port1);
                            } catch (NumberFormatException exception) {
                                ChatHelper.sendMessage(player, Messages.invalidUseOfCommand);
                            }
                        } else if (args.length == 3) {
                            try {
                                Integer port1 = Integer.valueOf((args[1]));
                                Integer port2 = Integer.valueOf((args[2]));

                                onLinkCommand(player, port1, port2);
                            } catch (NumberFormatException exception) {
                                ChatHelper.sendMessage(player, Messages.invalidUseOfCommand);
                            }
                        } else {
                            ChatHelper.sendMessage(player, Messages.invalidUseOfCommand);
                        }
                    } else if ("unlink".equals(args[0])) {
                        if (args.length == 1) {
                            onUnlinkCommand(player);
                        } else if (args.length == 2) {
                            try {
                                onUnlinkCommand(player, Integer.parseInt(args[1]));
                            } catch (NumberFormatException exception) {
                                ChatHelper.sendMessage(player, Messages.invalidUseOfCommand);
                            }
                        } else {
                            ChatHelper.sendMessage(player, Messages.invalidUseOfCommand);
                        }
                    } else if ("info".equals(args[0])) {
                        if (args.length == 1) {
                            onInfoCommand(player);
                        } else if (args.length == 2) {
                            try {
                                onInfoCommand(player, Integer.parseInt(args[1]));
                            } catch (NumberFormatException exception) {
                                ChatHelper.sendMessage(player, Messages.invalidUseOfCommand);
                            }
                        } else {
                            ChatHelper.sendMessage(player, Messages.invalidUseOfCommand);
                        }
                    }

                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Only use the /travel command as player!");

                    return false;
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have the permissions for that!");

            return true;
        }
    }

    /**
     * Called when the sender calls the help command.
     *
     * @param sender the sender which called the help command.
     */
    public void onHelpCommand(CommandSender sender) {
        PluginDescriptionFile pdf = plugin.getDescription();
        sender.sendMessage(ChatColor.GREEN + String.format("= = = %s [Version %s] = = =", pdf.getName(),
                pdf.getVersion()));
        sender.sendMessage(commandDescription("/travel create <name>",
                "Creates a new TravelPort with the passed name. (Name could contain spaces!)"));
        sender.sendMessage(commandDescription("/travel remove [<id>]",
                "Removes the TravelPort, with the passed ID or the TravelPort you are currently staying in."));
        sender.sendMessage(commandDescription("/travel link <id1> [<id2>]", "Links two TravelPorts. If you do not " +
                "pass a second ID it will link with the TravelPort you are currently staying at."));
        sender.sendMessage(commandDescription("/travel unlink [<id>]", "Unlink the TravelPort."));
        sender.sendMessage(commandDescription("/travel info [<id>]", "Shows information about your actual TravelPort" +
                " or the TravelPort with the passed ID!"));
        sender.sendMessage("/travel reload - Reloads the plugin.");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "Notice: Name and ID isn't the same! To get the ID of a TravelPort use " +
                "/travel info");
    }

    /**
     * Called when the player calls the create command.
     *
     * @param player the player which called the command.
     * @param name   the name the player passed for creating the port.
     */
    public synchronized void onCreateCommand(Player player, String name) {
        Selection playerSelection = plugin.getSelection(player);

        if (playerSelection != null && playerSelection.getArea() > 1) {
            TravelPort port = container.create();
            port.setName(name);
            port.setEdge1(playerSelection.getMinimumPoint());
            port.setEdge2(playerSelection.getMaximumPoint());

            ChatHelper.sendMessage(player, Messages.portCreatedSuccessfully);
        } else {
            ChatHelper.sendMessage(player, Messages.needToSelectArea);
        }
    }

    /**
     * Called when the player calls the link command with one parameters.
     *
     * @param player the player which called the command.
     * @param target the target to link.
     */
    public void onLinkCommand(Player player, Integer target) {
        TravelPort currentPlayerPort = plugin.getPlayerInformation(player).getCurrentPort();

        if (currentPlayerPort != null) {
            try {
                TravelPort passedPort = container.get(target);
                try {
                    container.link(currentPlayerPort, passedPort);
                    ChatHelper.sendMessage(player, Messages.linkedSuccessfully);
                } catch (InvalidLinkException exception) {
                    ChatHelper.sendMessage(player, Messages.portAlreadyLinked);
                }
            } catch (InvalidPortIdException exception) {
                ChatHelper.sendMessage(player, Messages.invalidPortId);
            }
        } else {
            ChatHelper.sendMessage(player, Messages.notInsideTravelPort);
        }
    }

    /**
     * Called when the player calls the link command with two parameters.
     *
     * @param player the player which called the command.
     * @param first  the first id passed.
     * @param second the second id passed.
     */
    public void onLinkCommand(Player player, Integer first, Integer second) {
        try {
            TravelPort port1 = container.get(first);
            TravelPort port2 = container.get(second);

            try {
                container.link(port1, port2);
                ChatHelper.sendMessage(player, Messages.linkedSuccessfully);
            } catch (InvalidLinkException exception) {
                ChatHelper.sendMessage(player, Messages.portAlreadyLinked);
            }
        } catch (InvalidPortIdException exception) {
            ChatHelper.sendMessage(player, Messages.invalidPortId);
        }
    }

    /**
     * Called when the player calls the unlink command without any parameters.
     *
     * @param player the player which calls the command.
     */
    public void onUnlinkCommand(Player player) {
        TravelPort currentPlayerPort = plugin.getPlayerInformation(player).getCurrentPort();

        if (currentPlayerPort != null) {
            try {
                container.unlink(currentPlayerPort);
                ChatHelper.sendMessage(player, Messages.unlinkedSuccessfully);
            } catch (InvalidLinkException e) {
                ChatHelper.sendMessage(player, Messages.portNotLinked);
            }
        } else {
            ChatHelper.sendMessage(player, Messages.notInsideTravelPort);
        }
    }

    /**
     * Called when the player calls the unlink command without any parameters.
     *
     * @param player the player which calls the command.
     */
    public void onUnlinkCommand(Player player, Integer portId) {
        try {
            container.unlink(container.get(portId));
        } catch (InvalidLinkException e) {
            ChatHelper.sendMessage(player, Messages.portNotLinked);
        }
    }

    /**
     * Called when the player calls the delete command without arguments.
     *
     * @param player the player which called the command.
     */
    public void onRemoveCommand(Player player) {
        TravelPort currentPlayerPort = plugin.getPlayerInformation(player).getCurrentPort();

        if (currentPlayerPort != null) {
            container.remove(currentPlayerPort);
            ChatHelper.sendMessage(player, Messages.removedSuccessfully);
        } else {
            ChatHelper.sendMessage(player, Messages.notInsideTravelPort);
        }
    }

    /**
     * Called when the user called the delete command.
     *
     * @param player the player which called the delete command.
     * @param portId the id of the port.
     */
    public void onRemoveCommand(Player player, Integer portId) {
        try {
            TravelPort port = container.get(portId);
            container.remove(port);
            ChatHelper.sendMessage(player, Messages.removedSuccessfully);
        } catch (InvalidPortIdException exception) {
            ChatHelper.sendMessage(player, Messages.invalidPortId);
        }
    }

    /**
     * Called when the player called the info command.
     *
     * @param player the player which called the info.
     */
    public void onInfoCommand(Player player) {
        TravelPort currentPlayerPort = plugin.getPlayerInformation(player).getCurrentPort();

        if (currentPlayerPort != null) {
            aboutTravelPort(player, currentPlayerPort);
        } else {
            ChatHelper.sendMessage(player, Messages.notInsideTravelPort);
        }
    }

    /**
     * Called when the player called the info command with one argument.
     *
     * @param player the player which called the command.
     * @param portId the id the player passed to the command.
     */
    public void onInfoCommand(Player player, Integer portId) {
        try {
            TravelPort port = container.get(portId);
            aboutTravelPort(player, port);
        } catch (InvalidPortIdException exception) {
            ChatHelper.sendMessage(player, Messages.invalidPortId);
        }
    }

    /**
     * Sends the player some information about the TravelPort.
     *
     * @param player the player to send the information.
     * @param port   the port.
     */
    private void aboutTravelPort(Player player, TravelPort port) {
        player.sendMessage(ChatColor.GREEN + "= = = Information About TravelPort = = =");
        player.sendMessage(String.format("ID: %d", port.getId()));
        player.sendMessage(String.format("Name: %s", port.getName()));
        player.sendMessage(String.format("Linked to ID: %d", port.getTargetId()));
        player.sendMessage(String.format("Price: %.2f", port.getPrice()));
        player.sendMessage(String.format("Password: %s", port.getPassword()));
    }

    /**
     * Creates a string for command description.
     *
     * @param label       the label of the command to describe.
     * @param description the description.
     * @return the created string.
     */
    private String commandDescription(String label, String description) {
        return String.format("%s%s%s - %s", ChatColor.GRAY, label, ChatColor.WHITE, description);
    }
}
