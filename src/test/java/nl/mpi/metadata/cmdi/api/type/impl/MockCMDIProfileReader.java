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
package nl.mpi.metadata.cmdi.api.type.impl;

import java.io.IOException;
import java.net.URI;
import nl.mpi.metadata.api.MetadataTypeException;
import nl.mpi.metadata.api.type.MetadataDocumentTypeReader;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MockCMDIProfileReader implements MetadataDocumentTypeReader<CMDIProfile> {
    
    private CMDIProfile profile;
    private URI uri;

    public MockCMDIProfileReader(CMDIProfile profile) {
	this.profile = profile;
    }

    public CMDIProfile read(URI uri) throws IOException, MetadataTypeException {
	this.uri = uri;
	return profile;
    }

    /**
     * @return the uri
     */
    public URI getUri() {
	return uri;
    }
}
