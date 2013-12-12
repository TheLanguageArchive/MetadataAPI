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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.EntityResolver;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApiDOMBuilderFactory implements DOMBuilderFactory {

    private final EntityResolver entityResolver;

    public CMDIApiDOMBuilderFactory(EntityResolver entityResolver) {
	this.entityResolver = entityResolver;
    }

    /**
     * Creates a fresh instance of DocumentBuilder. A new factory is requested from the DocumentBuilderFactory, which then gets
     * configured by {@code configureDocumentBuilderFactory(DocumentBuilderFactory)} . On this factory {@code newDocumentBuilder()} is
     * called. The resulting
     * builder gets configured by {@code configureDocumentBuilder(DocumentBuilder)} and is then returned.
     *
     * @return a newly instantiated and configured DocumentBuilder from the DocumentBuilderFactory
     * @see DocumentBuilderFactory
     * @see #configureDocumentBuilderFactory(javax.xml.parsers.DocumentBuilderFactory)
     * @see #configureDocumentBuilder(javax.xml.parsers.DocumentBuilder)
     */
    @Override
    public DocumentBuilder newDOMBuilder() {
	try {
	    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    configureDocumentBuilderFactory(factory);
	    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
	    configureDocumentBuilder(documentBuilder);
	    return documentBuilder;
	} catch (ParserConfigurationException pcEx) {
	    throw new RuntimeException(pcEx);
	}
    }

    /**
     * Configures a newly instantiated document factory. This gets called from {@code newDOMBuilder()} in this implementation
     *
     * @param factory a new instance of DocumentFactory
     * @see #newDOMBuilder()
     */
    protected void configureDocumentBuilderFactory(final DocumentBuilderFactory factory) {
	factory.setNamespaceAware(true);
	factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
    }

    /**
     * Configures a newly instantiated document builder. This gets called from {@code newDOMBuilder()} in this implementation
     *
     * @param builder a new instance of DocumentBuilder
     * @see #newDOMBuilder()
     */
    protected void configureDocumentBuilder(final DocumentBuilder builder) {
	if (entityResolver!= null) {
	    builder.setEntityResolver(entityResolver);
	}
    }
}
