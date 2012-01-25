/*
 * Copyright (C) 2011 Max Planck Institute for Psycholinguistics
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.model.ContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;

/**
 * A CMDI metadata document. Instance of a CMDIProfile
 * @see CMDIProfile
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocument extends CMDIContainerMetadataElement implements MetadataDocument<CMDIMetadataElement> {

    private CMDIProfile profile;
    private URI fileLocation;
    private final Collection<HeaderInfo> headerInfo;
    private final Map<String, CMDIMetadataElement> metadataElements;
    private final Collection<MetadataDocumentListener> listeners;

    /**
     * Construct an unsaved profile instance (no location associated)
     * @param profile 
     */
    public CMDIDocument(CMDIProfile profile) {
	this(profile, null);
    }

    /**
     * Create a profile instance that has a location associated
     * @param profile
     * @param fileLocation 
     */
    public CMDIDocument(CMDIProfile profile, URI fileLocation) {
	super(profile);

	this.profile = profile;
	this.fileLocation = fileLocation;

	this.metadataElements = new HashMap<String, CMDIMetadataElement>();
	this.headerInfo = new HashSet<HeaderInfo>();
	this.listeners = new HashSet<MetadataDocumentListener>();
    }

    @Override
    public CMDIProfile getType() {
	return profile;
    }

    public void setType(CMDIProfile profile) {
	this.profile = profile;
    }

    public URI getFileLocation() {
	return fileLocation;
    }

    /**
     * 
     * @return An <em>unmodifiable</em> copy of the collection of header info entries
     */
    public Collection<HeaderInfo> getHeaderInformation() {
	return Collections.unmodifiableCollection(headerInfo);
    }

    public synchronized CMDIMetadataElement getElement(String path) {
	return metadataElements.get(path);
    }

    public synchronized String insertElement(String path, CMDIMetadataElement element) throws MetadataDocumentException {
	if (path == null) {
	    // Add to root
	    metadataElements.put(null, element);
	} else {
	    // Add to child identified by XPath
	    CMDIMetadataElement parentElement = metadataElements.get(path);
	    if (parentElement instanceof ContainerMetadataElement) {
		try {
		    // Add to element object
		    ((ContainerMetadataElement) parentElement).addChild(element);
		    // Add to elements table
		    metadataElements.put(path, element);
		} catch (MetadataElementException elEx) {
		    throw new MetadataDocumentException(this, "Error while adding element to child element of document", elEx);
		}
	    } else {
		throw new MetadataDocumentException(this, "Attempt to insert element failed. Parent XPath not found or node cannot contain children: " + path);
	    }
	}

	final String newElementPath = appendToXpath(path, element.getName());
	((CMDIMetadataElement) element).setPath(newElementPath);

	for (MetadataDocumentListener listener : listeners) {
	    listener.elementInserted(this, element);
	}
	return newElementPath;
    }

    public synchronized CMDIMetadataElement removeElement(String path) {
	CMDIMetadataElement result = metadataElements.remove(path);
	if (result != null) {
	    for (MetadataDocumentListener listener : listeners) {
		listener.elementRemoved(this, result);
	    }
	}
	return result;
    }

    @Override
    public void addChild(CMDIMetadataElement child) throws MetadataElementException {
	try {
	    insertElement(null, child);
	} catch (MetadataDocumentException docEx) {
	    throw new MetadataElementException(child, "Could not add child as element to document", docEx);
	}
	super.addChild(child);
    }

    @Override
    public void removeChild(CMDIMetadataElement child) throws MetadataElementException {
	if (child.equals(metadataElements.get(child.getPath()))) {
	    removeElement(child.getPath());
	} else {
	    throw new MetadataElementException(this, "Element not found as child of this document");
	}
	super.removeChild(child);
    }

    public synchronized void addMetadataDocumentListener(MetadataDocumentListener listener) {
	listeners.add(listener);
    }

    public synchronized void removeMetadataDocumentListener(MetadataDocumentListener listener) {
	listeners.remove(listener);
    }

    /**
     * @return an <em>unmodifiable</em> copy of the MetadataDocumentListeners collection
     */
    public Collection<MetadataDocumentListener> getMetadataDocumentListeners() {
	return Collections.unmodifiableCollection(listeners);
    }

    private String appendToXpath(String xPath, String bitToAdd) {
	final StringBuilder pathBuilder = new StringBuilder();
	if (xPath != null) {
	    pathBuilder.append(xPath);
	}
	pathBuilder.append("\\").append(bitToAdd);
	return pathBuilder.toString();
    }

    /**
     * 
     * @return This document
     */
    @Override
    public CMDIDocument getDocument() {
	return this;
    }
}
