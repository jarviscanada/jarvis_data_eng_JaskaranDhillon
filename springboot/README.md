Table of contents
* [Introduction](#Introduction)
* [Quick Start](#Quick-Start)
* [Implementation](#Implementation)
* [REST API Usage](#REST-API-Usage)
* [Test](#Test)
* [Deployment](#Deployment)
* [Improvements](#Improvements)


# Introduction
A trading platform that allows users to create traders, accounts, and execute security orders through the EODHD API.
Built as a REST API using Spring Boot, PostgreSQL, and Docker.

# Quick Start
- Prerequisite: Docker, CentOS 7, Postman
- Navigate to the `/springboot/psql` and `/springboot directories` and run `docker build -t <image_name> .` respectively
- Create a docker network with `docker network create <network_name>`
- Start the containers with `docker run -d  --name <psql_container_name> --network <network_name> -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=<YOUR_DB_PASSWORD> -e POSTGRES_DB=jrvstrading -p 5432:5432 <psql_image_name>` and `docker run -d --name <app_container_name> -p 8080:8080 --network <network_name> -e DB_CONTAINER=<psql_container_name> -e DB_PASSWORD=<YOUR_DB_PASSWORD> -e DB_USERNAME=postgres -e API_KEY=<EOD_API_KEY> -t <app_image_name>`.
- Test the app at www.localhost:8080 using Postman.

# Implemenation
## Architecture
![Untitled Diagram.drawio.png](assets%2FUntitled%20Diagram.drawio.png)
- **Controller layer:** Handles user requests and acts as an interface between the client and the backend services. Responsible for 
routing incoming HTTP requests to the correct handler, validating input data, and returning the response to the client.
- **Service layer:** The business logic layer of your application. This layer interacts receives the input from the controller
and interacts with the DAO layer to process the data and formulate a response.
- **DAO layer:** Responsible for interacting with the database. Provides us an interface with methods, facilitating database operations
such as querying, saving, and deleting records.
- **SpringBoot:** The open-source Java framework used to construct the API. Spring Boot deploys servlets through the servlet container TomCat, which 
handles the HTTP requests and responses. Spring Boot uses IoC to offload the responsibility of object construction and management from the developer to the framework.
- **PSQL:** The relational database used to store Security Orders, Traders, Accounts, and Quotes.
- **EODHD**: The third party API used to fetch quotes for a given stock.

## REST API Usage
### Swagger
Swagger is an open-source framework used to design, document, and test RESTful APIs. It provides a set of tools and specifications to help developers describe the structure of their APIs in a machine-readable format, allowing for better communication and collaboration between developers and testers.

### Quote Controller
- Allows us to fetch quote information and interact with the daily list.
  - GET `/quote/eod/ticker/{ticker}`: Fetch a quote for the given stock.
  - PUT `/quote/eodMarketData` Update all the quotes in the quote table.
  - GET `/quote/dailyList`: list all securities that are available to trading in this trading system.
  - POST `/quote/tickerId/{tickerId}`: add a ticker to the daily list.
### TraderAccount Controller
- Allows us to manage trader accounts and their funds.
  - POST `/trader`: create a trader account given their name, dob, country, and email.
  - POST `/trader/firstname/{firstname}/lastname/{lastname}/dob/{dob}/country/{country}/email/{email}` also create a trader but by providing the details through the url.
  - DELETE `/trader/traderId/{traderId}`: delete a trader and its account given its id if funds = 0 and no open positions.
  - PUT `/trader/deposit/traderId/{traderId}/amount/{amount}`: deposit funds to the traders account.
  - PUT `/trader/withdraw/traderId/{traderId}/amount/{amount}`: withdraw funds from the traders account.
### Order Controller
- Allows us to execute security orders related to a stock for a given trader.
- POST `/order/marketOrder`: execute a BUY or SELL security order for a given # of shares.

# Test
The API was tested using JUnit4 and Mockito, resulting in code coverage of 90% for the service files.

# Deployment
![docker diagram.drawio.png](assets%2Fdocker%20diagram.drawio.png)

- **trading-psql:** This image utilizes the official postgres image on DockerHub to create a database and initialize it using the init-db.sql file.
- **trading-app:** This image utilizes Maven to first build the application into an executable JAR file, then utilizes the JDK image to run the executable.

# Improvements
If you have more time, what would you improve?
- Create a front-end UI to for the application.
- Allow users to create buy and sell orders at certain prices.
- Integrate the financial news API so that users can be better informed about their portfolio trajectory.
