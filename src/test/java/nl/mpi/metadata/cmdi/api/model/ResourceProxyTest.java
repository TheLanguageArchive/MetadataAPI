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
import java.net.URISyntaxException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ResourceProxyTest {

    private ResourceProxy resourceProxy;

    @Before
    public void setUp() throws Exception {
	resourceProxy = new ResourceProxyImpl("myId", new URI("http://testuri"), "test/my-mime-type");
    }

    @After
    public void tearDown() {
	resourceProxy = null;
    }

    /**
     * Test of getId method, of class ResourceProxy.
     */
    @Test
    public void testGetId() {
	assertEquals("myId", resourceProxy.getId());
    }

    /**
     * Test of getURI method, of class ResourceProxy.
     */
    @Test
    public void testGetURI() throws URISyntaxException {
	assertEquals(new URI("http://testuri"), resourceProxy.getURI());
    }

    @Test
    public void testSetURI() throws URISyntaxException {
	resourceProxy.setURI(new URI("http://newURI"));
	assertEquals(new URI("http://newURI"), resourceProxy.getURI());
    }

    /**
     * Test of getMimetype method, of class ResourceProxy.
     */
    @Test
    public void testGetMimetype() {
	assertEquals("test/my-mime-type", resourceProxy.getMimetype());
    }

    @Test
    public void testSetMimeType() {
	resourceProxy.setMimeType("test/new-mime-type");
	assertEquals("test/new-mime-type", resourceProxy.getMimetype());
    }

    @Test
    public void getHanlde() {
	assertEquals("http://testuri", resourceProxy.getHandle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHandleIllegalArgument() {
	// Not a syntactically valid URI
	resourceProxy.setHandle("h\\t\\t\\p:/");
    }

    @Test
    public void setHandle() {
	resourceProxy.setHandle("http://newuri");
	assertEquals("http://newuri", resourceProxy.getHandle());
    }

    /**
     * Test of equals method, of class ResourceProxy.
     */
    @Test
    public void testEquals() throws Exception {
	// ID and URI determine equality
	ResourceProxy resourceProxy2 = new ResourceProxyImpl("myId", new URI("http://testuri"), "test/my-mime-type");
	assertTrue(resourceProxy2.equals(resourceProxy));
	assertTrue(resourceProxy.equals(resourceProxy2));
	// Mime type is not taken into account in comparison
	ResourceProxy resourceProxy3 = new ResourceProxyImpl("myId", new URI("http://testuri"), "test/some-other-type");
	assertTrue(resourceProxy3.equals(resourceProxy));
	assertTrue(resourceProxy.equals(resourceProxy3));
	// Differs because of ID
	ResourceProxy resourceProxy4 = new ResourceProxyImpl("otherId", new URI("http://testuri"), "test/my-mime-type");
	assertFalse(resourceProxy4.equals(resourceProxy));
	assertFalse(resourceProxy.equals(resourceProxy4));
	// Differs because of URI
	ResourceProxy resourceProxy5 = new ResourceProxyImpl("myId", new URI("http://otheruri"), "test/my-mime-type");
	assertFalse(resourceProxy5.equals(resourceProxy));
	assertFalse(resourceProxy.equals(resourceProxy5));
    }

    /**
     * Test of hashCode method, of class ResourceProxy.
     */
    @Test
    public void testHashCode() throws Exception {
	// ID and URI determine equality
	ResourceProxy resourceProxy2 = new ResourceProxyImpl("myId", new URI("http://testuri"), "test/my-mime-type");
	assertEquals(resourceProxy2.hashCode(), resourceProxy.hashCode());
	// Mime type is not taken into account in comparison
	ResourceProxy resourceProxy3 = new ResourceProxyImpl("myId", new URI("http://testuri"), "test/some-other-type");
	assertEquals(resourceProxy3.hashCode(), resourceProxy.hashCode());
	// Differs because of ID
	ResourceProxy resourceProxy4 = new ResourceProxyImpl("otherId", new URI("http://testuri"), "test/my-mime-type");
	assertFalse(resourceProxy4.hashCode() == resourceProxy.hashCode());
	// Differs because of URI
	ResourceProxy resourceProxy5 = new ResourceProxyImpl("myId", new URI("http://otheruri"), "test/my-mime-type");
	assertFalse(resourceProxy5.hashCode() == resourceProxy.hashCode());
    }

    public class ResourceProxyImpl extends ResourceProxy {

	public ResourceProxyImpl(String id, URI uri, String mimeType) {
	    super(id, uri, mimeType);
	}
    }
}
