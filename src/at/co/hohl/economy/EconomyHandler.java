package at.co.hohl.economy;

/**
 * Represents an interface for an Economy System, like iConomy or BEOSconomy.
 *
 * @author Michael Hohl
 */
public interface EconomyHandler {
    /** @return the currency of the economy system. */
    String getCurrency();

    /**
     * Returns the balance of the account of the passed account.
     *
     * @param account the account.
     * @return the amount of money on his account.
     */
    double getBalance(String account);

    /**
     * Opposite of grant, means the account loses an amount of money.
     *
     * @param account the account.
     * @param amount  the amount of money to pay.
     * @return false, if the account doesn't have enough money to pay.
     */
    boolean pay(String account, double amount);

    /**
     * Grants an user the amount of money.
     *
     * @param account the account.
     * @param amount  the amount of money to grant.
     */
    void grant(String account, double amount);
}
