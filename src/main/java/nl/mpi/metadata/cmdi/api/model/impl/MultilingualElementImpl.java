/*
 * Copyright (C) 2012 Max Planck Institute for Psycholinguistics
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
package nl.mpi.metadata.cmdi.api.model.impl;

import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.MultilingualElement;
import nl.mpi.metadata.cmdi.api.type.ElementType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MultilingualElementImpl<T> extends ElementImpl<T> implements MultilingualElement<T> {

    private String language;

    /**
     * Constructs a multilingual element with no value or language specified
     *
     * @param elementType type of the element
     * @param parent container that will be this element's parent
     */
    public MultilingualElementImpl(ElementType elementType, CMDIContainerMetadataElement parent) {
	super(elementType, parent);
    }

    /**
     * Constructs a multilingual element with no language specified
     *
     * @param elementType type of the element
     * @param parent container that will be this element's parent
     * @param value initial value of the element
     */
    public MultilingualElementImpl(ElementType elementType, CMDIContainerMetadataElement parent, T value) {
	super(elementType, parent, value);
    }

    /**
     * Constructs a multilingual element with a value and language specified
     *
     * @param elementType type of the element
     * @param parent container that will be this element's parent
     * @param value initial value of the element
     */
    public MultilingualElementImpl(ElementType elementType, CMDIContainerMetadataElement parent, T value, String language) {
	super(elementType, parent, value);
	this.language = language;
    }

    public void setLanguage(String language) {
	this.language = language;
    }

    public String getLanguage() {
	return language;
    }
}
