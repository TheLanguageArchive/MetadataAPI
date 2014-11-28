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
package nl.mpi.metadata.cmdi.api.model;

import java.net.URI;
import nl.mpi.metadata.api.model.MetadataReference;
import nl.mpi.metadata.cmdi.api.CMDIConstants;

/**
 * A resource proxy of the metadata type. Calling {@link #getType() } will alway
 * return the value of {@link CMDIConstants#CMD_RESOURCE_PROXY_TYPE_METADATA}.
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MetadataResourceProxy extends ResourceProxy implements MetadataReference {

    public MetadataResourceProxy(String id, URI uri, String mimeType) {
        super(id, uri, CMDIConstants.CMD_RESOURCE_PROXY_TYPE_METADATA, mimeType);
    }

    public MetadataResourceProxy(String id, URI uri, URI location, String mimeType) {
        super(id, uri, location, CMDIConstants.CMD_RESOURCE_PROXY_TYPE_METADATA, mimeType);
    }
}
