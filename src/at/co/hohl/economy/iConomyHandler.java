package at.co.hohl.economy;


import com.iConomy.iConomy;
import com.iConomy.system.Account;

/**
 * Handler for the iConomy system.
 *
 * @author Michael Hohl
 */
public class iConomyHandler implements EconomyHandler {
    /** Creates a new iConomyHandler. */
    public iConomyHandler() {
    }

    /**
     * Formats the passed amount to a string with the currency. For example passing 20.0 will return a "20.0 Coins" or
     * "20.0 $" depending on server config.
     *
     * @param amount the amount of money.
     * @return the formatted string.
     */
    public final String format(double amount) {
        return iConomy.format(amount);
    }

    /**
     * Returns the balance of the account of the passed account.
     *
     * @param account the account.
     * @return the amount of money on his account.
     */
    public final double getBalance(String account) {
        Account playersAccount = iConomy.getAccount(account);

        if (playersAccount != null) {
            return playersAccount.getHoldings().balance();
        } else {
            return 0;
        }
    }

    /**
     * Opposite of grant, means the account loses an amount of money.
     *
     * @param account the account.
     * @param amount  the amount of money to pay.
     * @return false, if the account has not enough money.
     */
    public final boolean pay(String account, double amount) {
        Account playersAccount = iConomy.getAccount(account);

        if (playersAccount != null) {
            if (playersAccount.getHoldings().hasEnough(amount)) {
                playersAccount.getHoldings().subtract(amount);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Grants an user the amount of money.
     *
     * @param account the account.
     * @param amount  the amount of money to grant.
     */
    public final void grant(String account, double amount) {
        Account playersAccount = iConomy.getAccount(account);

        if (playersAccount != null) {
            playersAccount.getHoldings().add(amount);
        }
    }
}
