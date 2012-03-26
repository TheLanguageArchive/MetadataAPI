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
package nl.mpi.metadata.cmdi.api.model;

import java.net.URI;
import java.util.Collection;
import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ElementType;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ElementTest extends CMDIMetadataElementTest {

    private static ComponentType parentType;
    private static ElementType type;
    private CMDIDocument document;
    private Component parent;
    private Element instance;

    @BeforeClass
    public static void setUpTest() throws Exception {
	CMDIProfile profile = getNewTestProfileAndRead();
	parentType = (ComponentType) profile.getContainableTypeByName("Collection");
	type = (ElementType) ((ComponentType) parentType.getContainableTypeByName("GeneralInfo")).getContainableTypeByName("Name");
    }

    @Before
    public void setUp() throws Exception {
	document = getNewTestDocument();
	parent = new Component(parentType, document);
	instance = new Element(type, parent, "value");
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
     * Test of getName method, of class Element.
     */
    @Test
    public void testGetName() {
	assertEquals("Name", instance.getName());
    }

    /**
     * Test of getValue method, of class Element.
     */
    @Test
    public void testGetValue() {
	assertEquals("value", instance.getValue());
    }

    /**
     * Test of setValue method, of class Element.
     */
    @Test
    public void testSetValue() {
	instance.setValue("newValue");
	assertEquals("newValue", instance.getValue());
    }

    /**
     * Test of getParent method, of class Element.
     */
    @Test
    public void testGetParent() {
	assertEquals(parent, instance.getParent());
    }

    /**
     * Test of getType method, of class Element.
     */
    @Test
    public void testGetType() {
	assertEquals(type, instance.getType());
    }

    /**
     * Test of getMetadataDocument method, of class Element.
     */
    @Test
    public void testGetMetadataDocument() {
	assertEquals(document, instance.getMetadataDocument());
    }

    /**
     * Test of addMetadataElementListener method, of class Element.
     */
    @Test
    @Ignore
    public void testAddMetadataElementListener() {
	System.out.println("addMetadataElementListener");
	MetadataElementListener listener = null;
	Element instance = null;
	instance.addMetadataElementListener(listener);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of removeMetadataElementListener method, of class Element.
     */
    @Test
    @Ignore
    public void testRemoveMetadataElementListener() {
	System.out.println("removeMetadataElementListener");
	MetadataElementListener listener = null;
	Element instance = null;
	instance.removeMetadataElementListener(listener);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of getReferences method, of class Element.
     */
    @Test
    @Ignore
    public void testGetReferences() {
	System.out.println("getReferences");
	Element instance = null;
	Collection expResult = null;
	Collection result = instance.getReferences();
	assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of createResourceReference method, of class Element.
     */
    @Test
    @Ignore
    public void testCreateResourceReference() {
	System.out.println("createResourceReference");
	URI uri = null;
	String mimetype = "";
	Element instance = null;
	DataResourceProxy expResult = null;
	DataResourceProxy result = instance.createResourceReference(uri, mimetype);
	assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of createMetadataReference method, of class Element.
     */
    @Test
    @Ignore
    public void testCreateMetadataReference() {
	System.out.println("createMetadataReference");
	URI uri = null;
	String mimetype = "";
	Element instance = null;
	MetadataResourceProxy expResult = null;
	MetadataResourceProxy result = instance.createMetadataReference(uri, mimetype);
	assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }
}
