package at.co.hohl.economy;

import org.bukkit.entity.Player;

/**
 * Represents an interface for an Economy System, like iConomy or BEOSconomy.
 *
 * @author Michael Hohl
 */
public interface EconomyHandler {
    /** @return the currency of the economy system. */
    String getCurrency();

    /**
     * Returns the balance of the account of the passed player.
     *
     * @param player the player.
     * @return the amount of money on his account.
     */
    double getBalance(Player player);

    /**
     * Opposite of grant, means the player loses an amount of money.
     *
     * @param player the player.
     * @param amount the amount of money to pay.
     * @return false, if the player doesn't have enough money to pay.
     */
    boolean pay(Player player, double amount);

    /**
     * Grants an user the amount of money.
     *
     * @param player the player.
     * @param amount the amount of money to grant.
     */
    void grant(Player player, double amount);
}
