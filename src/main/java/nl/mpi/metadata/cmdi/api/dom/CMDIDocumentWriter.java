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

import java.util.Properties;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.dom.MetadataDOMBuilder;
import nl.mpi.metadata.api.dom.MetadataDocumentWriter;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.w3c.dom.Document;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentWriter implements MetadataDocumentWriter<CMDIDocument> {

    private MetadataDOMBuilder<CMDIDocument> domBuilder;
    private Properties outputProperties;

    public CMDIDocumentWriter(MetadataDOMBuilder<CMDIDocument> domWriter) {
	this.domBuilder = domWriter;
    }

    /**
     * Writes the specified metadata document to the provided outputStream. The transformer used for this is
     * obtained by calling {@code Transformer
     * @param metadataDocument
     * @param outputStream
     */
    public void write(CMDIDocument metadataDocument, Result outputResult) throws MetadataDocumentException, TransformerException {
	Document dom = domBuilder.buildDomForDocument(metadataDocument);
	Source source = new DOMSource(dom);

	Transformer transformer = getNewTransformer();
	transformer.transform(source, outputResult);
    }

    protected Transformer getNewTransformer() throws TransformerConfigurationException {
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();

	if (getOutputProperties() != null) {
	    transformer.setOutputProperties(getOutputProperties());
	}
	return transformer;
    }

    /**
     * @return outputProperties to use when serializing the metadata document to XML. If null, defaults are used.
     */
    public final Properties getOutputProperties() {
	return outputProperties;
    }

    /**
     * @param outputProperties outputProperties to use when serializing the metadata document to XML. The will be passed to the transformer
     * object through {@link Transformer#setOutputProperties(java.util.Properties)}. Set to null to keep defaults (i.e. the call to 
     * Transformer will not be made).
     */
    public final void setOutputProperties(Properties outputProperties) {
	this.outputProperties = outputProperties;
    }
}
