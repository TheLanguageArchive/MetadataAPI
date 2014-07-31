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
package nl.mpi.metadata.cmdi.api.dom;

import javax.xml.parsers.DocumentBuilder;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import org.junit.Test;

import static nl.mpi.metadata.cmdi.api.CMDIAPITestCase.CMDI_API_TEST_ENTITY_RESOLVER;
import static org.junit.Assert.*;

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
	CMDIApiDOMBuilderFactory instance = new CMDIApiDOMBuilderFactory(CMDI_API_TEST_ENTITY_RESOLVER);
	DocumentBuilder result = instance.newDOMBuilder();
	assertNotNull(result);
    }
}
