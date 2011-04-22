package at.co.hohl.easytravel.messages;

import at.co.hohl.utils.ChatHelper;
import org.bukkit.entity.Player;

/**
 * Represents a NPC which could speak to the player.
 *
 * @author Michael Hohl
 */
public class Speaker {
    /** The name of the NPC. */
    private final String name;

    /**
     * Creates a new Speaker.
     *
     * @param name the name of the npc.
     */
    public Speaker(String name) {
        this.name = name;
    }

    /**
     * Lets the NPC say something to the user.
     *
     * @param player  the player which should receive the spoken text.
     * @param message the message you want to let the NPC say.
     */
    public final void say(Player player, String message) {
        player.sendMessage(ChatHelper.replaceColorCodes(String.format("%s: %s", name, message)));
    }

    /** @return the name of the NPC. */
    public final String getName() {
        return name;
    }
}
