/*
 * Copyright (C) 2012 Max Planck Institute for Psycholinguistics
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

import java.util.Collection;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataElementAttributeContainer<A extends MetadataElementAttribute> {

    /**
     * Adds an attribute to the element's attribute set
     * @param attribute attribute to add
     * @return Whether the attribute has been added to the attribute set. If false, it was already in the set.
     */
    boolean addAttribute(A attribute);

    /**
     * Removes an attribute from the element's attribute set
     * @param attribute attribute to remove
     * @return Whether the attribute has been removed from the attribute set. If false, it was not in the set.
     */
    boolean removeAttribute(A attribute);

    /**
     * 
     * @return Collection of attributes this element has. Implementations should return an unmodifiable copy.
     */
    Collection<A> getAttributes();
}
