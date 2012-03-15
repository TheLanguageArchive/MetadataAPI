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
package nl.mpi.metadata.cmdi.api.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.MetadataContainerElementType;
import org.apache.xmlbeans.SchemaProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a CMDI component definition, defined by http://www.clarin.eu/cmd/general-component-schema.xsd
 * 
 * For example components, see http://www.clarin.eu/cmd/example/
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ComponentType extends CMDIProfileElement implements MetadataContainerElementType<CMDIProfileElement> {

    private final static Logger logger = LoggerFactory.getLogger(ComponentType.class);
    private final StringBuilder path;
    private List<CMDIProfileElement> children;
    private String componentId;

    /**
     * Constructor. Does not read actual data, for this call #readSchema()
     * @param schemaElement SchemaProperty that represents this component type
     * @param parent Parent component type
     * @see #readSchema() 
     */
    protected ComponentType(SchemaProperty schemaElement, ComponentType parent, StringBuilder path) {
	super(schemaElement, parent);
	this.path = path;
    }

    public CMDIProfileElement getContainableTypeByName(String name) {
	for (CMDIProfileElement child : children) {
	    if (child.getName().equals(name)) {
		return child;
	    }
	}
	return null;
    }

    public List<CMDIProfileElement> getContainableTypes() {
	return Collections.unmodifiableList(children);
    }

    public CMDIProfileElement getType(String name) {
	for (CMDIProfileElement type : children) {
	    if (type.getName().equals(name)) {
		return type;
	    }
	}
	return null;
    }

    public boolean canContainType(CMDIProfileElement type) {
	return children.contains(type);
    }

    public String getComponentId() {
	return componentId;
    }

    /**
     * Reads schema for this component type
     * @throws CMDITypeException  If schema has not been set or loaded
     */
    @Override
    void readSchema() throws CMDITypeException {
	super.readSchema();
	readChildren();
    }

    @Override
    protected void readProperties() {
	super.readProperties();
	SchemaProperty componentIdAttribute = getSchemaElement().getType().getAttributeProperty(new QName("ComponentId"));
	// attribute may not be present, e.g. for profile root component
	componentId = (componentIdAttribute == null) ? null : componentIdAttribute.getDefaultText();
    }

    /**
     * Recursively loads children (components, elements) for this component
     * @throws CMDITypeException 
     */
    private void readChildren() throws CMDITypeException {
	SchemaProperty[] elements = getSchemaElement().getType().getElementProperties();

	if (elements != null && elements.length > 0) {
	    children = new ArrayList<CMDIProfileElement>(elements.length);
	    for (SchemaProperty child : elements) {

		CMDIProfileElement childElement;

		// Is the element a Component (if so, it has ComponentId property)
		boolean isComponent = null != child.getType().getAttributeProperty(new QName("ComponentId"))
			|| child.getType().getElementProperties().length > 0;
		if (isComponent) {
		    // Component id found, so create component
		    logger.debug("Creating child component type {}", child.getName().toString());
		    childElement = new ComponentType(child, this, createChildPath(child));
		} else {
		    // Not a component, so create element
		    if (child.getType().hasStringEnumValues()) {
			logger.debug("Creating child CV element type {}", child.getName().toString());
			childElement = new ControlledVocabularyElementType(child, this, createChildPath(child));
		    } else {
			logger.debug("Creating child element type {}", child.getName().toString());
			childElement = new ElementType(child, this, createChildPath(child));
		    }
		}
		childElement.readSchema();
		children.add(childElement);
	    }
	} else {
	    children = Collections.emptyList();
	}
    }

    private StringBuilder createChildPath(SchemaProperty child) {
	return new StringBuilder(path).append("/:").append(child.getName().getLocalPart());
    }

    @Override
    public String getPathString() {
	return path.toString();
    }
}
