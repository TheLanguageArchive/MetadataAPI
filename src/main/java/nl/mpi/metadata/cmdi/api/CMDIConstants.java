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
package nl.mpi.metadata.cmdi.api;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public final class CMDIConstants {

    public final static String CMD_NAMESPACE = "http://www.clarin.eu/cmd/";
    public final static String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String CMD_RESOURCE_PROXY_LOCATION_ATTRIBUTE_NAMESPACE = "http://lat.mpi.nl/";
    
    /**
     * XPath to CMDI components
     */
    public static final String CMD_COMPONENTS_PATH = "/cmd:CMD/cmd:Components";
    public static final String CMD_COMPONENTS_NODE_NAME = "Components";
    /**
     * XPath to resource proxy list
     */
    public static final String CMD_RESOURCE_PROXY_LIST_PATH = "/cmd:CMD/cmd:Resources/cmd:ResourceProxyList";
    /**
     * XPath to resource proxy nodes
     */
    public final static String CMD_RESOURCE_PROXIES_PATH = "/cmd:CMD/cmd:Resources/cmd:ResourceProxyList/cmd:ResourceProxy";
    public final static String CMD_RESOURCE_PROXY_ELEMENT = "ResourceProxy";
    public final static String CMD_RESOURCE_PROXY_TYPE_ELEMENT = "ResourceType";
    public final static String CMD_RESOURCE_PROXY_REF_ELEMENT = "ResourceRef";
    public final static String CMD_RESOURCE_PROXY_LOCATION_ATTRIBUTE_PREFIX = "lat";
    public final static String CMD_RESOURCE_PROXY_LOCATION_ATTRIBUTE_NAME = "localURI";
    public final static String CMD_RESOURCE_PROXY_ID_ATTRIBUTE = "id";
    public final static String CMD_RESOURCE_PROXY_TYPE_METADATA = "Metadata";
    public final static String CMD_RESOURCE_PROXY_TYPE_RESOURCE = "Resource";
    public final static String CMD_RESOURCE_PROXY_TYPE_MIMETYPE_ATTRIBUTE = "mimetype";
    public final static String CMD_RESOURCE_PROXY_REFERENCE_ATTRIBUTE = "ref";
    public final static String CMD_ELEMENT_LANGUAGE_ATTRIBUTE_NAMESPACE_URI = XML_NAMESPACE;
    public final static String CMD_ELEMENT_LANGUAGE_ATTRIBUTE_NAME = "lang";
    /**
     * XPath to CMDI headers
     */
    public static final String CMD_HEADER_PATH = "/cmd:CMD/cmd:Header";
    public final static String CMD_HEADER_MD_CREATOR = "MdCreator";
    public final static String CMD_HEADER_MD_CREATION_DATE = "MdCreationDate";
    public final static String CMD_HEADER_MD_SELF_LINK = "MdSelfLink";
    public final static String CMD_HEADER_MD_PROFILE = "MdProfile";
    public final static String CMD_HEADER_MD_COLLECTION_DISPLAY_NAME = "MdCollectionDisplayName";

    /**
     * Prevents instantiation
     */
    private CMDIConstants() {
    }
}
