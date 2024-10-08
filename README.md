# DS-Assignment2

# Weather Data Aggregation System

OVERVIEW
In the client-server application known as the Weather Data Aggregation System, the server compiles weather data from various clients (content servers) and provides it to users upon request. When a client sends a GET request, the system receives weather data in JSON format, stores it with a timestamp, and sends it back. The system guarantees data consistency and use Lamport clocks for event ordering in distributed systems. Outdated data is also periodically eliminated by the aggregate server.


FEATURES
Lamport Clocks: Make sure that events in distributed systems are ordered consistently.
PUT/GET Requests: Clients can use PUT requests to provide meteorological data to the server, and they can use GET queries to receive it.
Concurrent Data Store: A thread-safe ConcurrentHashMap is used to store meteorological data.
Data Expiration: After 30 seconds, outdated data is immediately deleted.
Error Handling: Correct or incomplete data handled with grace.


PROJECT STRUCTURE
├── AggregationServer.java   # Main server to collect weather data
├── ContentServer.java       # Sends weather data to the aggregation server
├── GETClient.java           # Retrieves weather data from the aggregation server
├── lib/                     # JUnit and Hamcrest libraries for testing
│   ├── junit-4.13.2.jar     # JUnit library for testing
    |-- json-20210307.jar       # JSON parsing library
│   ├── hamcrest-core-1.3.jar # Hamcrest library for assertions
├── test/
│   ├── AggregationServerTest.java # Unit and integration tests for AggregationServer
│   ├── ContentServerTest.java     # Unit and edge case tests for ContentServer
│   ├── GETClientTest.java         # Tests for GETClient interactions
├── weather_data.txt          # Example data file for ContentServer
└── README.md                 # Project documentation


COMPONENTS OVERVIEW
1. Server for Aggregation
One or more ContentServer instances must provide weather data to the Aggregation Server. The GETClient can be used to retrieve the weather data, which is saved in a JSON format.

Port: The server's default port of operation is 4567. You can change this by supplying an argument with the port.
Data Expiration: The server automatically discards meteorological data that is more than thirty seconds old.

2. Content Server
The Content Server reads weather data from a local file (e.g., weather1.txt) and sends it to the Aggregation Server in JSON format.

Usage:
Code
java ContentServer

Example
Code
java ContentServer localhost 4567 weather1.txt

Data Format: The data file should contain weather-related fields in a key-value format, where each line represents a field. Example:

Code
id:IDS60901
name:Adelaide (West Terrace /  ngayirdapira)
state: SA
time_zone:CST
lat:-34.9
lon:138.6
local_date_time:15/04:00pm
local_date_time_full:20230715160000
air_temp:13.3
apparent_t:9.5
cloud:Partly cloudy
dewpt:5.7
press:1023.9
rel_hum:60
wind_dir:S
wind_spd_kmh:15
wind_spd_kt:8

3. GET Client
The GETClient connects to the Aggregation Server and retrieves the aggregated weather data. The data is printed in JSON format.

Usage:
Code
java GETClient 
Example:
Code
java GETClient localhost 4567


RUNNING THE APPLICATION
1. Compile the Code
To compile all components:
Code
javac -cp ".;lib/json-20210307.jar" AggregationServer.java ContentServer.java GETClient.java

2. Start the Aggregation Server
To start the server on the default port (4567):

Code
java -cp ".;lib/json-20210307.jar" AggregationServer

3. Run the Content Server
To send weather data to the aggregation server:

Code
java -cp ".;lib/json-20210307.jar" ContentServer localhost 4567 weather1.txt

4. Run the GET Client
To retrieve the aggregated weather data:

Code
java -cp ".;lib/json-20210307.jar" GETClient localhost 4567


RUNNING TESTS
Unit and integration tests are provided for each component in the /test directory. The tests use JUnit and Hamcrest for assertions.

1. Compile the Tests
Code
javac -cp ".;lib/*" test/*.java

2. Run the Tests

Code
java -cp ".;lib/*" org.junit.runner.JUnitCore test.AggregationServerTest
java -cp ".;lib/*" org.junit.runner.JUnitCore test.ContentServerTest
java -cp ".;lib/*" org.junit.runner.JUnitCore test.GETClientTest

LIBRARIES USED

JUnit: Provides the framework for unit testing.
Hamcrest: Used for writing expressive test assertions.