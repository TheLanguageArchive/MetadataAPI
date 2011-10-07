/*
 * Copyright (C) 2011 The Max Planck Institute for Psycholinguistics
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
package nl.mpi.metadata.identifierresolver;

import java.net.URI;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import nl.mpi.metadata.api.MetadataDocument;

/**
 * Identifier resolver that passes resolution requests on to 
 * one of a set of resolvers that is capable of resolving the specified 
 * id. Custom resolvers can be implemented and similarly chained. 

 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ChainingIdentifierResolver implements IdentifierResolver {

    private Collection<IdentifierResolver> chain;

    public ChainingIdentifierResolver() {
	this(new CopyOnWriteArrayList<IdentifierResolver>());
    }

    public ChainingIdentifierResolver(Collection<IdentifierResolver> chain) {
	this.chain = new CopyOnWriteArrayList(chain);
    }

    public boolean canResolve(MetadataDocument document, URI identifier) {
	for (IdentifierResolver resolver : chain) {
	    if (resolver.canResolve(document, identifier)) {
		return true;
	    }
	}
	return false;
    }

    public URI resolveIdentifier(MetadataDocument document, URI identifier) {
	for (IdentifierResolver resolver : chain) {
	    if (resolver.canResolve(document, identifier)) {
		return resolver.resolveIdentifier(document, identifier);
	    }
	}
	return null;
    }
}
