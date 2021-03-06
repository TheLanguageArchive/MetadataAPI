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
package nl.mpi.metadata.identifierresolver;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ChainingIdentifierResolverTest {

    /**
     * Test of canResolve method, of class ChainingIdentifierResolver.
     */
    @Test
    public void testCanResolve() throws Exception {
	URI testUri = new URI("test");

	List<IdentifierResolver> resolverList = new ArrayList<IdentifierResolver>();
	resolverList.add(new MockIdentifierResolver(false, new URL("file://first")));
	resolverList.add(new MockIdentifierResolver(false, new URL("file://second")));
	ChainingIdentifierResolver resolver = new ChainingIdentifierResolver(resolverList);

	assertFalse(resolver.canResolve(null, testUri));

	resolverList.add(new MockIdentifierResolver(true, new URL("file://third")));
	resolver.setChain(resolverList);

	assertTrue(resolver.canResolve(null, testUri));

	resolverList.add(new MockIdentifierResolver(true, new URL("file://fourth")));
	resolver.setChain(resolverList);

	assertTrue(resolver.canResolve(null, testUri));
    }

    /**
     * Test of resolveIdentifier method, of class ChainingIdentifierResolver.
     */
    @Test
    public void testResolveIdentifier() throws Exception {
	URI testUri = new URI("test");
	ChainingIdentifierResolver resolver = new ChainingIdentifierResolver();

	List<IdentifierResolver> resolverList = new ArrayList<IdentifierResolver>();
	resolverList.add(new MockIdentifierResolver(false, new URL("file://first")));
	resolverList.add(new MockIdentifierResolver(false, new URL("file://second")));
	resolverList.add(new MockIdentifierResolver(true, new URL("file://third")));
	resolver.setChain(resolverList);

	assertEquals(new URL("file://third"), resolver.resolveIdentifier(null, testUri));

	resolverList.add(new MockIdentifierResolver(true, new URL("file://fourth")));
	resolver.setChain(resolverList);

	assertEquals(new URL("file://third"), resolver.resolveIdentifier(null, testUri));
    }
}
