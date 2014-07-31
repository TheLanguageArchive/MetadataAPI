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
package nl.mpi.metadata.identifierresolver;

import java.net.URI;
import java.net.URL;
import nl.mpi.metadata.api.model.MetadataDocument;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MockIdentifierResolver implements IdentifierResolver {

    protected boolean canResolve;
    protected URL result;

    public MockIdentifierResolver(boolean canResolve, URL result) {
	this.canResolve = canResolve;
	this.result = result;
    }

    @Override
    public boolean canResolve(MetadataDocument document, URI identifier) {
	return canResolve;
    }

    @Override
    public URL resolveIdentifier(MetadataDocument document, URI identifier) throws IdentifierResolutionException {
	return result;
    }

    /**
     * Set the value of canResolve
     *
     * @param canResolve new value of canResolve
     */
    public void setCanResolve(boolean canResolve) {
	this.canResolve = canResolve;
    }

    /**
     * Set the value of result
     *
     * @param result new value of result
     */
    public void setResult(URL result) {
	this.result = result;
    }
}
