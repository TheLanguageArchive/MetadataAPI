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
package nl.mpi.metadata.cmdi.api.type;

import java.net.URL;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIAPITest {

    /**
     * Test schema 1 (TextCorpusProfile http://catalog.clarin.eu/ds/ComponentRegistry?item=clarin.eu:cr1:p_1271859438164)
     */
    public final static URL testSchemaSession = CMDIProfileTest.class.getResource("/xsd/TextCorpusProfile.xsd");
    /**
     * Test schema 2 (CLARINWebservice http://catalog.clarin.eu/ds/ComponentRegistry?item=clarin.eu:cr1:p_1311927752335)
     */
    public final static URL testSchemaWebservice = CMDIProfileTest.class.getResource("/xsd/clarin-webservice.xsd");
}
