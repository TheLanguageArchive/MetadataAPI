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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import org.apache.xmlbeans.impl.schema.SchemaPropertyImpl;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileTest extends CMDIAPITestCase {

    @Test
    public void testEquals() throws Exception {
	SchemaPropertyImpl schemaProperty1 = new SchemaPropertyImpl();
	schemaProperty1.setName(new QName("Name1"));
	CMDIProfile profile1 = new CMDIProfile(testSchemaTextCorpus.toURI(), schemaProperty1, null, null);
	SchemaPropertyImpl schemaProperty2 = new SchemaPropertyImpl();
	schemaProperty2.setName(new QName("Name1"));
	CMDIProfile profile2 = new CMDIProfile(testSchemaTextCorpus.toURI(), schemaProperty2, null, null);
	assertTrue("Expected equality of profiles", profile1.equals(profile2));
	assertTrue("Expected equality of profiles", profile2.equals(profile1));
	SchemaPropertyImpl schemaProperty3 = new SchemaPropertyImpl();
	schemaProperty3.setName(new QName("Name3"));
	CMDIProfile profile3 = new CMDIProfile(testSchemaWebservice.toURI(), schemaProperty3, null, null);
	assertFalse("Expected non-equality of profiles", profile1.equals(profile3));
	assertFalse("Expected non-equality of profiles", profile3.equals(profile1));
    }

    /**
     * Test of getSchemaLocation method, of class CMDIProfile.
     */
    @Test
    public void testGetSchemaLocation() throws Exception {
	CMDIProfile profile = new CMDIProfile(testSchemaTextCorpus.toURI(), new SchemaPropertyImpl(), null, null);
	assertEquals(testSchemaTextCorpus.toURI(), profile.getSchemaLocation());
    }

    @Test
    public void testGetHeaderNames() throws Exception {
	List<String> items = Arrays.asList("name1", "name2");
	CMDIProfile profile = new CMDIProfile(testSchemaTextCorpus.toURI(), new SchemaPropertyImpl(), null, items);
	assertArrayEquals(items.toArray(), profile.getHeaderNames().toArray());
    }

    @Test
    public void testGetHeaderNamesEmpty() throws Exception {

	CMDIProfile profile = new CMDIProfile(testSchemaTextCorpus.toURI(), new SchemaPropertyImpl(), null, null);
	assertEquals(0, profile.getHeaderNames().size());
    }
}
