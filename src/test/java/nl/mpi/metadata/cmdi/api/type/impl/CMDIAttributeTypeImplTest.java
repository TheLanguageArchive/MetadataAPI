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
package nl.mpi.metadata.cmdi.api.type.impl;

import org.apache.xmlbeans.SchemaProperty;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIAttributeTypeImplTest {

    private CMDIAttributeTypeImpl instance;
    private Mockery context = new JUnit4Mockery();

    @Before
    public void setUp() {
	instance = new CMDIAttributeTypeImpl("path", "type");
    }

    @After
    public void tearDown() {
	instance = null;
    }

    /**
     * Test of getName method, of class CMDIAttributeTypeImpl.
     */
    @Test
    public void testGetName() {
	assertNull(instance.getName());
	instance.setName("name");
	assertEquals("name", instance.getName());

	instance = new CMDIAttributeTypeImpl("path", null, "newName", "type");
	assertEquals("newName", instance.getName());
    }

    /**
     * Test of getType method, of class CMDIAttributeTypeImpl.
     */
    @Test
    public void testGetType() {
	assertEquals("type", instance.getType());
    }

    /**
     * Test of isMandatory method, of class CMDIAttributeTypeImpl.
     */
    @Test
    public void testIsMandatory() {
	assertFalse(instance.isMandatory());
	instance.setMandatory(true);
	assertTrue(instance.isMandatory());
    }

    /**
     * Test of getDefaultValue method, of class CMDIAttributeTypeImpl.
     */
    @Test
    public void testGetDefaultValue() {
	assertNull(instance.getDefaultValue());
	instance.setDefaultValue("default");
	assertEquals("default", instance.getDefaultValue());
    }

    /**
     * Test of getNamespaceURI method, of class CMDIAttributeTypeImpl.
     */
    @Test
    public void testGetNamespaceURI() {
	assertNull(instance.getNamespaceURI());
	instance = new CMDIAttributeTypeImpl("path", "nsUri", "localPart", "type");
	assertEquals("nsUri", instance.getNamespaceURI());
	instance.setNamespaceURI("newNsUri");
	assertEquals("newNsUri", instance.getNamespaceURI());
    }

    /**
     * Test of toString method, of class CMDIAttributeTypeImpl.
     */
    @Test
    public void testToString() {
	assertEquals("type", instance.toString());
    }

    /**
     * Test of getSchemaElement method, of class CMDIAttributeTypeImpl.
     */
    @Test
    public void testGetSchemaElement() {
	assertNull(instance.getSchemaElement());
	SchemaProperty element = context.mock(SchemaProperty.class);
	instance.setSchemaElement(element);
	assertSame(element, instance.getSchemaElement());
    }

    /**
     * Test of getPathString method, of class CMDIAttributeTypeImpl.
     */
    @Test
    public void testGetPathString() {
	assertEquals("path", instance.getPathString());
    }

    /**
     * Test of createAttributePathString method, of class CMDIAttributeTypeImpl.
     */
    @Test
    public void testCreateAttributePathString() {
	String parentPath = "/path/to/parent";
	String localPart = "attribute";
	String result = CMDIAttributeTypeImpl.createAttributePathString(parentPath, null, localPart);
	assertEquals("/path/to/parent/@attribute", result);

	String nsUri = "namespace";
	result = CMDIAttributeTypeImpl.createAttributePathString(parentPath, nsUri, localPart);
	assertEquals("/path/to/parent/@{namespace}attribute", result);
    }
}
