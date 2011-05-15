package at.co.hohl.easytravel.listener;

import at.co.hohl.easytravel.TravelPlugin;
import com.nijikokun.register.payment.Methods;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

/**
 * ServerListener which looks for economy plugins.
 *
 * @author Michael Hohl
 */
public class EconomyPluginListener extends ServerListener {
    /** Plugin instance which holds the Register Library. */
    private TravelPlugin plugin;

    /**
     * Creates a new EconomyPluginListener.
     *
     * @param plugin the plugin which holds the instance.
     */
    public EconomyPluginListener(TravelPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when a plugin gets disabled.
     *
     * @param event the occurred event.
     */
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        // Check to see if the plugin that's being disabled is the one we are using
        Methods methods = plugin.getMethods();
        if (methods != null && methods.hasMethod()) {
            Boolean check = methods.checkDisabled(event.getPlugin());

            if (check) {
                plugin.getLogger().info(String.format("[%s] Payment method was disabled. No longer accepting payments.",
                        plugin.getDescription().getName()));
            }
        }
    }

    /**
     * Called when a plugin gets enabled.
     *
     * @param event the occurred event.
     */
    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        // Create new methods if there didn't exists anyone.
        Methods methods = plugin.getMethods();

        // Check to see if we need a payment method
        if (!methods.hasMethod()) {
            if (methods.setMethod(event.getPlugin())) {
                plugin.getLogger().info(
                        String.format("[%s] Payment method found (%s %s)", plugin.getDescription().getName(),
                                methods.getMethod().getName(), methods.getMethod().getVersion()));
            }
        }
    }
}
