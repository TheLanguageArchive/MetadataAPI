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

    /**
     * Finds the number children of this element that are of the specified type
     * @param childType metadata type to look for
     * @return number of childern of the specified type
     */
    public int getChildrenCount(CMDIProfileElement childType) {
	if (childrenMap.containsKey(childType.getName())) {
	    return childrenMap.get(childType.getName()).size();
	} else {
	    return 0;
	}
    }

    /**
     * Adds the provided element as a child
     * @param element element to add
     * @return whether the child was added. Will be false if the child is already registered as a child.
     */
    public synchronized boolean addChildElement(CMDIMetadataElement element) {
	if (!children.contains(element)) {
	    if (children.add(element)) {
		final String typeName = element.getType().getName();
		List<CMDIMetadataElement> elements = childrenMap.get(typeName);
		if (elements == null) {
		    elements = new ArrayList<CMDIMetadataElement>();
		    childrenMap.put(typeName, elements);
		}
		if (!elements.add(element)) {
		    throw new AssertionError("Child was added but could not be registered in map");
		}
		return true;
	    }
	}
	return false;
    }

    /**
     * Removes a child from this element
     * @param element element to remove
     * @return  whether the child was removed. Will be false if the child is not registered as a child.
     */
    public synchronized boolean removeChildElement(CMDIMetadataElement element) {
	if (children.remove(element)) {
	    List<CMDIMetadataElement> elements = childrenMap.get(element.getType().getName());
	    if (elements == null) {
		throw new AssertionError("No list in children map for removed child element");
	    }
	    if (!elements.remove(element)) {
		throw new AssertionError("Child was removed but could not be deleted from map");
	    }
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Gets a child element by type and index
     * @param type type of the child to get
     * @param index index of the element to return
     * @return child, if found; null if no children of the specified type are contained in this element
     * @throws IndexOutOfBoundsException if children of the specified type do exist, but the specified index is outside the bounds of the collection
     */
    public CMDIMetadataElement getChildElement(CMDIProfileElement type, int index) throws IndexOutOfBoundsException {
	List<CMDIMetadataElement> elements = childrenMap.get(type.getName());
	if (elements == null) {
	    return null;
	} else {
	    return elements.get(index);
	}
    }

    /**
     * Gets a child element of this node, selected by the specified path.
     * 
     * This method supports a subset of XPath. Examples:
     * <ul>
     *	<li><em>Actor</em> 
     *	    will get the first child of the type Actor</li>
     *	<li><em>Actor[1]</em> 
     *	    will also get the first child of the type Actor</li>
     *	<li><em>Actor[2]</em>
     *	    will get the second child of the type Actor</li>
     *	<li><em>Actor/Language[2]</em>
     *	    will get the second child of the type Language of the first child of the node Actor of this node</li>
     * </ul>
     * 
     * Among other things, the following features are <strong>not supported</strong>:
     * <ul>
     *	<li>alternative starting nodes (e.g. <em>../Actor[3]</em>)</li>
     *	<li>retrieving attributes (e.g. <em>Actor[2]/@name</em>)</li>
     *	<li>conditions (e.g. <em>Actor[@name='Joe']</em>)</li>
     * </ul>
     * @param path specification of child element to return
     * @return the child at the specified path, if found; otherwise null
     * @throws IllegalArgumentException if the format of the path is illegal
     */
    public CMDIMetadataElement getChildElement(final String path) throws IllegalArgumentException {
	// e.g. Actor[1]/Language -> (Actor)([(1)])(/(Language)) -> 
	final Pattern pathPattern = Pattern.compile("(^[^(/|\\[]+)(\\[(\\d+)\\])?(/(.*))?$");
	final Matcher pathMatcher = pathPattern.matcher(path);
	if (pathMatcher.find()) {
	    final String elementName = pathMatcher.group(1);
	    if (elementName != null && elementName.length() > 0) {
		final String elementIndexString = pathMatcher.group(3);
		final String childPath = pathMatcher.group(5);

		return getChildElement(elementName, elementIndexString, childPath);
	    }
	}
	throw new IllegalArgumentException(String.format("Path does not match accepted pattern: %1$s", path));
    }

    /**
     * Gets a child element 
     * @param elementName name of local element to find
     * @param elementIndexString if null or empty, will default to '1'
     * @param childPath path that should be propagated to child
     * @return child element if match is found. Null if not found.
     * @throws NumberFormatException 
     */
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
