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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataReference;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.model.ResourceReference;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;

/**
 * Abstract base class for Component and Profile instance classes
 * @see Component
 * @see CMDIDocument
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIContainerMetadataElement extends CMDIMetadataElement implements MetadataContainer<CMDIMetadataElement> {

    private ComponentType type;
    private List<CMDIMetadataElement> children;
    private final Map<String, List<CMDIMetadataElement>> childrenMap;

    public CMDIContainerMetadataElement(final ComponentType type) {
	this.type = type;
	this.children = Collections.synchronizedList(new ArrayList<CMDIMetadataElement>());
	this.childrenMap = new HashMap<String, List<CMDIMetadataElement>>();
    }

    public int getChildrenCount(CMDIProfileElement childType) {
	if (childrenMap.containsKey(childType.getName())) {
	    return childrenMap.get(childType.getName()).size();
	} else {
	    return 0;
	}
    }

    public synchronized void addChildElement(CMDIMetadataElement element) {
	if (!children.contains(element)) {
	    if (children.add(element)) {
		final String typeName = element.getType().getName();
		List<CMDIMetadataElement> elements = childrenMap.get(typeName);
		if (elements == null) {
		    elements = new ArrayList<CMDIMetadataElement>();
		    childrenMap.put(typeName, elements);
		}
		elements.add(element);
	    }
	}
    }

    public synchronized void removeChildElement(CMDIMetadataElement element) {
	if (children.remove(element)) {
	    List<CMDIMetadataElement> elements = childrenMap.get(element.getType().getName());
	    if (elements == null) {
		throw new AssertionError("No list in children map for removed child element");
	    }
	    elements.remove(element);
	}
    }

    public CMDIMetadataElement getChildElement(final String path) {
	// e.g. Actor[1]/Language -> (Actor)([(1)])(/(Language)) -> 
	final Pattern pathPattern = Pattern.compile("(^[^(/|\\[]+)(\\[(\\d+)\\])?(/(.*))?$");
	final Matcher pathMatcher = pathPattern.matcher(path);
	if (pathMatcher.find()) {
	    final String elementName = pathMatcher.group(1);
	    final String elementIndexString = pathMatcher.group(3);
	    final String childPath = pathMatcher.group(5);

	    return getChildElement(elementName, elementIndexString, childPath);
	}
	return null;
    }

    private synchronized CMDIMetadataElement getChildElement(final String elementName, final String elementIndexString, final String childPath) throws NumberFormatException {
	final List<CMDIMetadataElement> elements = childrenMap.get(elementName);
	if (elements != null) {
	    final int elementIndex = (elementIndexString == null || elementIndexString.length() == 0)
		    ? 0
		    : Integer.valueOf(elementIndexString) - 1; // In the path, counting starts at 1, so substract to get array index
	    final CMDIMetadataElement childElement = elements.get(elementIndex);
	    if (childPath != null && childPath.length() > 0) {
		// Path specifies child path
		if (childElement instanceof CMDIContainerMetadataElement) {
		    return ((CMDIContainerMetadataElement) childElement).getChildElement(childPath);
		}
	    } else {
		// End of path
		return childElement;
	    }
	}
	return null;
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
}
