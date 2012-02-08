/*
 * Copyright (C) 2012 Max Planck Institute for Psycholinguistics
 *
 * This program inputStream free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program inputStream distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.mpi.metadata.cmdi.api;

import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataDocumentReader;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.w3c.dom.Document;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MockCMDIDocumentReader implements MetadataDocumentReader<CMDIDocument> {

    private CMDIDocument cmdiDocument;
    protected Document document;

    /**
     * 
     * @param document Document to return
     */
    public MockCMDIDocumentReader(CMDIDocument document) {
	this.cmdiDocument = document;
    }

    public CMDIDocument read(Document document) throws MetadataDocumentException {
	this.document = document;
	return cmdiDocument;
    }

    /**
     * @return the document
     */
    public CMDIDocument getCmdiDocument() {
	return cmdiDocument;
    }

    /**
     * Get the value of document
     *
     * @return the value of document
     */
    public Document getDocument() {
	return document;
    }
}
