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

import java.net.URI;
import nl.mpi.metadata.api.type.MetadataDocumentType;
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
public class CMDIProfile extends ComponentType implements MetadataDocumentType<CMDIProfileElement> {

    private URI schemaLocation;

    /**
     * Constructor. Loads schema but does not actually read data, for this call readSchema()
     * Will use the default CMDI Entity Resolver
     * @param schemaLocation Location of the CMDI profile schema to load
     * @see #readSchema()
     * @see CMDIEntityResolver
     */
    public CMDIProfile(URI schemaLocation, SchemaProperty schemaElement, StringBuilder rootPath) {
	super(schemaElement, null, rootPath);
	this.schemaLocation = schemaLocation;
    }

    public URI getSchemaLocation() {
	return schemaLocation;
    }
}
