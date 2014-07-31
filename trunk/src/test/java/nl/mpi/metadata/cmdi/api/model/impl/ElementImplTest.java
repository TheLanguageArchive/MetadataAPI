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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ElementImplTest extends CMDIMetadataElementImplTest {

    protected static final String DEFAULT_VALUE = "value";
    protected static ComponentType parentType;
    protected static ElementType type;
    protected CMDIDocument document;
    protected Component parent;
    private ElementImpl instance;

    @BeforeClass
    public static void setUpTest() throws Exception {
	CMDIProfile profile = getNewTestProfileAndRead();
	parentType = (ComponentType) profile.getType("Collection");
	type = (ElementType) ((ComponentType) parentType.getType("GeneralInfo")).getType("Name");
    }

    protected ElementImpl createInstance(ElementType type, Component parent, String value) {
	return new ElementImpl(type, parent, value);
    }

    @Before
    public void setUp() throws Exception {
	document = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY);
	// Add collection to document, this will be Collection[2] because by default the document already has a collection
	parent = new ComponentImpl(parentType, document);
	document.addChildElement(parent);
	instance = createInstance(type, parent, DEFAULT_VALUE);
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
	assertEquals(DEFAULT_VALUE, instance.getValue());
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
	assertEquals(DEFAULT_VALUE, instance.getDisplayValue());
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

    /**
     * Test of getMetadataDocument method, of class ElementImpl.
     */
    @Test
    public void testGetMetadataDocument() {
	assertEquals(document, instance.getMetadataDocument());
    }

    @Test
    public void testGetPathString() {
	assertEquals("/cmd:CMD/cmd:Components/cmd:TextCorpusProfile/cmd:Collection[2]/cmd:Name[1]", instance.getPathString());
    }

    @Test
    public void testDirtyAfterValueChange() {
	instance.setDirty(false);

	// changing value should make dirty
	instance.setValue("newValue");
	assertTrue(instance.isDirty());

	// make undirty and set same value. Should not become dirty.
	instance.setDirty(false);
	instance.setValue("newValue");
	assertFalse(instance.isDirty());

	// setting to null should also make dirty
	instance.setValue(null);
	assertTrue(instance.isDirty());

	// but not after a second time
	instance.setDirty(false);
	instance.setValue(null);
	assertFalse(instance.isDirty());
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
