# Technology Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Flyway (database migrations)
- MySQL
- Maven
- Docker Compose
- Lombok
- JUnit (testing)

how to run the app:

1. git clone https://github.com/ekali-star/VanOpt.git
2. cd VanOpt
3. docker compose up -d (This will start a MySQL container with: Database: vanopt, 
																 User: vanoptuser
																 Password: secret
																 Port: 3306)

Database setup

Database schema is managed using Flyway. theres a V1_init_schema.sql file
in the db.migration package, which runs automatically when you run the app.
this sql file creates two tables in the database: optimization_request and shipment.

Example cURL requests and responses for all three endpoints.

there are three example json files in the request-examples package:
1. valid-request

to check the output for this input run the command: 
curl.exe -X POST http://localhost:8080/api/v1/van-opt/optimize `
-H "Content-Type: application/json" `
--data-binary "@C:\Users\USER\OneDrive\Desktop\spring-core-task\VanOpt\src\main\java\org\example\vanopt\request-examples\valid-request.json" |
ConvertFrom-Json | ConvertTo-Json -Depth 10
2. invalid-request

to check the output for this input run the command: 
curl.exe -X POST http://localhost:8080/api/v1/van-opt/optimize `
-H "Content-Type: application/json" `
--data-binary "@src\main\java\org\example\vanopt\request-examples\invalid-request.json" | 
ConvertFrom-Json | ConvertTo-Json -Depth 10
3. empty-request

to check the output for this input run the command: 
curl.exe -X POST http://localhost:8080/api/v1/van-opt/optimize `
-H "Content-Type: application/json" `
--data-binary "@src\main\java\org\example\vanopt\request-examples\empty-request.json" | 
ConvertFrom-Json | ConvertTo-Json -Depth 10

Database schema & indexes
we have two tables in the database:
1. 	optimization_request
	columns:
		id 				CHAR(36) primary key;
		max_volume 		INT
		total_volume	INT
		total_revenue 	INT
		created_at		TIMESTAMP
2. 	shipment:
	columns:
		id				BIGINT primary key
		request_id		CHAR(36) foreign key
		name			VARCHAR(255)
		volume			INT
		revenue			INT
		selected		BOOLEAN
		
indexing: idx_shipment_request_id
		
		
		