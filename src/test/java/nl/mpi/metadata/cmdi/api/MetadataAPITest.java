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
import java.net.URISyntaxException;
import java.net.URL;
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.ReferencingMetadataElement;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;

/**
 * Implementation of MetadataAPI test for {@link CMDIApi}
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MetadataAPITest extends nl.mpi.metadata.api.MetadataAPITest {

    private final MetadataAPITestProvider provider;

    public MetadataAPITest() throws Exception {
	provider = new CMDIMetadataAPITestProvider();
    }

    @Override
    protected MetadataAPITestProvider getProvider() {
	return provider;
    }

    private class CMDIMetadataAPITestProvider extends CMDIAPITestCase implements MetadataAPITestProvider<CMDIApi> {

	private final URI schemaURI;

	private CMDIMetadataAPITestProvider() throws URISyntaxException {
	    schemaURI = testSchemaTextCorpus.toURI();
	}

	public MetadataAPI createAPI() throws Exception {
	    CMDIApi cmdiApi = new CMDIApi();
	    cmdiApi.setEntityResolver(CMDI_API_TEST_ENTITY_RESOLVER);
	    return cmdiApi;
	}

	public MetadataDocumentType createDocumentType(CMDIApi api) throws Exception {
	    return getNewTestProfileAndRead(schemaURI);
	}

	public MetadataDocument createDocument(CMDIApi api) throws Exception {
	    return getNewTestDocument(schemaURI, TEXT_CORPUS_INSTANCE_LOCATION, TEXT_CORPUS_PROFILE_ROOT_NODE_PATH);
	}

	public MetadataDocument createInvalidDocument(CMDIApi api) throws Exception {
	    return getNewTestDocument(schemaURI, "/cmdi/TextCorpusProfile-instance-invalid.cmdi", TEXT_CORPUS_PROFILE_ROOT_NODE_PATH);
	}

	public MetadataContainer createEmptyParentElement(CMDIApi api, MetadataDocument document) throws Exception {
	    CMDIDocument cmdiDocument = (CMDIDocument) document;
	    CMDIProfile profile = api.getProfileContainer().getProfile(schemaURI);
	    ComponentType collectionType = (ComponentType) profile.getContainableTypeByName("Collection");
	    return new Component(collectionType, cmdiDocument);
	}

	public MetadataElementType createAddableType(CMDIApi api) throws Exception {
	    CMDIProfile profile = api.getProfileContainer().getProfile(schemaURI);
	    ComponentType collectionType = (ComponentType) profile.getContainableTypeByName("Collection");
	    CMDIProfileElement generalInfoType = collectionType.getContainableTypeByName("GeneralInfo");
	    return generalInfoType;
	}

	public MetadataElementType createUnaddableType(CMDIApi api) throws Exception {
	    CMDIProfile profile = api.getProfileContainer().getProfile(schemaURI);
	    ComponentType collectionType = (ComponentType) profile.getContainableTypeByName("Collection");
	    ComponentType generalInfoType = (ComponentType) collectionType.getContainableTypeByName("GeneralInfo");
	    CMDIProfileElement nameType = generalInfoType.getContainableTypeByName("Name");
	    return nameType;
	}

	public ReferencingMetadataElement getReferencingMetadataElement(CMDIApi api, MetadataDocument document) {
	    CMDIDocument cmdiDocument = (CMDIDocument) document;
	    return cmdiDocument.getChildElement("Collection");
	}

	public URL getDocumentURL() throws Exception {
	    return getClass().getResource(TEXT_CORPUS_INSTANCE_LOCATION);
	}

	public URI getDocumentTypeURI() {
	    return schemaURI;
	}
    }
}
