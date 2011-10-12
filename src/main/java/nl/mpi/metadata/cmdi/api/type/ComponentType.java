/*
 * Copyright (C) 2011 The Max Planck Institute for Psycholinguistics
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

import java.util.Collection;
import nl.mpi.metadata.api.type.MetadataContainerElementType;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategoryType;

/**
 * This class represents a CMDI component definition, defined by http://www.clarin.eu/cmd/general-component-schema.xsd
 * 
 * For example components, see http://www.clarin.eu/cmd/example/
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ComponentType implements MetadataContainerElementType, DataCategoryType {

    public Collection<MetadataElementType> getContainableTypes() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canContainType(MetadataElementType type) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMinOccurencesOfType(MetadataElementType type) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaxOccurencesOfType(MetadataElementType type) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<MetadataElementAttributeType> getAttributes() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataCategory getDataCategory() {
	throw new UnsupportedOperationException("Not supported yet.");
    }
}
