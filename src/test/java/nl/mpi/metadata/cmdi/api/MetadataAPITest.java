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

import java.net.URI;
import java.net.URL;
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MetadataAPITest extends nl.mpi.metadata.api.MetadataAPITest {

    private final URI schemaURI;

    public MetadataAPITest() throws Exception {
	schemaURI = CMDIAPITestCase.testSchemaSmall.toURI();
    }
    private CMDIAPITestCase testCase = new CMDIAPITestCase() {
    };

    @Override
    protected MetadataAPI createAPI() throws Exception {
	CMDIApi api = new CMDIApi();
	return api;
    }

    @Override
    protected MetadataDocumentType createDocumentType() throws Exception {
	return testCase.getNewTestProfileAndRead(CMDIAPITestCase.testSchemaTextCorpus.toURI());
    }

    @Override
    protected MetadataDocument createDocument() throws Exception {
	return testCase.getNewTestDocument(CMDIAPITestCase.testSchemaTextCorpus.toURI(), CMDIAPITestCase.TEXT_CORPUS_INSTANCE_LOCATION, CMDIAPITestCase.TEXT_CORPUS_PROFILE_ROOT_NODE_PATH);
    }

    @Override
    protected MetadataDocument createInvalidDocument() throws Exception {
	return testCase.getNewTestDocument(CMDIAPITestCase.testSchemaTextCorpus.toURI(), "/cmdi/TextCorpusProfile-instance-invalid.cmdi", CMDIAPITestCase.TEXT_CORPUS_PROFILE_ROOT_NODE_PATH);
    }

    @Override
    protected MetadataContainer createEmptyParentElement(MetadataDocument document) throws Exception {
	CMDIDocument cmdiDocument = (CMDIDocument) document;
	CMDIProfile profile = ((CMDIApi) getAPI()).getProfileContainer().getProfile(schemaURI);

	CMDIProfileElement testComponentType = profile.getContainableTypes().iterator().next();
	return (Component) getAPI().createMetadataElement(cmdiDocument, testComponentType);
    }

    @Override
    protected MetadataElementType createAddableType() throws Exception {
	CMDIProfile profile = ((CMDIApi) getAPI()).getProfileContainer().getProfile(schemaURI);
	ComponentType testComponentType = (ComponentType) profile.getContainableTypes().iterator().next();
	CMDIProfileElement booleanElement = testComponentType.getContainableTypes().iterator().next();
	return booleanElement;
    }

    @Override
    protected URL getDocumentURL() throws Exception {
	return getClass().getResource(CMDIAPITestCase.TEXT_CORPUS_INSTANCE_LOCATION);
    }
}
