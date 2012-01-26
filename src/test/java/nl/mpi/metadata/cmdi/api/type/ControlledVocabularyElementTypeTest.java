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

import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import java.util.List;
import nl.mpi.metadata.api.type.ControlledVocabularyItem;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ControlledVocabularyElementTypeTest extends CMDIAPITestCase {

    @Test
    public void testCVItems() throws Exception {
	CMDIProfile profile = new CMDIProfile(testSchemaTextCorpus.toURI());
	ComponentType collectionType = (ComponentType) profile.getType("Collection");
	ComponentType collectionTypeType = (ComponentType) collectionType.getType("CollectionType");
	ControlledVocabularyElementType collectionTypeCV = (ControlledVocabularyElementType) collectionTypeType.getType("CollectionType");
	List<ControlledVocabularyItem> items = collectionTypeCV.getItems();
	// 8 continents + unknown, unspecified
	assertEquals(items.size(), 5);
    }
}
