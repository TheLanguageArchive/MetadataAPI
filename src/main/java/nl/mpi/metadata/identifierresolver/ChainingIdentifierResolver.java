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
package nl.mpi.metadata.identifierresolver;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import nl.mpi.metadata.api.model.MetadataDocument;

/**
 * Identifier resolver that passes resolution requests on to
 * one of a set of resolvers that is capable of resolving the specified
 * id. Custom resolvers can be implemented and similarly chained.  *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ChainingIdentifierResolver implements IdentifierResolver {

    private List<IdentifierResolver> chain;

    public ChainingIdentifierResolver() {
	this(new CopyOnWriteArrayList<IdentifierResolver>());
    }

    public ChainingIdentifierResolver(IdentifierResolver... chain) {
	this(Arrays.asList(chain));
    }

    public ChainingIdentifierResolver(List<IdentifierResolver> chain) {
	this.chain = new CopyOnWriteArrayList(chain);
    }

    @Override
    public boolean canResolve(MetadataDocument document, URI identifier) {
	for (IdentifierResolver resolver : chain) {
	    if (resolver.canResolve(document, identifier)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public URL resolveIdentifier(MetadataDocument document, URI identifier) {
	for (IdentifierResolver resolver : chain) {
	    if (resolver.canResolve(document, identifier)) {
		return resolver.resolveIdentifier(document, identifier);
	    }
	}
	return null;
    }

    /**
     * @return A <em>copy</em> of the identifiers chain
     */
    public List<IdentifierResolver> getChain() {
	return new ArrayList<IdentifierResolver>(chain);
    }

    /**
     * @param chain the identifiers chain to use (instead of the current one)
     */
    public void setChain(List<IdentifierResolver> chain) {
	this.chain = new CopyOnWriteArrayList(chain);
    }
}
