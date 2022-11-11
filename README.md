# MoviesBattle
The Movies Battle project aimed to create a REST API for a cardgame-style game, where two movies are informed and the player must choose the one with the best rating on IMDB.

## Specifications
The project was created using Spring Web, Boot, Data, JPA and Security. The java version used was 11 and the IDE was Eclipse. Maven was used for dependency management. Through the Spring Framework, the concept of dependency injection is used. Spring uses a container called Spring IoC Container that manages project dependencies automatically.
IMD Top 250 OMDBApi and Web Scraping Integration
When starting the application, 30 movies are loaded. To carry out this loading, web scraping was performed, which is the process of extracting relevant information from a given site, from the IMDB 250 Top Movies link. After randomly capturing the IMDB ID's, the query is made in the OMDB Api. This process was chosen to insert popular movies into the application, not just old and unpopular movies.

## Code Documentation with Java Doc
Javadoc is a documentation generator for the Java language with the aim of generating documentation from Java source code in HTML format. Documentation is available in the program's Doc folder.

## Endpoint Documentation with Swagger UI
Swagger is an open source application that helps you define, create, document, and consume REST APIs. It was used in the project in order to generate documentation for the endpoints based on the Open API.

## Tests with JUnit
JUnit was used to create and run the automated tests and are available in the /src/test/java folder.
