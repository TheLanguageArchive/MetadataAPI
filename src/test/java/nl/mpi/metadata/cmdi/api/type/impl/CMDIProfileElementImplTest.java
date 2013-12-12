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
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

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
	instance = new CMDIProfileElementTestImpl();
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
	mockContext.checking(new Expectations() {
	    {
		allowing(instance.getSchemaElement()).getMaxOccurs();
		will(returnValue(BigInteger.valueOf(5)));
	    }
	});

	assertEquals(5, instance.getMaxOccurences());
    }

    /**
     * Test of getMaxOccurences method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetMaxOccurencesUnbounded() {


	mockContext.checking(new Expectations() {
	    {
		allowing(instance.getSchemaElement()).getMaxOccurs();
		will(returnValue(null));
	    }
	});

	assertEquals(-1, instance.getMaxOccurences());
    }

    /**
     * Test of getMinOccurences method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetMinOccurences() {

	mockContext.checking(new Expectations() {
	    {
		allowing(instance.getSchemaElement()).getMinOccurs();
		will(returnValue(BigInteger.valueOf(5)));
	    }
	});

	int result = instance.getMinOccurences();
	assertEquals(5, result);
    }

    /**
     * Test of getMinOccurences method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetMinOccurencesDefault() {

	mockContext.checking(new Expectations() {
	    {
		allowing(instance.getSchemaElement()).getMinOccurs();
		will(returnValue(null));
	    }
	});

	int result = instance.getMinOccurences();
	assertEquals(0, result);
    }

    /**
     * Test of getName method, of class CMDIProfileElementImpl.
     */
    @Test
    public void testGetName() {

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
	final SchemaProperty schemaProperty = mockContext.mock(SchemaProperty.class, "parentSchemaElement");
	mockContext.checking(new Expectations() {
	    {
		oneOf(schemaProperty).getName();
	    }
	});
	final ComponentTypeImpl parent = new ComponentTypeImpl(schemaProperty, null, new StringBuilder("/path"));
	CMDIProfileElementImpl element = new CMDIProfileElementTestImpl(parent);
	assertSame(parent, element.getParent());
    }

    public class CMDIProfileElementTestImpl extends CMDIProfileElementImpl {

	protected String pathString;

	public CMDIProfileElementTestImpl() {
	    this(null);
	}

	public CMDIProfileElementTestImpl(ComponentTypeImpl parent) {
	    super(getSchemaPropertyMock(), parent);
	}

	@Override
	public String getPathString() {
	    return pathString;
	}
    }

    private SchemaProperty getSchemaPropertyMock() {
	final SchemaProperty mock = mockContext.mock(SchemaProperty.class);
	mockContext.checking(new Expectations() {
	    {
		// Constructor will ask for schema property name
		oneOf(mock).getName();
		will(returnValue(new QName("elementName")));
	    }
	});
	return mock;
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
