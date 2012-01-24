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

import nl.mpi.metadata.api.MetadataDocument;
import nl.mpi.metadata.api.MetadataElement;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataDocumentListener {

    /**
     * Specified document has been saved
     * @param document 
     */
    void documentSaved(MetadataDocument document);

    /**
     * Specified document has been deleted
     * @param document 
     */
    void documentDeleted(MetadataDocument document);

    /**
     * Specified element has been inserted into the specified document
     * @param document
     * @param insertedElement 
     */
    void elementInserted(MetadataDocument document, MetadataElement insertedElement);

    /**
     * Specified element has been removed from the specified document
     * @param document
     * @param removedElement 
     */
    void elementRemoved(MetadataDocument document, MetadataElement removedElement);
}
