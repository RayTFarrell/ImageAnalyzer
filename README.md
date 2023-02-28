# ImageAnalyzer
Spring REST api utilizing Immaga API for image analysis

## How to use the REST api
By default, the Spring application can be reached at localhost:8080.
The API key to reach the Imagga service is stored as an environment variable for security purposed, typically pulled from a service like Hshicorp Vault.

### Endpoints Available

localhost:8080/images
- RequestMapping.GET
- Will fetch all images stored in the H2 database.

localhost:8080/images?objects={COMMA_SEPERATED_OBJECTS}
- RequestMapping.GET
- Will fetch all images stored in the H2 database with object detection performed, and contain the provided objects as parameters.

localhost:8080/images/{id}
- RequestMapping.GET
- Will fetch the image stored in the H2 database by the ID provided.

localhost:8080/images
- RequestMapping.POST
- Will store the provided image and meta data into the H2 database.
- Enpoint consumes optional multipart/form-data in this format: {url : urlString, file : file, label : labelString, objectDetectionEnabled : objectDetectionEnabledString}
- Request must contain only a url OR a image file. If both are provided, or neither, the client is served a bad request reponse.
- If label is not provided, one is generated for the database entry based on the url or file base name.
- If objectDetectionEnabled is set to true, the Imagga API is called to return a list of objects detected in the image, which is stored in the H2 database.

### Datastore
The database used for this project is H2. The database can be spun up for demo purposes with the service and stored into a file, or in-memory only but updating the spring.datasource.url.
H2 can be reached at localhost:8080/h2-console for ad-hoc SQL queries.