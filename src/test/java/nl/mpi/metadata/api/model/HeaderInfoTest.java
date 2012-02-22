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
package nl.mpi.metadata.api.model;

import java.util.Collections;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class HeaderInfoTest {

    public static final String KEY = "KEY";
    public static final String VALUE = "VALUE";

    public HeaderInfoTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class HeaderInfo.
     */
    @Test
    public void testGetName() {
	String expResult = "J. Unit";
	HeaderInfo instance = new HeaderInfo(expResult, "");
	String result = instance.getName();
	assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class HeaderInfo.
     */
    @Test
    public void testGetValue() {
	String expResult = "My VALUE";
	HeaderInfo<String> instance = new HeaderInfo<String>("", expResult);
	String result = instance.getValue();
	assertEquals(expResult, result);
    }

    /**
     * Test of getAttributesMap method, of class HeaderInfo.
     */
    @Test
    public void testGetAttributes() {
	HeaderInfo instance = new HeaderInfo("key", "value");
	Map result = instance.getAttributes();
	assertNotNull(result);
	assertEquals(0, result.size());
	
	instance = new HeaderInfo("key", "value", Collections.singletonMap(KEY, VALUE));
	result = instance.getAttributes();
	assertEquals(1, result.size());
	assertEquals(VALUE, instance.getAttribute(KEY));
	assertEquals(null, instance.getAttribute("otherKey"));
    }
}
