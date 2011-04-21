package at.co.hohl.easytravel;

/**
 * Static container for the localized strings. Sadly, Bukkit doesn't support ResourceBundle inside the data directory.
 *
 * @author Michael Hohl
 */
public final class LocalizedStrings {
    // General
    public static String MESSAGE_FORMAT = "&d%s";

    // DEPART
    public static String NO_DEPART_PERMISSIONS = "&cYou are not allowed to depart!";

    public static String NOT_INSIDE_TRAVEL_PORT = "&cYou can only depart, when you are on board!";

    public static String ON_BOARD_GREETING_FREE = "&eWelcome on board! Use /depart to begin your journey.";

    public static String ON_BOARD_GREETING_PAID =
            "&eWelcome on board! This journey costs &a%d%s&e. Use /depart to begin your journey.";

    public static String ARRIVING_AT_TARGET = "Welcome in %s!";

    public static String MONEY_PAYED = "You have been charged &a%d%s&d for this trip!";

    public static String NOT_ENOUGH_MONEY = "&cYou haven't enough money for begin your journey!";

    public static String PROBLEM_WITH_ECONOMY =
            "&cSorry. You can't begin your journey, because of technical problems.";

    public static String PORT_HAS_NO_TARGET = "&cThe port doesn't have a target! Link to ports, before using /depart.";

    // TRAVEL
    public static String INVALID_USE = "&cInvalid use of this command! Use /travel help for information how to use it.";

    public static String NEED_TO_PASS_NAME = "&cYou need to pass a name, when creating a TravelPort!";

    public static String NEED_TO_SELECT = "&cYou need to select an area with you WorldEdit wand!";

    public static String PORT_CREATED_SUCCESSFULLY = "&2Port created successfully!";

    public static String NEED_TO_STAY_INSIDE_PORT =
            "&cYou are not inside a TravelPort!";

    public static String LINKED_SUCCESSFULLY = "&2Ports linked successfully!";

    public static String UNLINKED_SUCCESSFULLY = "&2Port unlinked successfully!";

    public static String REMOVED_SUCCESSFULLY = "&2Port removed successfully!";

    public static String ALREADY_LINKED = "&cAt least one of the port is already linked! Do unlink before!";

    public static String NOT_LINKED = "&cPort isn't linked to another!";

    public static String INVALID_PORT = "&cInvlaid Port ID!";
}
