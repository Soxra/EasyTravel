package at.co.hohl.easytravel.messages;

import at.co.hohl.utils.ChatHelper;
import org.bukkit.entity.Player;

/**
 * Represents a NPC which could speak to the player.
 *
 * @author Michael Hohl
 */
public class NpcSpeaker {
    /** The name of the NPC. */
    private final String name;

    /**
     * Creates a new NpcSpeaker.
     *
     * @param name the name of the npc.
     */
    public NpcSpeaker(String name) {
        this.name = name;
    }

    /**
     * Lets the NPC say something to the user.
     *
     * @param player  the player which should receive the spoken text.
     * @param message the message you want to let the NPC say.
     */
    public void say(Player player, String message) {
        player.sendMessage(ChatHelper.replaceColorCodes(String.format("%s: %s", name, message)));
    }

    /** @return the name of the NPC. */
    public String getName() {
        return name;
    }
}
