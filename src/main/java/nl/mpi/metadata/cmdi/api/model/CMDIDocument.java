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
import java.util.HashSet;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A CMDI metadata document. Instance of a CMDIProfile
 * @see CMDIProfile
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocument extends CMDIContainerMetadataElement implements MetadataDocument<CMDIMetadataElement> {

    private CMDIProfile profile;
    private URI fileLocation;
    private final Collection<HeaderInfo> headerInfo;
    private final Collection<MetadataDocumentListener> listeners;
    private Document domDocument;

    /**
     * Construct an unsaved profile instance (no location associated)
     * @param profile 
     */
    public CMDIDocument(Document domDocument, CMDIProfile profile) {
	this(domDocument, profile, null);
    }

    /**
     * Create a profile instance that has a location associated
     * @param profile
     * @param fileLocation 
     */
    public CMDIDocument(Document domDocument, CMDIProfile profile, URI fileLocation) {
	super(profile);

	this.profile = profile;
	this.fileLocation = fileLocation;
	this.domDocument = domDocument;

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

    /**
     * 
     * @return This document
     */
    @Override
    public CMDIDocument getMetadataDocument() {
	return this;
    }

    @Override
    public Node getDomNode() {
	return domDocument;
    }
}
