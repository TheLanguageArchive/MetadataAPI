/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
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

import java.util.List;
import nl.mpi.metadata.api.type.MetadataContainerElementType;
import nl.mpi.metadata.api.type.MetadataElementType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface ComponentType extends CMDIProfileElement, MetadataContainerElementType<CMDIProfileElement> {

    boolean canContainType(CMDIProfileElement type);

    String getComponentId();

    List<MetadataElementType> getContainableTypes();

    StringBuilder getPath();

    String getPathString();

    CMDIProfileElement getType(String name);
    
}
