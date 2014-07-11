/*
 * Copyright (C) 2014 Max Planck Institute for Psycholinguistics
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

import java.util.List;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ElementType;

/**
 *
 * @author wilelb
 */
public class DefaultDisplayValueStrategy implements DisplayValueStrategy {
    @Override
    public String getDisplayValue(List<MetadataElement> children, MetadataElement element) {
        //Try to get a display value from one of the prioritized child elements
        boolean component = element.getType() instanceof ComponentType;
	String displayValue = getDisplayNameFromElementType(children, component);
	if (displayValue != null) {
	    return displayValue;
        }
        
        //Failed so far, try to get a display value from the first child component
        displayValue = getDisplayNameFromFirstComponentType(children);
	if (displayValue != null) {
	    return displayValue;
        }
    
        //Everything failed, just return the element name
        return element.getName();
    }
    
    protected String getDisplayNameFromElementType(List<MetadataElement> children, boolean component) {
        int minPriority = 0;
	String displayValue = null;
	// Look for Element (field) children
	for (MetadataElement child : children) {
            MetadataElementType type = child.getType();
	    if (type instanceof ElementType) {
		final String childDisplayValue = child.getDisplayValue();
		// Only consider if it has a valid display value
		if (null != childDisplayValue && !"".equals(childDisplayValue)) {
		    // Check child priority, 0 == no priority
		    final int childPriority = ((ElementType) child.getType()).getDisplayPriority();
		    if (childPriority > 0 && (minPriority == 0 || minPriority > childPriority)) {
			// Lowest priority thus far. Use display value!
			minPriority = childPriority;
			displayValue = childDisplayValue;
		    }
		}
	    } 
	}
        return displayValue;
    }
    
    protected String getDisplayNameFromFirstComponentType(List<MetadataElement> children) {
        String displayValue = null;
        for (MetadataElement child : children) {
            MetadataElementType type = child.getType();
            if (type instanceof ComponentType) {
                displayValue = child.getDisplayValue();
                break;
            } 
        }
        return displayValue;
    }
}
