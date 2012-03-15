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

import java.util.Collections;
import java.util.List;
import nl.mpi.metadata.api.type.MetadataContainerElementType;
import org.apache.xmlbeans.SchemaProperty;

/**
 * This class represents a CMDI component definition, defined by http://www.clarin.eu/cmd/general-component-schema.xsd
 * 
 * For example components, see http://www.clarin.eu/cmd/example/
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ComponentType extends CMDIProfileElement implements MetadataContainerElementType<CMDIProfileElement> {

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

    protected StringBuilder getPath() {
	return path;
    }

    protected void setChildren(List<CMDIProfileElement> children) {
	this.children = children;
    }
    
    protected void setComponentId(String componentId){
	this.componentId = componentId;
    }

    @Override
    public String getPathString() {
	return path.toString();
    }
}
