package nl.mpi.metadata.api.util;

import java.io.IOException;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class DefaultResourceResolver implements LSResourceResolver {

    private static Logger logger = LoggerFactory.getLogger(DefaultResourceResolver.class);
    private EntityResolver entityResolver;

    /**
     * Construct using the CatalogResolver as identity resolver. This will assume the presence of some resources.
     * See http://xml.apache.org/commons/components/resolver/resolver-article.html#s.catalogs.in.action
     * @see CatalogResolver
     */
    public DefaultResourceResolver() {
	this(new CatalogResolver());
    }

    /**
     * Construct using the provided entity resolver
     * @param entityResolver Implementation of EntityResolver to use
     */
    public DefaultResourceResolver(EntityResolver entityResolver) {
	this.entityResolver = entityResolver;
    }

    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
	Exception exception = null;
	try {
	    InputSource resolveEntity = entityResolver.resolveEntity(publicId, systemId);
	    resolveEntity.setEncoding("UTF-8");
	    DOMImplementationLS domImplementation;
	    domImplementation = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("LS");
	    LSInput lsInput = domImplementation.createLSInput();
	    lsInput.setEncoding("UTF-8");
	    lsInput.setByteStream(resolveEntity.getByteStream());
	    lsInput.setCharacterStream(resolveEntity.getCharacterStream());
	    return lsInput;
	} catch (ClassNotFoundException ex) {
	    exception = ex;
	} catch (InstantiationException ex) {
	    exception = ex;
	} catch (IllegalAccessException ex) {
	    exception = ex;
	} catch (ClassCastException ex) {
	    exception = ex;
	} catch (SAXException ex) {
	    exception = ex;
	} catch (IOException ex) {
	    exception = ex;
	}
	if (exception != null) {
	    logger.error(
		    String.format(
		    "Exception while attempting to resolve resource: type=%1$s nameSpaceUri=%2$s publicId=%3$s systemId=%4$s baseURI=%5$s",
		    type, namespaceURI, publicId, systemId, baseURI),
		    exception);
	}
	return null;
    }
}
