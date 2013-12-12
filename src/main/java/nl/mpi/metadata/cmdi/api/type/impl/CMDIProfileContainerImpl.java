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
package nl.mpi.metadata.cmdi.api.type.impl;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import nl.mpi.metadata.api.MetadataTypeException;
import nl.mpi.metadata.api.type.MetadataDocumentTypeReader;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;

/**
 * Class for chaching CMDI profiles
 * @see CMDIProfileImpl
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileContainerImpl implements CMDIProfileContainer {

    private final Map<URI, CMDIProfile> profileMap;
    private final MetadataDocumentTypeReader<CMDIProfile> profileReader;

    /**
     * Creates a profile container with no entityresolver set. In this implementation, {@link #getProfileReader() } 
     * will instantiate a new instance of {@link CMDIProfileReader} on first request.
     */
    public CMDIProfileContainerImpl(MetadataDocumentTypeReader<CMDIProfile> profileReader) {
	this.profileReader = profileReader;
	this.profileMap = new HashMap<URI, CMDIProfile>();
    }

    @Override
    public synchronized CMDIProfile getProfile(URI profileUri) throws IOException, MetadataTypeException {
	CMDIProfile profile = profileMap.get(profileUri);
	if (profile == null) {
	    profile = profileReader.read(profileUri);
	    profileMap.put(profileUri, profile);
	}
	return profile;
    }

    @Override
    public synchronized boolean containsProfile(URI profileUri) {
	return profileMap.containsKey(profileUri);
    }

    @Override
    public synchronized boolean containsProfile(CMDIProfile profile) {
	return profileMap.containsValue(profile);
    }
}
