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
package nl.mpi.metadata.cmdi.api.type.datacategory;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class DataCategoryTest {

    private final static String identifier = "http://www.isocat.org/datcat/DC-2658";
    private DataCategory dataCategory;

    @Before
    public void setUp() throws URISyntaxException {
	dataCategory = new DataCategory(new URI(identifier));
    }

    @After
    public void tearDown() {
	dataCategory = null;
    }

    /**
     * Test of getIdentifier method, of class DataCategory.
     */
    @Test
    public void testGetIdentifier() throws URISyntaxException {
	assertEquals(new URI(identifier), dataCategory.getIdentifier());
    }

    /**
     * Test of equals method, of class DataCategory.
     */
    @Test
    public void testEquals() throws URISyntaxException {
	DataCategory datCat2 = new DataCategory(new URI(identifier));
	DataCategory datCat3 = new DataCategory(new URI("other-identifier"));
	assertTrue(datCat2.equals(dataCategory));
	assertTrue(dataCategory.equals(datCat2));
	assertFalse(datCat3.equals(dataCategory));
	assertFalse(dataCategory.equals(datCat3));
    }
}
