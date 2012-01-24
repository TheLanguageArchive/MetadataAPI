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
package nl.mpi.metadata.api.events;

import nl.mpi.metadata.api.model.MetadataElement;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataElementListener {

    /**
     * A new element has been inserted in the specified parent element
     * @param parentElement Parent element
     * @param insertedElement Newly inserted child element
     */
    void elementInserted(MetadataElement parentElement, MetadataElement insertedElement);

    /**
     * A new element has been removed from the specified parent element
     * @param parentElement Parent element
     * @param insertedElement Removed child element
     */
    void elementRemoved(MetadataElement parentElement, MetadataElement removedElement);

    /**
     * The value of the specified element has been changed
     * @param element 
     */
    void valueChanged(MetadataElement element);
}
