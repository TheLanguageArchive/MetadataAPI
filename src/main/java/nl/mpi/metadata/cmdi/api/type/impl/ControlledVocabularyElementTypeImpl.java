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

import java.util.Collections;
import java.util.List;
import nl.mpi.metadata.api.type.ControlledVocabularyItem;
import nl.mpi.metadata.cmdi.api.type.ControlledVocabularyElementType;
import org.apache.xmlbeans.SchemaProperty;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ControlledVocabularyElementTypeImpl extends ElementTypeImpl implements ControlledVocabularyElementType {

    private List<ControlledVocabularyItem> items;

    public ControlledVocabularyElementTypeImpl(SchemaProperty schemaElement, ComponentTypeImpl parent, CharSequence path) {
	super(schemaElement, parent, path);
    }

    protected void setItems(List<ControlledVocabularyItem> items) {
	this.items = items;
    }

    @Override
    public List<ControlledVocabularyItem> getItems() {
	return Collections.unmodifiableList(items);
    }
}
