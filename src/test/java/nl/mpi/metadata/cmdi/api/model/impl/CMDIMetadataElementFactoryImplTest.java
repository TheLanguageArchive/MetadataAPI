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

import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.model.MultilingualElement;
import nl.mpi.metadata.cmdi.api.type.CMDIAttributeType;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ElementType;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIMetadataElementFactoryImplTest extends CMDIAPITestCase {

    private CMDIDocument document;
    private CMDIMetadataElementFactoryImpl instance;
    private Mockery context = new JUnit4Mockery();

    @Before
    public void setUp() throws Exception {
	document = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY);
	instance = new CMDIMetadataElementFactoryImpl();
    }

    /**
     * Test of createNewMetadataElement method, of class CMDIMetadataElementFactoryImpl.
     */
    @Test
    public void testCreateNewComponent() throws Exception {
	System.out.println("createNewMetadataElement");

	CMDIProfileElement type = document.getType().getContainableTypeByName("Collection");
	CMDIMetadataElement result = instance.createNewMetadataElement(document, type);

	assertTrue(result instanceof Component);
	assertEquals(type, result.getType());
	assertEquals(document, ((Component) result).getParent());
    }

    /**
     * Test of createNewMetadataElement method, of class CMDIMetadataElementFactoryImpl.
     */
    @Test
    public void testCreateNewElement() throws Exception {
	System.out.println("createNewMetadataElement");

	ComponentType componentType = (ComponentType) ((ComponentType) document.getType().getContainableTypeByName("Collection")).getContainableTypeByName("GeneralInfo");
	Component component = new ComponentImpl(componentType, document); // adding GeneralInfo directly to document doesn't really match the model but it doesn't matter here
	CMDIProfileElement type = componentType.getContainableTypeByName("Name");

	// Test for base Element
	CMDIMetadataElement result = instance.createNewMetadataElement(component, type);
	assertTrue(result instanceof Element);
	assertEquals(type, result.getType());
	assertEquals(component, ((Element) result).getParent());

	// Test for Multilingual Element
	((ElementType) type).setMultilingual(true);

	result = instance.createNewMetadataElement(component, type);
	assertTrue(result instanceof MultilingualElement);
	assertEquals(type, result.getType());
	assertEquals(component, ((Element) result).getParent());
    }

    @Test(expected = AssertionError.class)
    public void testCreateElementUnknownType() {
	// Create new implementation of CMDIProfileElement that the factory cannot handle
	CMDIContainerMetadataElement parent = context.mock(CMDIContainerMetadataElement.class);
	CMDIProfileElement cmdiProfileElement = new CMDIProfileElement(document.getDocumentType().getSchemaElement(), null) {

	    @Override
	    public String getPathString() {
		return null;
	    }
	};

	// This line should throw an exception because of the unknown type
	instance.createNewMetadataElement(parent, cmdiProfileElement);
    }

    @Test
    public void testCreateNewAttribute() throws Exception {
	Component component = context.mock(Component.class);

	CMDIAttributeType attributeType = new CMDIAttributeType();
	attributeType.setName("attributeName");
	Attribute<String> result = instance.createAttribute(component, attributeType);

	assertTrue(result instanceof Attribute);
	assertEquals(attributeType, result.getType());
    }
}
