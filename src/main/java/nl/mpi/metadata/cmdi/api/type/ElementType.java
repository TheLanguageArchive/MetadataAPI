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

import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategoryType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface ElementType extends CMDIProfileElement, DataCategoryType, MetadataElementType {

    /**
     * Display priority of this element
     *
     * @return the display priority of this element
     */
    int getDisplayPriority();

    String getPathString();

    /**
     * Gets the multilingual property of this type
     *
     * @return whether this element type is multilingual
     */
    boolean isMultilingual();

    /**
     * Sets the display priority of this element
     *
     * @param displayPriority new display priority of this element
     */
    void setDisplayPriority(int displayPriority);

    /**
     * Sets the multilingual property of this type
     *
     * @param multilingual whether this element type is multilingual
     */
    void setMultilingual(boolean multilingual);
    
}
