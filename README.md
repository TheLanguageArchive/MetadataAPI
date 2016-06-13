# MetadataAPI
An API that makes it easy to read and write [CMDI metadata](http://www.clarin.eu/cmdi) of any profile without explicitly manipulating XML or requiring knowledge about the internal structure of CMDI documents.

The Metadata API was developed at [The Language Archive](http://tla.mpi.nl). Development takes place in the `develop` branch. 

Maintained and written by Twan Goosen <twan.goosen@mpi.nl>.

## Building and Deploying

MetadataAPI is a single maven project and builds to a jar that can be used as a dependency in other projets. The latest stable version released and deployed to our Nexus Maven repository is 1.0. To use it in your project, add the following to its pom:

```xml
<dependencies>
...
  <dependency>
    <groupId>nl.mpi</groupId>
    <artifactId>metadata-api</artifactId>
    <version>1.0</version>
  </dependency>
  ...
</dependencies>
```

## Documentation

### Usage examples

Reading and writing of CMDI with the API takes place in [Lamus2](https://github.com/TheLanguageArchive/lamus2).

Another example of a tool that is using the MetadataAPI to write CMDI is the [CSV-to-CMDI metadata importer](https://trac.mpi.nl/browser/metadata-csv-importer/trunk/metadata-csv-importer-cmdi), mainly in the `CmdiWritingResultHandler`.

### Instantiating the API

The most basic use case is really simple, you can just do

```java
MetadataAPI myAPI = new CMDIApi();
```

which will give you an instance of MetadataAPI. You can of course use stronger typing (e.g. `CMDIApi`) but, as usual, whenever you can, try to to be agnostic about the specific implementation. Most functionality should be available through the generic interfaces.

Also have a look at the alternative constructors for the CMDIApi. You may want to provider your own ​EntityResolver, e.g.:

```java
MetadataAPI myAPI = new CMDIApi(myEntityResolver);
```

A lot of things get re-used and/or cached within an API instance, so try to use (within a single thread at least) the same API instance for optimal performance.

### Retrieving an existing metadata document

One you have your API instance, you can get an instance of MetadataDocument by doing

```java
MetadataDocument mdDocument = myApi.getMetadataDocument(documentUrl);
```

MetadataDocuments are also MetadataContainers, which means they provide methods to get their constituent MetadataElements:

```java
List<MetadataElement> children = mdDocument.getChildren();
MetadataElement myElement = children.get(0);
```

Then on a MetadataElement (the instance of which may also implement other interfaces, such as MetadataContainer!) you can read some properties, like:

```java
String name = myElement.getName();
MetadataDocument parentDocument = myElement.getDocument();
MetadataElementType type = myElement.getType()
```

### Writing changes to an existing metadata document

Make changes to your metadata elements, e.g.:

```java
((MetadataField) myField).setValue("New value");
((MetadataContainer) myContainer).addChildElement(myChildElement);
```

Use a ​StreamResult instance to write the metadata document to a file, stream or output writer:

```java
myAPI.writeMetadataDocument(myDocument, new StreamResult(new File("/tmp/myFile.cmdi"));
```

### Dealing with links

```java
if (metadataElement instanceof ReferencingMetadataDocument) {
  List<Reference> documentReferences = ((ReferencingMetadataDocument)metadataElement).getDocumentReferences();
  Reference firstRef = documentReferences.get(0);
  if (firstRef instanceOf MetadataReference) {
    //it's a metadata reference, try to open using metadata API
  } else if(firstRef instanceof ResourceReference) {
    // it's a resource. retrieve type using getMimetype() method
  }

  //references may have a local URL (file or http)
  URL location = firstRef.getLocation();
  if (location == null) {
    // use firstRef.getURI() instead, it should always have a value (can be a PID)
  }
}
```
