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
package nl.mpi.metadata.cmdi.api;

import java.net.URI;
import java.util.Collection;
import javax.xml.xpath.XPath;
import nl.mpi.metadata.api.HeaderInfo;
import nl.mpi.metadata.api.MetadataDocument;
import nl.mpi.metadata.api.MetadataElement;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocument extends Component implements MetadataDocument {

    @Override
    public CMDIProfile getType() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public URI getFileLocation() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<HeaderInfo> getHeaderInformation() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataElement getElement(XPath path) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insertElement(XPath path, MetadataElement element) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataElement removeElement(XPath path) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addMetadataDocumentListener(MetadataDocumentListener listener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeMetadataDocumentListener(MetadataDocumentListener listener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }
}
