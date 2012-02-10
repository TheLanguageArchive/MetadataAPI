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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.transform.TransformerException;
import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataReference;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.model.ResourceReference;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;

/**
 * Abstract base class for Component and Profile instance classes
 * @see Component
 * @see CMDIDocument
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIContainerMetadataElement extends CMDIMetadataElement implements MetadataContainer<CMDIMetadataElement> {

    private ComponentType type;
    private List<CMDIMetadataElement> children;

    public CMDIContainerMetadataElement(ComponentType type) {
	this.type = type;
	this.children = Collections.synchronizedList(new ArrayList<CMDIMetadataElement>());
    }

    public CMDIMetadataElement getChildElement(String path) throws IllegalArgumentException {
	try {
	    Node domNode = XPathAPI.selectSingleNode(getDomNode(), path);
	    if (domNode == null) {
		return null;
	    } else {
		return getMetadataDocument().getElementFromMap(domNode);
	    }
	} catch (TransformerException transformerException) {
	    throw new IllegalArgumentException("Could not apply provided XPath to document: " + path, transformerException);
	}
    }

    public void addChildElement(CMDIMetadataElement element) {
	getMetadataDocument().addElementToMap(element);
	children.add(element);
    }

    public void removeChildElement(CMDIMetadataElement element) {
	getMetadataDocument().removeElementFromMap(element);
	children.remove(element);
    }

    /**
     * 
     * @return An <em>unmodifiable</em> copy of the list of children
     */
    public List<CMDIMetadataElement> getChildren() {
	return Collections.unmodifiableList(children);
    }

    public void addMetadataElementListener(MetadataElementListener listener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeMetadataElementListener(MetadataElementListener listener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<Reference> getReferences() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public ResourceReference createResourceReference(URI uri, String mimetype) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataReference createMetadataReference(URI uri, String mimetype) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
	return type.getName();
    }

    public ComponentType getType() {
	return type;
    }
//    public synchronized String insertElement(String path, CMDIMetadataElement element) throws MetadataDocumentException {
//	if (path == null) {
//	    // Add to root
//	    metadataElements.put(null, element);
//	} else {
//	    // Add to child identified by XPath
//	    CMDIMetadataElement parentElement = metadataElements.get(path);
//	    if (parentElement instanceof MetadataContainer) {
//		try {
//		    // Add to element object
//		    ((MetadataContainer) parentElement).addChild(element);
//		    // Add to elements table
//		    metadataElements.put(path, element);
//		} catch (MetadataElementException elEx) {
//		    throw new MetadataDocumentException(this, "Error while adding element to child element of document", elEx);
//		}
//	    } else {
//		throw new MetadataDocumentException(this, "Attempt to insert element failed. Parent XPath not found or node cannot contain children: " + path);
//	    }
//	}
//
//	final String newElementPath = appendToXpath(path, element.getName());
//	((CMDIMetadataElement) element).setPath(newElementPath);
//
//	for (MetadataDocumentListener listener : listeners) {
//	    listener.elementInserted(this, element);
//	}
//	return newElementPath;
//    }
//
//    public synchronized CMDIMetadataElement removeElement(String path) {
//	CMDIMetadataElement result = metadataElements.remove(path);
//	if (result != null) {
//	    for (MetadataDocumentListener listener : listeners) {
//		listener.elementRemoved(this, result);
//	    }
//	}
//	return result;
//    }
}
