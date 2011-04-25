package at.co.hohl.economy;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;

/**
 * Handler for the iConomy system.
 *
 * @author Michael Hohl
 */
public class iConomyHandler implements EconomyHandler {
    /** Creates a new iConomyHandler. */
    public iConomyHandler() {
    }

    /** @return the currency of the economy system. */
    public final String getCurrency() {
        return iConomy.getBank().getCurrency();
    }

    /**
     * Returns the balance of the account of the passed account.
     *
     * @param account the account.
     * @return the amount of money on his account.
     */
    public final double getBalance(String account) {
        Account playersAccount = iConomy.getBank().getAccount(account);

        if (playersAccount != null) {
            return playersAccount.getBalance();
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
        Account playersAccount = iConomy.getBank().getAccount(account);

        if (playersAccount != null) {
            if (playersAccount.getBalance() >= amount) {
                playersAccount.subtract(amount);
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
        Account playersAccount = iConomy.getBank().getAccount(account);

        if (playersAccount != null) {
            playersAccount.add(amount);
        }
    }
}
