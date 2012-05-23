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

import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.api.type.MetadataElementType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataElement {

    /**
     *
     * @return Name of this element in the document
     */
    String getName();

    /**
     *
     * @return Text that should be displayed as value for this element
     */
    String getDisplayValue();

    /**
     *
     * @return Type of this element
     */
    MetadataElementType getType();

    /**
     *
     * @return Document that this element is part of
     */
    MetadataDocument getMetadataDocument();

    /**
     * Registers a {@link MetadataElementListener} for this element
     *
     * @param listener Listener to add
     */
    void addMetadataElementListener(MetadataElementListener listener);

    /**
     * Unregisters a {@link MetadataElementListener} from this element
     *
     * @param listener Listener to remove
     */
    void removeMetadataElementListener(MetadataElementListener listener);

    /**
     *
     * @return XPath to this metadata element <em>from the root of the document</em>
     */
    String getPathString();
}
