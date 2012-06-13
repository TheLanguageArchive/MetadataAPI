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

import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ElementType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ElementImplTest extends CMDIMetadataElementImplTest {

    private static ComponentType parentType;
    private static ElementType type;
    private CMDIDocument document;
    private Component parent;
    private ElementImpl instance;

    @BeforeClass
    public static void setUpTest() throws Exception {
	CMDIProfile profile = getNewTestProfileAndRead();
	parentType = (ComponentType) profile.getContainableTypeByName("Collection");
	type = (ElementType) ((ComponentType) parentType.getContainableTypeByName("GeneralInfo")).getContainableTypeByName("Name");
    }

    @Before
    public void setUp() throws Exception {
	document = getNewTestDocument();
	// Add collection to document, this will be Collection[2] because by default the document already has a collection
	parent = new ComponentImpl(parentType, document);
	document.addChildElement(parent);
	instance = new ElementImpl(type, parent, "value");
	parent.addChildElement(instance);
    }

    @After
    public void tearDown() {
	document = null;
	parent = null;
	instance = null;
    }

    @Override
    CMDIMetadataElement getInstance() {
	return instance;
    }

    @Override
    CMDIDocument getDocument() {
	return document;
    }

    /**
     * Test of getName method, of class ElementImpl.
     */
    @Test
    public void testGetName() {
	assertEquals("Name", instance.getName());
    }

    /**
     * Test of getValue method, of class ElementImpl.
     */
    @Test
    public void testGetValue() {
	assertEquals("value", instance.getValue());
    }

    /**
     * Test of setValue method, of class ElementImpl.
     */
    @Test
    public void testSetValue() {
	instance.setValue("newValue");
	assertEquals("newValue", instance.getValue());
    }

    /**
     * Test of getValue method, of class ElementImpl.
     */
    @Test
    public void testGetDisplayValue() {
	assertEquals("value", instance.getDisplayValue());
	instance.setValue("newValue");
	assertEquals("newValue", instance.getDisplayValue());
	instance.setValue(null);
	assertEquals(null, instance.getDisplayValue());
    }

    /**
     * Test of getParent method, of class ElementImpl.
     */
    @Test
    public void testGetParent() {
	assertEquals(parent, instance.getParent());
    }

    /**
     * Test of getType method, of class ElementImpl.
     */
    @Test
    public void testGetType() {
	assertEquals(type, instance.getType());
    }

    @Test
    public void testGetLanguage() {
	assertNull(instance.getLanguage());
	instance = new ElementImpl(type, parent, "value", "en-EN");
	assertEquals("en-EN", instance.getLanguage());
    }
    
    @Test
    public void testSetLanguage() {
	instance.setLanguage("nl-NL");
	assertEquals("nl-NL", instance.getLanguage());
    }

    /**
     * Test of getMetadataDocument method, of class ElementImpl.
     */
    @Test
    public void testGetMetadataDocument() {
	assertEquals(document, instance.getMetadataDocument());
    }

    @Test
    public void testGetPathString() {
	assertEquals("/:CMD/:Components/:TextCorpusProfile/:Collection[2]/:Name[1]", instance.getPathString());
    }

    /**
     * Test of addMetadataElementListener method, of class ElementImpl.
     */
    @Test
    @Ignore
    public void testAddMetadataElementListener() {
	System.out.println("addMetadataElementListener");
	MetadataElementListener listener = null;
	ElementImpl instance = null;
	instance.addMetadataElementListener(listener);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of removeMetadataElementListener method, of class ElementImpl.
     */
    @Test
    @Ignore
    public void testRemoveMetadataElementListener() {
	System.out.println("removeMetadataElementListener");
	MetadataElementListener listener = null;
	ElementImpl instance = null;
	instance.removeMetadataElementListener(listener);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }
}
