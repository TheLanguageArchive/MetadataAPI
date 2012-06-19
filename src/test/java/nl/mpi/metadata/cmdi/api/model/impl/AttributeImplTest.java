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

import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.impl.AttributeImpl;
import nl.mpi.metadata.cmdi.api.type.CMDIAttributeType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class AttributeImplTest {

    private Mockery context = new JUnit4Mockery();
    private MetadataElementAttributeType attributeType;
    private CMDIMetadataElement parent;

    @Before
    public void setUp() {
	attributeType = new CMDIAttributeType();
	parent = context.mock(CMDIMetadataElement.class);
    }

    @Test(expected = NullPointerException.class)
    public void testIllegalConstruction() {
	new AttributeImpl(null, parent);
    }

    /**
     * Test of getType method, of class AttributeImpl.
     */
    @Test
    public void testGetType() {
	AttributeImpl instance = new AttributeImpl(attributeType, parent);
	assertEquals(attributeType, instance.getType());
    }

    /**
     * Test of getValue method, of class AttributeImpl.
     */
    @Test
    public void testGetValue() {
	AttributeImpl instance = new AttributeImpl(attributeType, parent);
	instance.setValue("testValue");
	assertEquals("testValue", instance.getValue());
    }

    @Test
    public void testGetPathString() {
	attributeType.setName("attributeType");

	AttributeImpl instance = new AttributeImpl(attributeType, parent);
	instance.setValue("testValue");

	context.checking(new Expectations() {

	    {
		oneOf(parent).getPathString();
		will(returnValue("/path/to/parent"));
	    }
	});

	assertEquals("/path/to/parent/@attributeType", instance.getPathString());
    }

    @Test
    public void testGetPathStringNamespace() {
	attributeType.setName("attributeType");
	attributeType.setNamespaceURI("http://namespace/uri");

	AttributeImpl instance = new AttributeImpl(attributeType, parent);
	instance.setValue("testValue");

	context.checking(new Expectations() {

	    {
		oneOf(parent).getPathString();
		will(returnValue("/path/to/parent"));
	    }
	});

	assertEquals("/path/to/parent/@{http://namespace/uri}attributeType", instance.getPathString());
    }
}
