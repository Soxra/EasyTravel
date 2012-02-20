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

package at.co.hohl.utils;

import at.co.hohl.utils.storage.SyntaxException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents the bukkit time as datatype.
 *
 * @author Michael
 */
public class BukkitTime implements Comparable<BukkitTime> {
    /**
     * DateFormatter for 24h da
     */
    private final DateFormat dayTimeFormat = new SimpleDateFormat("h:mm a");

    /**
     * The ticks, represented by the time.
     */
    private final long ticks;

    /**
     * Creates a new BukkitTime with the passed ticks.
     *
     * @param ticks the ticks of the time.
     */
    public BukkitTime(long ticks) {
        this.ticks = ticks;
    }

    /**
     * Creates a new BukkitTime with the passed string.
     *
     * @param time the string which describes the time.
     * @throws SyntaxException called when the description string is invalid.
     */
    public BukkitTime(String time) throws SyntaxException {
        long ticks = 0;
        String[] parts = time.split(" ");

        for (String part : parts) {
            if (part.matches("^[0-9]+[d|h|m|t]$")) {
                long value = Long.valueOf(part.substring(0, part.length() - 1));

                if (part.endsWith("d")) {
                    ticks += value * 24000;
                } else if (part.endsWith("h")) {
                    ticks += value * 1000;
                } else if (part.endsWith("m")) {
                    ticks += value * 60 / 1000;
                } else if (part.endsWith("t")) {
                    ticks += value;
                }
            } else if (part.matches("^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])$")) {
                String[] splittedPart = part.split(":");
                ticks += Long.parseLong(splittedPart[0]) * 1000;
                ticks += Long.parseLong(splittedPart[1]) * 60;
            } else if (part.matches("^[0-9]+$")) {
                ticks += Long.parseLong(part);
            } else {
                throw new SyntaxException(String.format("'%s' is not a valid time description!", time));
            }
        }

        this.ticks = ticks;
    }

    /**
     * @return the ticks of the time.
     */
    public long getTicks() {
        return ticks;
    }

    /**
     * @return the seconds of the time.
     */
    public long getSeconds() {
        return ticks * 36 / 10;
    }

    /**
     * @return the seconds of the time.
     */
    public long getMinutes() {
        return ticks * 60 / 1000;
    }

    /**
     * @return the hours of the time.
     */
    public long getHours() {
        return ticks / 1000;
    }

    /**
     * @return the days of the time.
     */
    public long getDays() {
        return ticks / 24000;
    }

    /**
     * @return formatted string with 24 hours day time.
     */
    public String getDayTime12() {
        Date dateTime = new Date();
        dateTime.setMinutes((int) getMinutes() % 60);
        dateTime.setHours((int) getHours() % 24);
        return dayTimeFormat.format(dateTime);
    }

    @Override
    public int compareTo(BukkitTime bukkitTime) {
        if (ticks > bukkitTime.ticks) {
            return 1;
        } else if (ticks < bukkitTime.ticks) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("%dd %dh %dm", getDays(), getHours() % 24, getMinutes() % 60);
    }
}
