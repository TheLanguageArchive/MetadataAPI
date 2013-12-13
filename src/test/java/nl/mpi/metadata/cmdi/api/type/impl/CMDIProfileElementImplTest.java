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

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.impl.schema.SchemaPropertyImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import static org.jmock.Expectations.returnValue;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileElementImplTest {

    private MetadataElementAttributeType attrType1;
    private MetadataElementAttributeType attrType2;
    private MetadataElementAttributeType attrType3;
    private Mockery mockContext = new JUnit4Mockery();
    private CMDIProfileElementTestImpl instance;

    @Before
    public void setUp() {
	instance = new CMDIProfileElementTestImpl("elementName");
    }

    /**
     * Test of getPathString method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetPathString() {
	instance.pathString = "/path/to/element";
	String result = instance.getPathString();
	assertEquals("/path/to/element", result);
    }

    /**
     * Test of getAttributeTypeByName method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetAttributeTypeByName() {
	setAttributes(instance);

	// Get by name and NS
	assertSame(attrType1, instance.getAttributeTypeByName("http://namespace", "name"));
	assertSame(attrType2, instance.getAttributeTypeByName("http://namespace2", "name2"));
	assertSame(attrType3, instance.getAttributeTypeByName(null, "name3"));
	// Ignore NS
	assertSame(attrType1, instance.getAttributeTypeByName(null, "name"));
	assertSame(attrType2, instance.getAttributeTypeByName(null, "name2"));
	// Try to get non-existent 
	assertNull(instance.getAttributeTypeByName(null, "name4"));
    }

    /**
     * Test of getAttributes method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetAttributes() {

	List<MetadataElementAttributeType> attributesList = setAttributes(instance);

	Collection result = instance.getAttributes();
	assertEquals(3, result.size());
	assertTrue(Arrays.deepEquals(attributesList.toArray(), result.toArray()));
    }

    /**
     * Test of getAttributes method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetAllAttributes() {

	List<MetadataElementAttributeType> attributesList = setAttributes(instance);

	Collection result = instance.getAllAttributes();
	assertEquals(3, result.size());

	instance.setExcludedAttributes(Collections.<MetadataElementAttributeType>emptySet());
	assertEquals(3, result.size());

	attrType1 = getMockMetadataElementAttributeType("http://namespace", "excluded", "type");
	attrType2 = getMockMetadataElementAttributeType("http://namespace2", "excluded2", "type2");
	final List<MetadataElementAttributeType> excludedAttributesList = Arrays.asList(attrType1, attrType2, attrType3);
	instance.setExcludedAttributes(excludedAttributesList);

	result = instance.getAllAttributes();
	assertEquals(5, result.size());
    }

    /**
     * Test of getDataCategory method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetDataCategory() throws URISyntaxException {

	DataCategory dc = new DataCategory(new URI("http://dc.org/dc-123"));
	instance.setDataCategory(dc);
	DataCategory result = instance.getDataCategory();
	assertEquals(dc, result);
    }

    /**
     * Test of getDescription method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetDescription() {

	instance.setDescription("My test element");
	String result = instance.getDescription();
	assertEquals("My test element", result);
    }

    /**
     * Test of getMaxOccurences method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetMaxOccurences() {
	((SchemaPropertyImpl) instance.getSchemaElement()).setMaxOccurs(BigInteger.valueOf(5));
	assertEquals(5, instance.getMaxOccurences());
    }

    /**
     * Test of getMaxOccurences method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetMaxOccurencesUnbounded() {
	((SchemaPropertyImpl) instance.getSchemaElement()).setMaxOccurs(null);
	assertEquals(-1, instance.getMaxOccurences());
    }

    /**
     * Test of getMinOccurences method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetMinOccurences() {
	((SchemaPropertyImpl) instance.getSchemaElement()).setMinOccurs(BigInteger.valueOf(5));
	assertEquals(5, instance.getMinOccurences());
    }

    /**
     * Test of getMinOccurences method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetMinOccurencesDefault() {
	((SchemaPropertyImpl) instance.getSchemaElement()).setMinOccurs(null);
	assertEquals(0, instance.getMinOccurences());
    }

    /**
     * Test of getName method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetName() {
	((SchemaPropertyImpl) instance.getSchemaElement()).setName(new QName("elementName"));
	String result = instance.getName();
	assertEquals("elementName", result);
    }

    /**
     * Test of getSchemaElement method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetSchemaElement() {
	assertSame(instance.schemaElement, instance.getSchemaElement());
    }

    /**
     * Test of getParent method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetParent() {
	final ComponentTypeImpl parent = new ComponentTypeImpl(getSchemaProperty("Parent"), null, new StringBuilder("/path"));
	CMDIProfileElementImpl element = new CMDIProfileElementTestImpl(parent,"childElementName");
	assertSame(parent, element.getParent());
    }

    public static class CMDIProfileElementTestImpl extends CMDIProfileElementImpl {

	protected String pathString;

	public CMDIProfileElementTestImpl(String elementName) {
	    this(null,elementName);
	}

	public CMDIProfileElementTestImpl(ComponentTypeImpl parent, String elementName) {
	    super(getSchemaProperty(elementName), parent);
	}

	@Override
	public String getPathString() {
	    return pathString;
	}
    }

    private static SchemaProperty getSchemaProperty(String elementName) {
	final SchemaPropertyImpl schemaProperty = new SchemaPropertyImpl();
	schemaProperty.setName(new QName(elementName));
	return schemaProperty;
    }

    private List<MetadataElementAttributeType> setAttributes(CMDIProfileElementImpl instance) {
	attrType1 = getMockMetadataElementAttributeType("http://namespace", "name", "type");
	attrType2 = getMockMetadataElementAttributeType("http://namespace2", "name2", "type2");
	attrType3 = getMockMetadataElementAttributeType(null, "name3", "type3");
	final List<MetadataElementAttributeType> attributesList = Arrays.asList(attrType1, attrType2, attrType3);
	instance.setAttributes(attributesList);
	return attributesList;
    }

    private MetadataElementAttributeType getMockMetadataElementAttributeType(final String namespace, final String name, final String type) {
	final MetadataElementAttributeType mock = mockContext.mock(MetadataElementAttributeType.class, name);
	mockContext.checking(new Expectations() {
	    {
		allowing(mock).getName();
		will(returnValue(name));

		allowing(mock).getNamespaceURI();
		will(returnValue(namespace));

		allowing(mock).getType();
		will(returnValue(type));
	    }
	});
	return mock;
    }
}
