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
import nl.mpi.metadata.api.model.MetadataDocument;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class URLResolver implements IdentifierResolver {

    public boolean canResolve(MetadataDocument document, URI identifier) {
	final String scheme = identifier.getScheme();
	return !identifier.isAbsolute()
		|| scheme.equalsIgnoreCase("http")
		|| scheme.equalsIgnoreCase("https")
		|| scheme.equalsIgnoreCase("file");
    }

    public URI resolveIdentifier(MetadataDocument document, URI identifier) {
	return document.getFileLocation().resolve(identifier);
    }
}
