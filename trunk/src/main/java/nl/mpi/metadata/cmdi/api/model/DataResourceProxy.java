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
import java.net.URL;
import nl.mpi.metadata.api.model.ResourceReference;
import nl.mpi.metadata.cmdi.api.CMDIConstants;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class DataResourceProxy extends ResourceProxy implements ResourceReference {

    /**
     * Constructor for a resource proxy with a custom
     * {@link #getType()  proxy type} and no local location
     *
     * @param id Unique identifier for the resource proxy
     * @param uri URI of the referenced resource
     * @param type a string representation of the type of this resource, usually
     * 'Resource'
     * @param mimeType mime type of the referenced resource
     */
    public DataResourceProxy(String id, URI uri, String type, String mimeType) {
        super(id, uri, type, mimeType);
    }

    /**
     * Constructor for a resource proxy with a custom
     * {@link #getType()  proxy type}
     *
     * @param id Unique identifier for the resource proxy
     * @param uri URI of the referenced resource
     * @param url Local location of the resource
     * @param type a string representation of the type of this resource, usually
     * 'Resource'
     * @param mimeType mime type of the referenced resource
     */
    public DataResourceProxy(String id, URI uri, URL url, String type, String mimeType) {
        super(id, uri, url, type, mimeType);
    }

    /**
     * Constructor for a resource proxy with {@link #getType() type} set to
     * {@link CMDIConstants#CMD_RESOURCE_PROXY_TYPE_RESOURCE Resource} and no
     * local location
     *
     * @param id Unique identifier for the resource proxy
     * @param uri URI of the referenced resource
     * @param mimeType mime type of the referenced resource
     */
    public DataResourceProxy(String id, URI uri, String mimeType) {
        super(id, uri, CMDIConstants.CMD_RESOURCE_PROXY_TYPE_RESOURCE, mimeType);
    }

    /**
     * Constructor for a resource proxy with {@link #getType() type} set to
     * {@link CMDIConstants#CMD_RESOURCE_PROXY_TYPE_RESOURCE Resource}
     *
     * @param id Unique identifier for the resource proxy
     * @param uri URI of the referenced resource
     * @param url Local location of the resource
     * @param mimeType mime type of the referenced resource
     */
    public DataResourceProxy(String id, URI uri, URL url, String mimeType) {
        super(id, uri, url, CMDIConstants.CMD_RESOURCE_PROXY_TYPE_RESOURCE, mimeType);
    }
}
