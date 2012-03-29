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

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.model.MetadataDocument;

/**
 * Takes a metadata document and writes it to an output stream
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataDocumentWriter<T extends MetadataDocument> {

    /**
     * 
     * @param metadataDocument document to write to result
     * @param outputResult XML transformation result to write to
     * @throws TransformerException if an exception occurs during the transformation process
     * @throws MetadataException if an exception occurs while processing the metadata document
     */
    void write(T metadataDocument, Result outputResult) throws  MetadataDocumentException, TransformerException;
}
