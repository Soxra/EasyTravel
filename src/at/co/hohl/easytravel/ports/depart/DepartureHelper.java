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

package at.co.hohl.easytravel.ports.depart;

import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.utils.BukkitTime;
import at.co.hohl.utils.storage.SyntaxException;

import java.util.LinkedList;
import java.util.List;

/**
 * Static helper class for loading and saving Departures.
 *
 * @author Michael Hohl
 */
public final class DepartureHelper {
    /**
     * Loads the Departure for the passed TravelPort.
     *
     * @param port        the port to load the Departure for.
     * @param description the description of the Departure.
     * @return the loaded departure.
     * @throws SyntaxException invalid format of description.
     */
    public static Departure load(TravelPort port, String description) throws SyntaxException {
        description = description.trim();
        if ("MANUAL".equalsIgnoreCase(description)) {
            return new ManualDeparture(port);
        } else if (description.toLowerCase().startsWith("every")) {
            BukkitTime interval = new BukkitTime(description.substring("every".length()).trim());
            return new IntervalScheduledDeparture(port, interval);
        } else {
            String[] times = description.split(",");
            List<BukkitTime> timeList = new LinkedList<BukkitTime>();
            for (String time : times) {
                try {
                    timeList.add(new BukkitTime(time));
                } catch (SyntaxException e) {
                    System.out.printf("[EasyTravel] Invalid time description: '%s'!", time);
                }
            }
            return new TimeScheduledDeparture(port, timeList);
        }
    }

    /**
     * No need of creating an instance of that!
     */
    private DepartureHelper() {
    }
}
