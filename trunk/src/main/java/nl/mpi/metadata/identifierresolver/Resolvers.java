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
package nl.mpi.metadata.identifierresolver;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class Resolvers {

    /**
     * Resolves relative URLs against their documents and leaves absolute URL's untouched
     */
    public final static URLResolver urlResolver = new URLResolver();
    /**
     * Rewrites 'hdl:..." URI's to "http://hdl.handle.net/..." URL's
     */
    public final static HandleResolver handleResolver = new HandleResolver();
    /**
     * Chains {@link #urlResolver} and {@link #handleResolver}, so resolves relative and absolute URL's as well as handles
     */
    public final static ChainingIdentifierResolver defaultResolverChain = new ChainingIdentifierResolver(urlResolver, handleResolver);
}
