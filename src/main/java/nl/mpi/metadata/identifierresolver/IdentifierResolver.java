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
import nl.mpi.metadata.api.model.MetadataDocument;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface IdentifierResolver {

    /**
     * 
     * @param document Document to resolve from
     * @param identifier Identifier to resolve
     * @return Whether specified identifier can be resolved
     */
    boolean canResolve(MetadataDocument document, URI identifier);

    /**
     * Resolves specified identifier
     * @param document Document to resolve from
     * @param identifier Identifier to resolve
     * @return Resolution of identifier. Null if cannot be resolved.
     */
    URL resolveIdentifier(MetadataDocument document,URI identifier) throws IdentifierResolutionException ;
}
