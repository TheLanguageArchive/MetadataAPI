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
package nl.mpi.metadata.api.dom;

import java.io.IOException;
import java.io.OutputStream;
import nl.mpi.metadata.api.model.MetadataDocument;

/**
 * Takes a metadata document and writes it to an output stream
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataDocumentWriter<T extends MetadataDocument> {

    /**
     * 
     * @param metadataDocument document to write to stream
     * @param outputStream stream to write to
     * @throws IOException if I/O error occurred while writing to stream
     */
    void write(T metadataDocument, OutputStream outputStream) throws IOException;
}
