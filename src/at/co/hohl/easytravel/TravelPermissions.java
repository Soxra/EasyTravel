package at.co.hohl.easytravel;

import at.co.hohl.Permissions.Permission;

/**
 * Static holder for permissions.
 *
 * @author Michael Hohl
 */
public final class TravelPermissions {
    /** The Permission needed to depart. */
    public final static Permission DEPART_PERMISSION = new Permission("easytravel.depart", false);

    /** The Permission needed to create new TravelPorts. */
    public final static Permission MODERATE_PERMISSION = new Permission("easytravel.moderate", true);
}
