/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import nl.mpi.metadata.cmdi.api.CMDIConstants;

/**
 * Namespace context implementation to be used with {@link XPath} that resolves all namespaces used internally in the CMDI API
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDINamespaceContext implements NamespaceContext {

    public static final String CMD_PREFIX = "cmd";
    public static final String XSI_PREFIX = "xsi";
    private final Map<String, String> prefixNsMap;
    private final Map<String, List<String>> nsPrefixMap;

    public CMDINamespaceContext() {
	prefixNsMap = new ConcurrentHashMap<String, String>();
	nsPrefixMap = new ConcurrentHashMap<String, List<String>>();
        add("", CMDIConstants.CMD_NAMESPACE); // default namespace in XPaths
	add(CMD_PREFIX, CMDIConstants.CMD_NAMESPACE);
	add(XSI_PREFIX, CMDIConstants.XSI_NAMESPACE);
    }

    private void add(String prefix, String namespaceURI) {
	prefixNsMap.put(prefix, namespaceURI);
	List<String> prefixes = nsPrefixMap.get(namespaceURI);
	if (prefixes == null) {
	    prefixes = new CopyOnWriteArrayList<String>();
	    nsPrefixMap.put(namespaceURI, prefixes);
	}
	prefixes.add(namespaceURI);
    }

    @Override
    public String getNamespaceURI(String prefix) {
	return prefixNsMap.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
	final List<String> prefixes = nsPrefixMap.get(namespaceURI);
	if (prefixes.size() > 0) {
	    return prefixes.get(0);
	} else {
	    return null;
	}
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
	final List<String> prefixes = nsPrefixMap.get(namespaceURI);
	return prefixes.iterator();
    }
}
