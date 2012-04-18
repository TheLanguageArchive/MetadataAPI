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

import java.util.List;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.model.MetadataElement;
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
public class CMDIContainerMetadataElementTest extends CMDIMetadataElementTest {

    ComponentType collectionType;
    ComponentType originLocationType;
    ComponentType generalInfoType;
    CMDIContainerMetadataElement collection;
    CMDIContainerMetadataElement originLocation;
    CMDIDocument document;

    @Before
    public void setUp() throws Exception {
	collectionType = (ComponentType) getNewTestProfileAndRead().getContainableTypeByName("Collection");
	originLocationType = (ComponentType) collectionType.getContainableTypeByName("OriginLocation");
	generalInfoType = (ComponentType) collectionType.getContainableTypeByName("GeneralInfo");
	document = new CMDIDocument(null);
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
	final CMDIContainerMetadataElementImpl originLocation2 = new CMDIContainerMetadataElementImpl(originLocationType, document);
	assertTrue(collection.addChildElement(originLocation2));
	List<MetadataElement> children = collection.getChildren();
	assertEquals(2, children.size());
	assertEquals(2, collection.getChildrenCount(originLocationType));
	// should have been added after first originLocation
	assertTrue(children.indexOf(originLocation) < children.indexOf(originLocation2));

	// Add GeneralInfo, which should appear before OriginLocation
	final CMDIContainerMetadataElementImpl generalInfo = new CMDIContainerMetadataElementImpl(generalInfoType, document);
	assertTrue(collection.addChildElement(generalInfo));
	children = collection.getChildren();
	assertEquals(3, children.size());
	// should have been added before originLocation (profile determines order)
	assertTrue(children.indexOf(generalInfo) < children.indexOf(originLocation));
    }

    @Test
    public void testGetName() {
	final CMDIContainerMetadataElementImpl generalInfo = new CMDIContainerMetadataElementImpl(generalInfoType, document);
	assertEquals("GeneralInfo", generalInfo.getName());
    }

    @Test
    public void testGetDisplayValue() throws MetadataElementException {
	final ElementType nameType = (ElementType) generalInfoType.getContainableTypeByName("Name");
	assertNotNull("Type not found in schema", nameType);
	final ElementType titleType = (ElementType) generalInfoType.getContainableTypeByName("Title");
	assertNotNull("Type not found in schema", titleType);

	final CMDIContainerMetadataElementImpl generalInfo = new CMDIContainerMetadataElementImpl(generalInfoType, document);

	// No children, name equals type name
	assertEquals("GeneralInfo", generalInfo.getDisplayValue());

	// Add element with displayPriority == 0
	final Element name = new Element(nameType, generalInfo, "nameValue");
	generalInfo.addChildElement(name);
	// No displayPriority children, still type name
	assertEquals("GeneralInfo", generalInfo.getDisplayValue());

	// Add element with displayPriority == 1
	final Element title = new Element(titleType, generalInfo, "titleValue");
	generalInfo.addChildElement(title);
	// Value of lowest display priority child
	assertEquals("titleValue", generalInfo.getDisplayValue());

	// Add another element with displayPriority == 1
	final Element title2 = new Element(titleType, generalInfo, "title2Value");
	generalInfo.addChildElement(title2);
	assertEquals("titleValue", generalInfo.getDisplayValue());

	// Change display priorities
	titleType.setDisplayPriority(2);
	nameType.setDisplayPriority(1);
	// Now Name has a lower priority, so should provide display value
	assertEquals("nameValue", generalInfo.getDisplayValue());

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
	CMDIContainerMetadataElementImpl originLocation2 = new CMDIContainerMetadataElementImpl(originLocationType, document);
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

    /**
     * Test of getPathForNewElement method, of class CMDIContainerMetadataElement.
     */
    @Test
    public void testGetChildElement() throws Exception {
	ComponentType locationType = (ComponentType) originLocationType.getContainableTypeByName("Location");
	CMDIContainerMetadataElement location1 = new CMDIContainerMetadataElementImpl(locationType, document);
	CMDIContainerMetadataElement location2 = new CMDIContainerMetadataElementImpl(locationType, document);

	collection.addChildElement(originLocation);

	assertEquals(originLocation, collection.getChildElement("OriginLocation"));
	assertEquals(originLocation, collection.getChildElement("OriginLocation[1]"));

	originLocation.addChildElement(location1);
	originLocation.addChildElement(location2);

	assertEquals(location1, collection.getChildElement("OriginLocation/Location"));
	assertEquals(location1, collection.getChildElement("OriginLocation[1]/Location[1]"));
	assertEquals(location2, collection.getChildElement("OriginLocation/Location[2]"));
	assertNull(collection.getChildElement("NoSuchChildNode"));

	assertEquals(location1, originLocation.getChildElement(locationType, 0));
	assertEquals(location2, originLocation.getChildElement(locationType, 1));
	assertNull(originLocation.getChildElement(collectionType, 0));
    }

    /**
     * Test of getPathForNewElement method, of class CMDIContainerMetadataElement.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetChildIllegalPath() throws Exception {
	collection.getChildElement("/");
    }

    /**
     * Test of getPathForNewElement method, of class CMDIContainerMetadataElement.
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

    private class CMDIContainerMetadataElementImpl extends CMDIContainerMetadataElement {

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
