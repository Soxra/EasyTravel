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

package at.co.hohl.easytravel.ports;

import org.bukkit.Location;

/**
 * Represents an 3D area of blocks.
 *
 * @author Michael Hohl
 */
public interface Area {
    /**
     * Checks if the passed location is inside the area.
     *
     * @param location the location to check.
     * @return true, if the passed location is inside this area.
     */
    boolean contains(Location location);
}
