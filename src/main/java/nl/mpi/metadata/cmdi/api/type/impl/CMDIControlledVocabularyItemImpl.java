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

import nl.mpi.metadata.cmdi.api.type.CMDIControlledVocabularyItem;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIControlledVocabularyItemImpl extends nl.mpi.metadata.api.type.ControlledVocabularyItem implements CMDIControlledVocabularyItem {

    private DataCategory dataCategory;
    
    /**
     * Set the data category
     * @param dataCategory 
     */
    protected void setDataCategory(DataCategory dataCategory){
	this.dataCategory = dataCategory;
    }
    
    @Override
    public DataCategory getDataCategory() {
	return dataCategory;
    }
}
