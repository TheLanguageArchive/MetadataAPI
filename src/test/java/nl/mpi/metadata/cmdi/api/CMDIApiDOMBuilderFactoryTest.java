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

import javax.xml.parsers.DocumentBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.EntityResolver;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApiDOMBuilderFactoryTest extends CMDIAPITestCase {

    /**
     * Test of newDOMBuilder method, of class CMDIApiDOMBuilderFactory.
     */
    @Test
    public void testNewDOMBuilder() {
	CMDIApiDOMBuilderFactory instance = new CMDIApiDOMBuilderFactory() {

	    public EntityResolver getEntityResolver() {
		return CMDI_API_TEST_ENTITY_RESOLVER;
	    }
	};
	DocumentBuilder result = instance.newDOMBuilder();
	assertNotNull(result);
    }

    /**
     * Test of getEntityResolver method, of class CMDIApiDOMBuilderFactory.
     */
    @Test
    public void testGetEntityResolver() throws Exception {
	final TracingEntityResolver entityResolver = new TracingEntityResolver();

	CMDIApiDOMBuilderFactory instance = new CMDIApiDOMBuilderFactory() {

	    @Override
	    protected EntityResolver getEntityResolver() {
		return entityResolver;
	    }
	};
	assertEquals(entityResolver, instance.getEntityResolver());
	
	// TODO: trigger entity resolver
//	DocumentBuilder builder = instance.newDOMBuilder();
//	assertFalse(entityResolver.isTriggered());
//	Document document = builder.parse(getClass().getResourceAsStream(TEXT_CORPUS_INSTANCE_LOCATION));
//	document.getTextContent();
//	assertTrue(entityResolver.isTriggered());
    }
}
