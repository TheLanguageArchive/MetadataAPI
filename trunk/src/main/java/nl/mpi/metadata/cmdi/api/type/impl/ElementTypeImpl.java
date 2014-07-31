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

import nl.mpi.metadata.cmdi.api.type.ElementType;
import org.apache.xmlbeans.SchemaProperty;

/**
 * This class represents an element definition inside a CMDI component, defined by http://www.clarin.eu/cmd/general-component-schema.xsd
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ElementTypeImpl extends CMDIProfileElementImpl implements ElementType {

    private final String path;
    private int displayPriority; //defaults to 0
    private boolean multilingual; //defaults to false

    public ElementTypeImpl(SchemaProperty schemaElement, ComponentTypeImpl parent, CharSequence path) {
	super(schemaElement, parent);
	// Convert path to String, it's immutable and won't be extended (in contrast to ComponentTypes)
	this.path = path.toString();
    }

    @Override
    public String getPathString() {
	return path.toString();
    }

    /**
     * Display priority of this element
     *
     * @return the display priority of this element
     */
    @Override
    public int getDisplayPriority() {
	return displayPriority;
    }

    /**
     * Sets the display priority of this element
     *
     * @param displayPriority new display priority of this element
     */
    @Override
    public void setDisplayPriority(int displayPriority) {
	this.displayPriority = displayPriority;
    }

    /**
     * Gets the multilingual property of this type
     *
     * @return whether this element type is multilingual
     */
    @Override
    public boolean isMultilingual() {
	return multilingual;
    }

    /**
     * Sets the multilingual property of this type
     *
     * @param multilingual whether this element type is multilingual
     */
    @Override
    public void setMultilingual(boolean multilingual) {
	this.multilingual = multilingual;
    }
}
