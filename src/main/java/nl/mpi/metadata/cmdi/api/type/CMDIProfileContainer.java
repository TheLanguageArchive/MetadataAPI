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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.xml.sax.EntityResolver;

/**
 * Class for chaching CMDI profiles
 * @see CMDIProfile
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileContainer {

    private Map<URI, CMDIProfile> profileMap;
    private EntityResolver entityResolver;

    /**
     * Creates a profile container with no entityresolver set. In this implementation, {@link #getEntityResolver() } 
     * will instantiate a new instance of {@link CMDIEntityResolver} on first request.
     */
    public CMDIProfileContainer() {
	profileMap = new HashMap<URI, CMDIProfile>();
    }

    public CMDIProfileContainer(EntityResolver entityResolver) {
	this();
	this.entityResolver = entityResolver;
    }

    public synchronized CMDIProfile getProfile(URI profileUri) throws IOException, CMDITypeException {
	CMDIProfile profile = profileMap.get(profileUri);
	if (profile == null) {
	    profile = new CMDIProfile(profileUri, getEntityResolver());
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

    /**
     * 
     * @return an instance of {@link CMDIEntityResolver}
     */
    public synchronized EntityResolver getEntityResolver() {
	if (entityResolver == null) {
	    entityResolver = new CMDIEntityResolver();
	}
	return entityResolver;
    }
}
