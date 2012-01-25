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

import java.io.IOException;
import java.io.InputStream;
import nl.mpi.metadata.api.MetadataDocumentReader;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MockCMDIDocumentReader implements MetadataDocumentReader<CMDIDocument> {

    private CMDIDocument document;
    private InputStream inputStream;

    /**
     * 
     * @param document Document to return
     */
    public MockCMDIDocumentReader(CMDIDocument document) {
	this.document = document;
    }

    /**
     * 
     * @param inputStream
     * @return Always the document passed into the constructor
     * @throws IOException 
     */
    public CMDIDocument read(InputStream inputStream) {
	this.inputStream = inputStream;
	return document;
    }

    /**
     * @return the document
     */
    public CMDIDocument getDocument() {
	return document;
    }

    /**
     * @return the inputStream
     */
    public InputStream getInputStream() {
	return inputStream;
    }
}
