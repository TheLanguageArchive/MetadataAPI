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

import java.util.List;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ElementType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIContainerMetadataElementImplTest extends CMDIMetadataElementImplTest {
    
    ComponentType collectionType;
    ComponentType originLocationType;
    ComponentType generalInfoType;
    CMDIContainerMetadataElement collection;
    CMDIContainerMetadataElement originLocation;
    CMDIDocument document;
    
    @Before
    public void setUp() throws Exception {
	CMDIProfile profile = getNewTestProfileAndRead();;
	document = new CMDIDocumentImpl(profile);
	collectionType = (ComponentType) getNewTestProfileAndRead().getContainableTypeByName("Collection");
	originLocationType = (ComponentType) collectionType.getContainableTypeByName("OriginLocation");
	generalInfoType = (ComponentType) collectionType.getContainableTypeByName("GeneralInfo");
	collection = new CMDIContainerMetadataElementImpl(collectionType, document);
	originLocation = new CMDIContainerMetadataElementImpl(originLocationType, document);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testAddChildElement() throws Exception {
	assertEquals(0, collection.getChildren().size());
	assertEquals(0, collection.getChildrenCount(originLocationType));

	// Add a new child
	assertTrue(collection.addChildElement(originLocation));
	assertEquals(1, collection.getChildren().size());
	assertEquals(1, collection.getChildrenCount(originLocationType));

	// Add the same child (should not get added)
	assertFalse(collection.addChildElement(originLocation));
	assertEquals(1, collection.getChildren().size());
	assertEquals(1, collection.getChildrenCount(originLocationType));

	// Add an aditional child
	final CMDIContainerMetadataElement originLocation2 = new CMDIContainerMetadataElementImpl(originLocationType, document);
	assertTrue(collection.addChildElement(originLocation2));
	List<MetadataElement> children = collection.getChildren();
	assertEquals(2, children.size());
	assertEquals(2, collection.getChildrenCount(originLocationType));
	// should have been added after first originLocation
	assertTrue(children.indexOf(originLocation) < children.indexOf(originLocation2));

	// Add GeneralInfo, which should appear before OriginLocation
	final CMDIContainerMetadataElement generalInfo = new CMDIContainerMetadataElementImpl(generalInfoType, document);
	assertTrue(collection.addChildElement(generalInfo));
	children = collection.getChildren();
	assertEquals(3, children.size());
	// should have been added before originLocation (profile determines order)
	assertTrue(children.indexOf(generalInfo) < children.indexOf(originLocation));
    }
    
    @Test
    public void testGetName() {
	final CMDIContainerMetadataElement generalInfo = new CMDIContainerMetadataElementImpl(generalInfoType, document);
	assertEquals("GeneralInfo", generalInfo.getName());
    }
    
    @Test
    public void testGetDisplayValue() throws MetadataElementException {
	final ElementType nameType = (ElementType) generalInfoType.getContainableTypeByName("Name");
	assertNotNull("Type not found in schema", nameType);
	final ElementType titleType = (ElementType) generalInfoType.getContainableTypeByName("Title");
	assertNotNull("Type not found in schema", titleType);
	
	final CMDIContainerMetadataElement generalInfo = new CMDIContainerMetadataElementImpl(generalInfoType, document);
	// No children, name equals type name
	assertEquals("GeneralInfo", generalInfo.getDisplayValue());

	// Add a component child element
	generalInfo.addChildElement(originLocation);
	// No element children, name equals type name
	assertEquals("GeneralInfo", generalInfo.getDisplayValue());

	// Add element with displayPriority == 0
	final Element name = new ElementImpl(nameType, generalInfo, "nameValue");
	generalInfo.addChildElement(name);
	// No displayPriority children, still type name
	assertEquals("GeneralInfo", generalInfo.getDisplayValue());

	// Add element with displayPriority == 1
	final Element title = new ElementImpl(titleType, generalInfo, "titleValue");
	generalInfo.addChildElement(title);
	// Value of lowest display priority child
	assertEquals("titleValue", generalInfo.getDisplayValue());

	// Add another element with displayPriority == 1
	final ElementImpl title2 = new ElementImpl(titleType, generalInfo, "title2Value");
	generalInfo.addChildElement(title2);
	assertEquals("titleValue", generalInfo.getDisplayValue());

	// Change display priorities
	titleType.setDisplayPriority(2); // used to be 1
	nameType.setDisplayPriority(1); // used to be 0

	// Now Name has a higher priority, so should provide display value
	assertEquals("nameValue", generalInfo.getDisplayValue());

	// Higher priority elements without proper value get ignored
	name.setValue(""); // will skip Name
	assertEquals("titleValue", generalInfo.getDisplayValue());
	name.setValue(null); // will skip Name
	assertEquals("titleValue", generalInfo.getDisplayValue());
    }
    
    @Test
    public void testRemoveChildElement() throws Exception {
	assertEquals(0, collection.getChildren().size());
	// Add two children
	assertTrue(collection.addChildElement(originLocation));
	assertTrue(collection.addChildElement(new CMDIContainerMetadataElementImpl(originLocationType, document)));
	assertEquals(2, collection.getChildren().size());
	// Remove first child
	assertTrue(collection.removeChildElement(originLocation));
	assertEquals(1, collection.getChildren().size());
	// Remove non-child (should not work)
	assertFalse(collection.removeChildElement(new CMDIContainerMetadataElementImpl(originLocationType, document)));
    }
    
    @Test
    public void testRemoveChildElementGetByPath() throws Exception {
	CMDIContainerMetadataElement originLocation2 = new CMDIContainerMetadataElementImpl(originLocationType, document);
	collection.addChildElement(originLocation);
	collection.addChildElement(originLocation2);
	// Get both by path
	assertEquals(originLocation, collection.getChildElement("OriginLocation[1]"));
	assertEquals(originLocation2, collection.getChildElement("OriginLocation[2]"));

	// Remove first child
	collection.removeChildElement(originLocation);
	// Second child should now be first
	assertEquals(originLocation2, collection.getChildElement("OriginLocation[1]"));
	try {
	    collection.getChildElement("OriginLocation[2]");
	    // Child should no longer be there
	    fail("Expected IndexOutOfBoundsException");
	} catch (IndexOutOfBoundsException ex) {
	    // Good
	}
    }
    
    @Test
    public void testCanAddInstanceOfType() throws Exception {
	// Error on type level (cannot contain itself)
	assertFalse(collection.canAddInstanceOfType(collectionType));
	// Error on type level (cannot contain grandchild)
	CMDIProfileElement nameType = generalInfoType.getContainableTypeByName("Name");
	assertFalse(collection.canAddInstanceOfType(nameType));
	// Should work because of type
	assertTrue(collection.canAddInstanceOfType(generalInfoType));

	// Add GeneralInfo element to Container
	CMDIContainerMetadataElement generalInfo = new CMDIContainerMetadataElementImpl(generalInfoType, document);
	collection.addChildElement(generalInfo);

	// There can be only one so now should return false
	assertFalse(collection.canAddInstanceOfType(generalInfoType));
    }

    /**
     * Test of getPathForNewElement method, of class CMDIContainerMetadataElementImpl.
     */
    @Test
    public void testGetChildElement() throws Exception {
	ComponentType locationType = (ComponentType) originLocationType.getContainableTypeByName("Location");
	CMDIContainerMetadataElement location1 = new CMDIContainerMetadataElementImpl(locationType, document);
	CMDIContainerMetadataElement location2 = new CMDIContainerMetadataElementImpl(locationType, document);
	
	document.addChildElement(collection);
	collection.addChildElement(originLocation);
	
	assertEquals(originLocation, collection.getChildElement("OriginLocation"));
	assertEquals(originLocation, collection.getChildElement("OriginLocation[1]"));
	
	originLocation.addChildElement(location1);
	originLocation.addChildElement(location2);
	
	assertEquals(location1, collection.getChildElement("OriginLocation/Location"));
	assertEquals(location1, collection.getChildElement("OriginLocation[1]/Location[1]"));
	assertEquals(location2, collection.getChildElement("OriginLocation/Location[2]"));
	assertNull(collection.getChildElement("NoSuchChildNode"));

	// Test paths with namespaces (should be ignored at this stage)
	assertEquals(location1, collection.getChildElement(":OriginLocation/:Location"));
	assertEquals(location1, collection.getChildElement("OriginLocation[1]/:Location[1]"));
	assertEquals(location1, collection.getChildElement(":OriginLocation[1]/Location[1]"));
	assertEquals(location2, collection.getChildElement(":OriginLocation/:Location[2]"));
	assertNull(collection.getChildElement(":NoSuchChildNode"));
	
	assertEquals(location1, originLocation.getChildElement(locationType, 0));
	assertEquals(location2, originLocation.getChildElement(locationType, 1));
	assertNull(originLocation.getChildElement(collectionType, 0));
	
	// Get from root
	assertEquals(collection, collection.getChildElement("/:CMD/:Components/:TextCorpusProfile/:Collection"));
	assertEquals(originLocation, collection.getChildElement("/:CMD/:Components/:TextCorpusProfile/:Collection/:OriginLocation"));
    }

    /**
     * Test of getPathForNewElement method, of class CMDIContainerMetadataElementImpl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetChildIllegalPath() throws Exception {
	collection.getChildElement("/");
    }

    /**
     * Test of getPathForNewElement method, of class CMDIContainerMetadataElementImpl.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetChildElementIndexOutOfBound() throws Exception {
	collection.addChildElement(originLocation);
	collection.getChildElement(originLocationType, 1);
    }
    
    @Override
    CMDIMetadataElement getInstance() {
	return collection;
    }
    
    @Override
    CMDIDocument getDocument() {
	return document;
    }
    
    private class CMDIContainerMetadataElementImpl extends nl.mpi.metadata.cmdi.api.model.impl.CMDIContainerMetadataElementImpl {
	
	private final CMDIDocument document;
	
	public CMDIContainerMetadataElementImpl(ComponentType type, CMDIDocument document) {
	    super(type);
	    this.document = document;
	}
	
	@Override
	public CMDIDocument getMetadataDocument() {
	    return document;
	}
    }
}
