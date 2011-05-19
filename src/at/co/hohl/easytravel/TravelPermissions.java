/*
 * EasyTravel
 * Copyright (C) 2011 Michael Hohl <http://www.hohl.co.at/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
    public final static Permission PORT = new Permission("easytravel.port", true);

    /** The Permission manage all existing TravelPorts. */
    public final static Permission MODERATE = new Permission("easytravel.moderate", true);

    /** The Permission needed to force saving or reload the plugin. */
    public final static Permission ADMINISTRATE = new Permission("easytravel.administrate", true);

    /** Hidden constructor. */
    private TravelPermissions() {
    }
}
