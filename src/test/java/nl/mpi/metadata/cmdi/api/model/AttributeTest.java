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

import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.type.CMDIAttributeType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class AttributeTest {

    private MetadataElementAttributeType attributeType;
    private final String PATH = "@attribute";

    @Before
    public void setUp() {
	attributeType = new CMDIAttributeType();
    }

    @Test(expected = NullPointerException.class)
    public void testIllegalConstruction() {
	new Attribute(null, "@path");
    }

    /**
     * Test of getType method, of class Attribute.
     */
    @Test
    public void testGetType() {
	Attribute instance = new Attribute(attributeType, PATH);
	assertEquals(attributeType, instance.getType());
    }

    /**
     * Test of getValue method, of class Attribute.
     */
    @Test
    public void testGetValue() {
	Attribute instance = new Attribute(attributeType, PATH);
	instance.setValue("testValue");
	assertEquals("testValue", instance.getValue());
    }

    @Test
    public void testGetPathString() {
	Attribute instance = new Attribute(attributeType, PATH);
	assertEquals(PATH, instance.getPathString());
    }
}
