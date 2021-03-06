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

import java.net.URI;
import java.util.Collections;
import java.util.List;
import nl.mpi.metadata.api.type.MetadataDocumentTypeReader;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.apache.xmlbeans.SchemaProperty;

/**
 * This class represents a CMDI profile, defined by http://www.clarin.eu/cmd/general-component-schema.xsd
 *
 * For an example profile, see http://www.clarin.eu/cmd/example/example-profile-instance.xml
 * For an example profile schema file, see http://www.clarin.eu/cmd/example/example-md-schema.xsd
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileImpl extends ComponentTypeImpl implements CMDIProfile {

    private final URI schemaLocation;
    private final List<String> headerNames;

    /**
     * Constructs a new profile object for a schema element with the specified location and root path.
     * <em>Does not actually read data; for this, use a {@link MetadataDocumentTypeReader}</em>
     *
     * @param schemaLocation Location of the CMDI profile schema to load
     * @param schemaElement schema element for the root component
     * @param rootPath path to the root element in the instance
     * @see CMDIEntityResolver
     */
    public CMDIProfileImpl(URI schemaLocation, SchemaProperty schemaElement, StringBuilder rootPath, List<String> headerNames) {
	super(schemaElement, null, rootPath);
	this.schemaLocation = schemaLocation;
	if (headerNames == null) {
	    this.headerNames = Collections.emptyList();
	} else {
	    this.headerNames = Collections.unmodifiableList(headerNames);
	}
    }

    @Override
    public URI getSchemaLocation() {
	return schemaLocation;
    }

    @Override
    public List<String> getHeaderNames() {
	return headerNames;
    }
}
