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
package nl.mpi.metadata.api.type;

import java.util.Collection;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataElementType {

    /**
     * 
     * @return Name of this element type
     */
    String getName();

    /**
     * 
     * @return Description for this element type
     */
    String getDescription();

    /**
     * 
     * @return Attribute types supported by this element type
     */
    Collection<MetadataElementAttributeType> getAttributes();

    int getMinOccurences(MetadataContainerElementType container);

    int getMaxOccurences(MetadataContainerElementType container);
}
