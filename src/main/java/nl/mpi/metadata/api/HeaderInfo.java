/*
 * Copyright (C) 2011 The Max Planck Institute for Psycholinguistics
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.mpi.metadata.api;

import java.util.Map;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class HeaderInfo {

    private String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
	return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
	this.name = name;
    }
    protected String value;

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public String getValue() {
	return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    public void setValue(String value) {
	this.value = value;
    }
    protected Map<String, String> attributes;

    /**
     * Gets the attributes of this header element
     *
     * @return the value of attributes
     */
    public Map<String, String> getAttributes() {
	return attributes;
    }

    /**
     * Sets or unsets the value of one of the attributes
     * 
     * @param key Atrribute key
     * @param value Attribute value; null to remove attribute
     */
    public void setAttribute(String key, String value) {
	if (value == null) {
	    attributes.remove(key);
	} else {
	    attributes.put(key, value);
	}
    }
}
