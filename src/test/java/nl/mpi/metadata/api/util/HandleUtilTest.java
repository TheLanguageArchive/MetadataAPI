/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
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
package nl.mpi.metadata.api.util;

import java.net.URI;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class HandleUtilTest {

    private HandleUtil instance;

    @Before
    public void setUp() {
	instance = new HandleUtil();
    }

    /**
     * Test of isHandleUri method, of class HandleUtil.
     */
    @Test
    public void testIsHandleUri() {
	assertTrue(instance.isHandleUri(URI.create("hdl:1234/5678"))); // valid
	assertTrue(instance.isHandleUri(URI.create("hdl:1234/test-handle"))); // valid
	assertFalse(instance.isHandleUri(URI.create("1234/5678"))); // no hdl scheme
	assertFalse(instance.isHandleUri(URI.create("hdl:test"))); // hdl scheme but invalid handle
	assertFalse(instance.isHandleUri(URI.create("http://hdl.handle.net/1234/5678"))); // handle proxy is not a valid handle URI
    }

    /**
     * Test of isHandle method, of class HandleUtil.
     */
    @Test
    public void testIsHandle() {
	assertTrue(instance.isHandle("1234/5678"));
	assertTrue(instance.isHandle("1234/test-handle"));
	assertFalse(instance.isHandle("1234"));
	assertFalse(instance.isHandle(""));
    }

    /**
     * Test of createHandleUri method, of class HandleUtil.
     */
    @Test
    public void testCreateHandleUri() {
	// already valid handle URI string, will return URI version
	URI result = instance.createHandleUri("hdl:1234/5678");
	assertEquals(URI.create("hdl:1234/5678"), result);

	// valid handle, will be wrapped in handle URI
	result = instance.createHandleUri("1234/5678");
	assertEquals(URI.create("hdl:1234/5678"), result);

	// handle URL from handle proxy, will convert to handle proxy
	result = instance.createHandleUri("http://hdl.handle.net/1234/5678");
	assertEquals(URI.create("hdl:1234/5678"), result);

	// invalid handle
	result = instance.createHandleUri("1234");
	assertNull(result);

	// invalid handle
	result = instance.createHandleUri("hdl:1234");
	assertNull(result);

	// invalid handle
	result = instance.createHandleUri("http://hdl.handle.net/1234");
	assertNull(result);
    }
}
