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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nl.mpi.metadata.api.type.ControlledVocabularyItem;
import nl.mpi.metadata.api.type.ControlledVocabularyMetadataType;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.XmlAnySimpleType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ControlledVocabularyElementType extends ElementType implements ControlledVocabularyMetadataType {

    private List<ControlledVocabularyItem> items;

    public ControlledVocabularyElementType(SchemaProperty schemaElement, ComponentType parent) {
	super(schemaElement, parent);
    }

    @Override
    protected void readProperties() {
	super.readProperties();
	readItems();
    }

    /**
     * Reads the allowed controlled vocabulary items from the element type
     */
    private void readItems() {
	XmlAnySimpleType[] itemTypes = getSchemaElement().getType().getEnumerationValues();
	if (itemTypes != null && itemTypes.length > 0) {
	    items = new ArrayList<ControlledVocabularyItem>();
	    for (XmlAnySimpleType itemType : getSchemaElement().getType().getEnumerationValues()) {
		CMDIControlledVocabularyItem item = new CMDIControlledVocabularyItem();
		item.setValue(itemType.getStringValue());
		// TODO: item.setDescription(itemDescription);
		// TODO: item.setDataCategory(itemDataCategory);
		items.add(item);
	    }
	} else {
	    items = Collections.emptyList();
	}
    }

    public List<ControlledVocabularyItem> getItems() {
	return Collections.unmodifiableList(items);
    }
}
