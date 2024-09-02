# Introduction
(50-100 words)
What does this app do? What technoglies your have used? (JDBC, PSQL, MVN, etc..)

This Java application allows users to execute stock trades, tracking both the latest stock information and purchases made by the user.
The app fetches real-time stock data from the Alpha Vantage API and stores it in a Postgres database through the JDBC API.
The repository was initialized and packaged using Maven, and Docker was used to containerize the application. A Docker image has been uploaded to DockerHub
to allow users to easily download and spin up the application locally.



# Implementation
## ER Diagram
![ERD.PNG](assets%2FERD.PNG)

## Design Patterns
Discuss DAO and Repository design patterns (150-200 words)

# Test
The app was tested through both automated unit tests using JUnit/Mockito, and manual testing.
The actual DB was used through the JDBC API as part of the tests. Sample test data for quotes and positions was constructed for the unit tests.
Unit tests were written for the following classes: 

- PositionDao
- QuoteDao
- QuoteHttpHelper
- QuoteService
- PositionService

Manual testing was performed for the controller as there is little value in mocking it.
The tests can be executed using ```mvn -X test```.