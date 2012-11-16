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
package nl.mpi.metadata.cmdi.api.model.impl;

import java.net.URI;
import java.util.Collection;
import java.util.UUID;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIMetadataElementImplTest extends CMDIAPITestCase {

    @Test
    public void testAddDocumentResourceProxyReference() throws Exception {
	getInstance().setDirty(false);

	String newId = UUID.randomUUID().toString();
	// Add unknown proxy by id 
	ResourceProxy proxy = getInstance().addDocumentResourceProxyReference(newId);
	assertNull(proxy);
	assertFalse(getInstance().isDirty());
	// Add proxy to document
	proxy = new DataResourceProxy(newId, new URI("http://test"), "test/test");
	getDocument().addDocumentResourceProxy(proxy);
	// Add known proxy by id
	ResourceProxy addedReference = getInstance().addDocumentResourceProxyReference(newId);
	assertSame(proxy, addedReference);
	assertTrue(getInstance().isDirty());
    }

    @Test
    public void testRemoveResourceProxyReference() throws Exception {
	getInstance().setDirty(false);

	String newId = UUID.randomUUID().toString();
	// Remove unknown proxy by id
	ResourceProxy proxy = getInstance().removeDocumentResourceProxyReference(newId);
	assertNull(proxy);
	assertFalse(getInstance().isDirty());
	// Add proxy to document
	proxy = new DataResourceProxy(newId, new URI("http://test"), "test/test");
	getDocument().addDocumentResourceProxy(proxy);
	// Add reference to element
	assertNotNull(getInstance().addDocumentResourceProxyReference(newId));
	assertTrue(getInstance().isDirty());
	getInstance().setDirty(false);

	// Remove from element
	ResourceProxy removedProxy = getInstance().removeDocumentResourceProxyReference(newId);
	assertSame(proxy, removedProxy);
	assertTrue(getInstance().isDirty());
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
	Collection<Reference> references = getInstance().getReferences();
	assertEquals(0, references.size());

	// Add to element
	getInstance().addDocumentResourceProxyReference(newId);
	// Get from element
	assertEquals(1, getInstance().getReferencesCount());
	references = getInstance().getReferences();
	assertSame(proxy, references.iterator().next());
    }

    @Test
    public void testCreateResourceReference() throws Exception {
	// Create reference on element
	DataResourceProxy createdReference = getInstance().createResourceReference(new URI("http://test"), "test/test");
	assertNotNull(createdReference);
	// Get reference from element
	assertEquals(1, getInstance().getReferencesCount());
	Collection<Reference> references = getInstance().getReferences();
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
	assertEquals(1, getInstance().getReferencesCount());
	Collection<Reference> references = getInstance().getReferences();
	assertSame(createdReference, references.iterator().next());
	// Should also be on document
	ResourceProxy documentResourceProxy = getDocument().getDocumentResourceProxy(createdReference.getId());
	assertSame(createdReference, documentResourceProxy);
	// Element should be referenced
	assertTrue(getDocument().getResourceProxyReferences(createdReference).contains(getInstance()));
    }

    @Test
    public void testRemoveReference() throws Exception {
	// Create metadata reference on element
	MetadataResourceProxy createdMetadataReference = getInstance().createMetadataReference(new URI("http://test/md"), "test/test");
	assertNotNull(createdMetadataReference);
	assertEquals(1, getInstance().getReferencesCount());

	// Create resource reference on element
	DataResourceProxy createdResourceReference = getInstance().createResourceReference(new URI("http://test/res"), "test/test");
	assertNotNull(createdResourceReference);
	assertEquals(2, getInstance().getReferencesCount());

	// Remove metadata reference
	Reference removedMetadataReference = getInstance().removeReference(createdMetadataReference);
	assertSame(removedMetadataReference, createdMetadataReference);
	assertEquals(1, getInstance().getReferencesCount());
	// Try to remove once more - should have no result
	assertNull(getInstance().removeReference(createdMetadataReference));
	// Reference should have been removed as well
	assertEquals(0, getDocument().getResourceProxyReferences(createdMetadataReference).size());

	// Remove resource reference
	Reference removedResourceReference = getInstance().removeReference(createdResourceReference);
	assertSame(removedResourceReference, createdResourceReference);
	assertEquals(0, getInstance().getReferencesCount());
	// Try to remove once more - should have no result
	assertNull(getInstance().removeReference(createdResourceReference));
	// Reference should have been removed as well
	assertEquals(0, getDocument().getResourceProxyReferences(createdResourceReference).size());
    }

    abstract CMDIMetadataElement getInstance();

    abstract CMDIDocument getDocument();
}
