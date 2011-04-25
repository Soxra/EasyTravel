package at.co.hohl.easytravel;

import at.co.hohl.Permissions.Permission;

/**
 * Static holder for permissions.
 *
 * @author Michael Hohl
 */
public final class TravelPermissions {
    /** The Permission needed to depart. */
    public final static Permission DEPART = new Permission("easytravel.depart", false);

    /** The Permission needed to create new TravelPorts. */
    public final static Permission CREATE = new Permission("easytravel.create", true);

    /** The Permission needed to get info about TravelPorts. */
    public final static Permission INFO = new Permission("easytravel.info", true);

    /** The Permission needed to get info about TravelPorts. */
    public final static Permission LIST = new Permission("easytravel.list", true);

    /** The Permission needed to point the compass TravelPorts. */
    public final static Permission COMPASS = new Permission("easytravel.compass", true);

    /** The Permission needed to warp to TravelPorts. */
    public final static Permission WARP = new Permission("easytravel.warp", true);

    /** The Permission manage all existing TravelPorts. */
    public final static Permission MODERATE = new Permission("easytravel.moderate", true);

    /** The Permission needed to force saving or reload the plugin. */
    public final static Permission ADMINISTRATE = new Permission("easytravel.administrate", true);

    /** Hidden constructor. */
    private TravelPermissions() {
    }
}
