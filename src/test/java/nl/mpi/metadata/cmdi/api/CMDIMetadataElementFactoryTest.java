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
package nl.mpi.metadata.cmdi.api;

import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIMetadataElementFactoryTest extends CMDIAPITestCase {

    /**
     * Test of createNewMetadataElement method, of class CMDIMetadataElementFactory.
     */
    @Test
    public void testCreateNewComponent() throws Exception {
	System.out.println("createNewMetadataElement");

	CMDIDocument document = getNewTestDocument();

	CMDIProfileElement type = document.getType().getContainableTypeByName("Collection");
	CMDIMetadataElementFactory instance = new CMDIMetadataElementFactory();
	CMDIMetadataElement result = instance.createNewMetadataElement(document, type);

	assertTrue(result instanceof Component);
	assertEquals(type, result.getType());
	assertEquals(document, ((Component) result).getParent());
    }

    /**
     * Test of createNewMetadataElement method, of class CMDIMetadataElementFactory.
     */
    @Test
    public void testCreateNewElement() throws Exception {
	System.out.println("createNewMetadataElement");


	CMDIDocument document = getNewTestDocument();
	ComponentType componentType = (ComponentType) ((ComponentType) document.getType().getContainableTypeByName("Collection")).getContainableTypeByName("GeneralInfo");
	Component component = new Component(componentType, document); // adding GeneralInfo directly to document doesn't really match the model but it doesn't matter here
	CMDIProfileElement type = componentType.getContainableTypeByName("Name");

	CMDIMetadataElementFactory instance = new CMDIMetadataElementFactory();
	CMDIMetadataElement result = instance.createNewMetadataElement(component, type);

	assertTrue(result instanceof Element);
	assertEquals(type, result.getType());
	assertEquals(component, ((Element) result).getParent());
    }
}