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

import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.type.ElementType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MultilingualElementImplTest extends ElementImplTest {

    private MultilingualElementImpl mlInstance;

    @Override
    protected ElementImpl createInstance(ElementType type, Component parent, String value) {
	mlInstance = new MultilingualElementImpl(type, parent, value, null);
	return mlInstance;
    }

    @Test
    public void testGetLanguage() {
	assertNull(mlInstance.getLanguage());
	mlInstance = new MultilingualElementImpl(type, parent, DEFAULT_VALUE, "en-EN");
	assertEquals("en-EN", mlInstance.getLanguage());
    }

    @Test
    public void testSetLanguage() {
	mlInstance.setLanguage("nl-NL");
	assertEquals("nl-NL", mlInstance.getLanguage());
    }
}
