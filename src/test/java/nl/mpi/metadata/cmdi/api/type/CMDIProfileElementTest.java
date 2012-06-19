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
package nl.mpi.metadata.cmdi.api.type;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import org.apache.xmlbeans.SchemaProperty;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileElementTest {

    private static MetadataElementAttributeType attrType1;
    private static MetadataElementAttributeType attrType2;
    private static MetadataElementAttributeType attrType3;
    private Mockery mockContext = new JUnit4Mockery();

    public CMDIProfileElementTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
	attrType1 = new TestMetadataElementAttributeType("http://namespace", "name", "type");
	attrType2 = new TestMetadataElementAttributeType("http://namespace2", "name2", "type2");
	attrType3 = new TestMetadataElementAttributeType(null, "name3", "type3");
    }

    /**
     * Test of getPathString method, of class CMDIProfileElement.
     */
    @Test
    public void testGetPathString() {
	CMDIProfileElementTestImpl instance = new CMDIProfileElementTestImpl();
	instance.pathString = "/path/to/element";
	String result = instance.getPathString();
	assertEquals("/path/to/element", result);
    }

    /**
     * Test of getAttributeTypeByName method, of class CMDIProfileElement.
     */
    @Test
    public void testGetAttributeTypeByName() {
	CMDIProfileElement instance = new CMDIProfileElementTestImpl();
	instance.setAttributes(Arrays.asList(attrType1, attrType2, attrType3));

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
     * Test of getAttributes method, of class CMDIProfileElement.
     */
    @Test
    public void testGetAttributes() {
	CMDIProfileElement instance = new CMDIProfileElementTestImpl();
	final List<MetadataElementAttributeType> attrList = Arrays.asList(attrType1, attrType2, attrType3);
	instance.setAttributes(attrList);

	Collection result = instance.getAttributes();
	assertEquals(3, result.size());
	assertTrue(Arrays.deepEquals(attrList.toArray(), result.toArray()));
    }

    /**
     * Test of getDataCategory method, of class CMDIProfileElement.
     */
    @Test
    public void testGetDataCategory() throws URISyntaxException {
	CMDIProfileElement instance = new CMDIProfileElementTestImpl();
	DataCategory dc = new DataCategory(new URI("http://dc.org/dc-123"));
	instance.setDataCategory(dc);
	DataCategory result = instance.getDataCategory();
	assertEquals(dc, result);
    }

    /**
     * Test of getDescription method, of class CMDIProfileElement.
     */
    @Test
    public void testGetDescription() {
	CMDIProfileElement instance = new CMDIProfileElementTestImpl();
	instance.setDescription("My test element");
	String result = instance.getDescription();
	assertEquals("My test element", result);
    }

    /**
     * Test of getMaxOccurences method, of class CMDIProfileElement.
     */
    @Test
    public void testGetMaxOccurences() {

	final CMDIProfileElement instance = new CMDIProfileElementTestImpl();
	mockContext.checking(new Expectations() {

	    {
		allowing(instance.getSchemaElement()).getMaxOccurs();
		will(returnValue(BigInteger.valueOf(5)));
	    }
	});

	assertEquals(5, instance.getMaxOccurences());
    }

    /**
     * Test of getMaxOccurences method, of class CMDIProfileElement.
     */
    @Test
    public void testGetMaxOccurencesUnbounded() {

	final CMDIProfileElement instance = new CMDIProfileElementTestImpl();
	mockContext.checking(new Expectations() {

	    {
		allowing(instance.getSchemaElement()).getMaxOccurs();
		will(returnValue(null));
	    }
	});

	assertEquals(-1, instance.getMaxOccurences());
    }

    /**
     * Test of getMinOccurences method, of class CMDIProfileElement.
     */
    @Test
    public void testGetMinOccurences() {
	final CMDIProfileElement instance = new CMDIProfileElementTestImpl();
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
     * Test of getMinOccurences method, of class CMDIProfileElement.
     */
    @Test
    public void testGetMinOccurencesDefault() {
	final CMDIProfileElement instance = new CMDIProfileElementTestImpl();
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
     * Test of getName method, of class CMDIProfileElement.
     */
    @Test
    public void testGetName() {
	final CMDIProfileElement instance = new CMDIProfileElementTestImpl();
	String result = instance.getName();
	assertEquals("elementName", result);
    }

    /**
     * Test of getSchemaElement method, of class CMDIProfileElement.
     */
    @Test
    public void testGetSchemaElement() {
	CMDIProfileElement instance = new CMDIProfileElementTestImpl();
	assertSame(instance.schemaElement, instance.getSchemaElement());
    }

    /**
     * Test of getParent method, of class CMDIProfileElement.
     */
    @Test
    public void testGetParent() {
	final SchemaProperty schemaProperty = mockContext.mock(SchemaProperty.class, "parentSchemaElement");
	mockContext.checking(new Expectations() {

	    {
		oneOf(schemaProperty).getName();
	    }
	});
	final ComponentType parent = new ComponentType(schemaProperty, null, new StringBuilder("/path"));
	CMDIProfileElement instance = new CMDIProfileElementTestImpl(parent);
	assertSame(parent, instance.getParent());
    }

    public class CMDIProfileElementTestImpl extends CMDIProfileElement {

	protected String pathString;

	public CMDIProfileElementTestImpl() {
	    this(null);
	}

	public CMDIProfileElementTestImpl(ComponentType parent) {
	    super(getSchemaPropertyMock(), parent);
	}

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

    private static class TestMetadataElementAttributeType implements MetadataElementAttributeType {

	final String namespace;
	final String name;
	final String type;

	public TestMetadataElementAttributeType(String namespace, String name, String type) {
	    this.namespace = namespace;
	    this.name = name;
	    this.type = type;
	}

	public String getNamespaceURI() {
	    return namespace;
	}

	public String getName() {
	    return name;
	}

	public String getType() {
	    return type;
	}

	public String getDefaultValue() {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isMandatory() {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setDefaultValue(String defaultValue) {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setMandatory(boolean mandatory) {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setName(String name) {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setNamespaceURI(String namespace) {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setType(String type) {
	    throw new UnsupportedOperationException("Not supported yet.");
	}
    }
}
