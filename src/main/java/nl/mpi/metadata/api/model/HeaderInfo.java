/*
 * Copyright (C) 2011 Max Planck Institute for Psycholinguistics
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
package nl.mpi.metadata.api.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable class representing header info
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public final class HeaderInfo<V> {

    private final String name;
    private final V value;
    private final Map<String, String> attributes;

    public HeaderInfo(String name, V value) {
	this(name, value, null);
    }

    public HeaderInfo(String name, V value, Map<String, String> attributes) {
	this.name = name;
	this.value = value;
	if (attributes == null) {
	    this.attributes = null;
	} else {
	    this.attributes = new HashMap<String, String>(attributes);
	}
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
	return name;
    }

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public V getValue() {
	return value;
    }

    /**
     * Gets an <em>unmodifiable</em> copy of the attributes map for the present header info
     * @return attributes the attributes that have been set for the present header info. An empty map if none are set. Never null.
     */
    public Map<String, String> getAttributes() {
	if (attributes == null) {
	    return Collections.emptyMap();
	} else {
	    return Collections.unmodifiableMap(attributes);
	}
    }

    /**
     * Gets the attributes of this header element associated with the provided key
     *
     * @return the value of attributes
     */
    public String getAttribute(String key) {
	if (attributes == null) {
	    return null;
	}
	return attributes.get(key);
    }
}
