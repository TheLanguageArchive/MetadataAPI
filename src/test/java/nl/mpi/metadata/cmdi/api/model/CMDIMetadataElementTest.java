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
import java.util.Collection;
import java.util.UUID;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIMetadataElementTest extends CMDIAPITestCase {

    @Test
    public void testAddDocumentResourceProxyReference() throws Exception {
	String newId = UUID.randomUUID().toString();
	// Add unknown proxy by id 
	ResourceProxy proxy = getInstance().addDocumentResourceProxyReference(newId);
	assertNull(proxy);
	// Add proxy to document
	proxy = new DataResourceProxy(newId, new URI("http://test"), "test/test");
	getDocument().addDocumentResourceProxy(proxy);
	// Add known proxy by id
	ResourceProxy addedReference = getInstance().addDocumentResourceProxyReference(newId);
	assertSame(proxy, addedReference);
    }

    @Test
    public void testRemoveResourceProxyReference() throws Exception {
	String newId = UUID.randomUUID().toString();
	// Remove unknown proxy by id
	ResourceProxy proxy = getInstance().removeDocumentResourceProxyReference(newId);
	assertNull(proxy);
	// Add proxy to document
	proxy = new DataResourceProxy(newId, new URI("http://test"), "test/test");
	getDocument().addDocumentResourceProxy(proxy);
	// Add reference to element
	assertNotNull(getInstance().addDocumentResourceProxyReference(newId));
	// Remove from element
	ResourceProxy removedProxy = getInstance().removeDocumentResourceProxyReference(newId);
	assertSame(proxy, removedProxy);
	// Remove from element again
	removedProxy = getInstance().removeDocumentResourceProxyReference(newId);
	assertNull(removedProxy);
    }

    @Test
    public void testGetReferences() throws Exception {
	// Add ref to document
	String newId = UUID.randomUUID().toString();
	ResourceProxy proxy = new DataResourceProxy(newId, new URI("http://test"), "test/test");
	getDocument().addDocumentResourceProxy(proxy);

	// References in element should be empty
	Collection<ResourceProxy> references = getInstance().getReferences();
	assertEquals(0, references.size());

	// Add to element
	getInstance().addDocumentResourceProxyReference(newId);
	// Get from element
	references = getInstance().getReferences();
	assertEquals(1, references.size());
	assertSame(proxy, references.iterator().next());
    }

    @Test
    public void testCreateResourceReference() throws Exception {
	// Create reference on element
	DataResourceProxy createdReference = getInstance().createResourceReference(new URI("http://test"), "test/test");
	assertNotNull(createdReference);
	// Get reference from element
	Collection<ResourceProxy> references = getInstance().getReferences();
	assertEquals(1, references.size());
	assertSame(createdReference, references.iterator().next());
	// Should also be on document
	ResourceProxy documentResourceProxy = getDocument().getDocumentResourceProxy(createdReference.getId());
	assertSame(createdReference, documentResourceProxy);
    }

    @Test
    public void testCreateMetadataReference() throws Exception {
	// Create reference on element
	MetadataResourceProxy createdReference = getInstance().createMetadataReference(new URI("http://test"), "test/test");
	assertNotNull(createdReference);
	// Get reference from element
	Collection<ResourceProxy> references = getInstance().getReferences();
	assertEquals(1, references.size());
	assertSame(createdReference, references.iterator().next());
	// Should also be on document
	ResourceProxy documentResourceProxy = getDocument().getDocumentResourceProxy(createdReference.getId());
	assertSame(createdReference, documentResourceProxy);
    }

    @Test
    public void testRemoveReference() throws Exception {
	// Create metadata reference on element
	MetadataResourceProxy createdMetadataReference = getInstance().createMetadataReference(new URI("http://test"), "test/test");
	assertNotNull(createdMetadataReference);
	Collection<ResourceProxy> references = getInstance().getReferences();
	assertEquals(1, references.size());

	// Create resource reference on element
	DataResourceProxy createdResourceReference = getInstance().createResourceReference(new URI("http://test"), "test/test");
	assertNotNull(createdResourceReference);
	references = getInstance().getReferences();
	assertEquals(2, references.size());

	// Remove metadata reference
	Reference removedMetadataReference = getInstance().removeReference(createdMetadataReference);
	assertSame(removedMetadataReference, createdMetadataReference);
	references = getInstance().getReferences();
	assertEquals(1, references.size());

	assertNull(getInstance().removeReference(createdMetadataReference));

	// Remove resource reference
	Reference removedResourceReference = getInstance().removeReference(createdResourceReference);
	assertSame(removedResourceReference, createdResourceReference);
	references = getInstance().getReferences();
	assertEquals(0, references.size());

	assertNull(getInstance().removeReference(createdResourceReference));
    }

    abstract CMDIMetadataElement getInstance();

    abstract CMDIDocument getDocument();
}
