package at.co.hohl.easytravel.messages;

/**
 * Static container for the localized strings. Sadly, Bukkit doesn't support ResourceBundle inside the data directory.
 *
 * @author Michael Hohl
 */
public final class Messages {
    // General
    public static String messageFormat = "&d%s";

    public static String defaultSpeakerName = "Captain";

    // DEPART
    public static String speakerSayGreetingFree = "Welcome on board! Use /depart to begin your journey.";

    public static String speakerSayGreetingPaid =
            "Welcome on board! This journey costs &a%.2f %s&e. Use /depart to begin your journey.";

    public static String speakerSayNotEnoughMoney = "&cYou haven't enough money for this journey!";

    public static String onArriveTarget = "&dYou arrived at &a%s&d!";

    public static String onMoneyPayed = "You have been charged &a%.2f %s&d for this trip!";

    public static String economyNotFound = "&cSorry. You can't begin your journey, because of technical problems.";

    public static String portHasNoTarget = "&cThe port doesn't have a target! Link ports, before using /depart.";

    public static String missDepartPermission = "&cYou are not allowed to depart!";

    public static String notInsideTravelPort = "&cYou can only depart, when you are on board!";

    public static String internalError = "&cInternal Error occurred! Please contact an Administrator!";

    // TRAVEL
    public static String onPortCreatedSuccessfully = "&2Port created successfully!";

    public static String onLinkedSuccessfully = "&2Ports linked successfully!";

    public static String onUnlinkedSuccessfully = "&2Port unlinked successfully!";

    public static String onRemovedSuccessfully = "&2Port removed successfully!";

    public static String needToPassNameForPort = "&cYou need to pass a name, when creating a TravelPort!";

    public static String needToSelectArea = "&cYou need to select an area with you WorldEdit wand!";

    public static String needToStayInsidePort =
            "&cYou are not inside a TravelPort!";

    public static String portAlreadyLinked = "&cAt least one of the port is already linked! Do unlink before!";

    public static String portNotLinked = "&cPort isn't linked to another!";

    public static String invalidUseOfCommand = "&cInvalid use of this command! Use /travel help for information how to use it.";

    public static String invalidPortId = "&cPassed ID isn't a valid port id!";
}
