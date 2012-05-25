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
package nl.mpi.metadata.cmdi.api.type;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import nl.mpi.metadata.api.MetadataTypeException;
import nl.mpi.metadata.api.type.MetadataDocumentTypeReader;

/**
 * Class for chaching CMDI profiles
 * @see CMDIProfile
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileContainer {

    private Map<URI, CMDIProfile> profileMap;
    private MetadataDocumentTypeReader<CMDIProfile> profileReader;

    /**
     * Creates a profile container with no entityresolver or profileReader set. In this implementation {@link #getProfileReader() () } 
     * will instantiate a new instance of {@link CMDIProfileReader} on first request.
     */
    public CMDIProfileContainer() {
	profileMap = new HashMap<URI, CMDIProfile>();
    }

    /**
     * Creates a profile container with no entityresolver set. In this implementation, {@link #getProfileReader() } 
     * will instantiate a new instance of {@link CMDIProfileReader} on first request.
     */
    public CMDIProfileContainer(MetadataDocumentTypeReader<CMDIProfile> profileReader) {
	this.profileReader = profileReader;
	this.profileMap = new HashMap<URI, CMDIProfile>();
    }

    public synchronized CMDIProfile getProfile(URI profileUri) throws IOException, MetadataTypeException {
	CMDIProfile profile = profileMap.get(profileUri);
	if (profile == null) {
	    profile = getProfileReader().read(profileUri);
	    profileMap.put(profileUri, profile);
	}
	return profile;
    }

    public synchronized boolean containsProfile(URI profileUri) {
	return profileMap.containsKey(profileUri);
    }

    public synchronized boolean containsProfile(CMDIProfile profile) {
	return profileMap.containsValue(profile);
    }

    public synchronized MetadataDocumentTypeReader<CMDIProfile> getProfileReader() {
	if (profileReader == null) {
	    profileReader = new CMDIProfileReader();
	}
	return profileReader;
    }
}
