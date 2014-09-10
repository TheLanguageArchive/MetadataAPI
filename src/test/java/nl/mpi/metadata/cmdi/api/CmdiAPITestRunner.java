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
package nl.mpi.metadata.cmdi.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CmdiAPITestRunner {

    private final static String DEFAULT_URL_STRING = "https://corpus1.mpi.nl/ds/TranslationService/translate?in=1839/00-0000-0000-0005-01ED-1&outFormat=cmdi"; //LAAARGE FILE

    public static void main(String[] args) throws MalformedURLException, IOException, MetadataException {
	Logger root = Logger.getLogger("nl.mpi.metadata");
	root.setLevel(Level.DEBUG);

	final String documentUrlString;
	if (args.length > 0) {
	    documentUrlString = args[0];
	} else {
	    documentUrlString = DEFAULT_URL_STRING;
	}

	final URL documentUrl = new URL(documentUrlString);
	final CMDIApi api = new CMDIApi();
	final CMDIDocument cmdiDocument = api.getMetadataDocument(documentUrl);
	printContents(cmdiDocument);
    }

    private static void printContents(final CMDIDocument cmdiDocument) {
	System.out.println(String.format("\n%s\n [%d resources]", cmdiDocument.getFileLocation(), cmdiDocument.getDocumentReferencesCount()));
	for(HeaderInfo header: cmdiDocument.getHeaderInformation()){
	    System.out.println(" " + header.toString());
	}
	printComponents(cmdiDocument, 0);
    }

    private static void printComponents(CMDIContainerMetadataElement container, int level) {
	final int whitespace = level + 1;
	System.out.println(String.format("%" + whitespace + "s+ %s", "", container.toString()));
	for (MetadataElement child : container.getChildren()) {
	    if (child instanceof CMDIContainerMetadataElement) {
		printComponents((CMDIContainerMetadataElement) child, level + 1);
	    } else {
		System.out.println(String.format("%" + whitespace + "s - %s: %s", "", child.getName(), child.getDisplayValue()));
	    }
	}
    }
}
