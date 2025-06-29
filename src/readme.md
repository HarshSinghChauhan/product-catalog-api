# Product Catalog API (Spring Boot, PostgreSQL, Redis)

This project is a RESTful Product Catalog API built using Spring Boot. It includes Redis as a caching layer to optimize response times and PostgreSQL as the database. The API supports full CRUD operations along with filtering capabilities and cache invalidation.

**Features**

- RESTful endpoints for managing products  
- Redis caching for product details, filtered listings, and all products  
- Automatic cache invalidation on data changes  
- Time-based expiration (TTL) for cache  
- Logging for cache hits, misses, and operation outcomes  
- Graceful error handling with consistent API responses

**Technology Stack**

- Java 21  
- Spring Boot 3  
- PostgreSQL  
- Redis  
- Docker & Docker Compose  
- JPA (Hibernate)  
- Spring Data Redis  
- Spring Web  
- Maven

**API Endpoints**

- `GET /products` – Fetch all products  
- `GET /products/{id}` – Get product details by ID  
- `GET /products?category=&price_min=&price_max=` – Filter by category and price range  
- `POST /products` – Add a new product  
- `PUT /products/{id}` – Update existing product  
- `DELETE /products/{id}` – Delete product by ID

All successful write operations return a structured response with `success`, `message`, and optionally `data`.

**Setup Instructions**

- Clone the repository  
- Start PostgreSQL and Redis using Docker  
- Run `docker-compose up -d`  
- Run the application using Maven with `mvn spring-boot:run`  
- Application will be available at `http://localhost:8080`

**Database Configuration**

- Configured in `application.yml`  
- Host: `localhost`  
- Port: `5432`  
- Database: `productdb`  
- Username: `postgres`  
- Password: `password`  
- Redis runs on `localhost:6379`

**Testing the API**

Use Postman or `curl` to test endpoints  
Example POST request payload:
```dtd 
CREATE:
        curl --location 'http://localhost:8080/products' \
        --header 'Content-Type: application/json' \
        --data '{
        "name": "iPhone 15",
        "category": "electronics",
        "price": 99999.0,
        "stock": 50
        }'

GET ALL PRODUCT:
        curl --location 'http://localhost:8080/products'

GET PRODUCT BY ID:
        curl --location 'http://localhost:8080/products/1'

UPDATE PRODUCT BY ID:
        curl --location --request PUT 'http://localhost:8080/products/1' \
        --header 'Content-Type: application/json' \
        --data '{
        "name": "iPhone 15 Pro",
        "category": "electronics",
        "price": 109999.0,
        "stock": 41
        }'

DELETE PRODUCT BY ID:
        curl --location --request DELETE 'http://localhost:8080/products/2'
```

**Project Structure**

- controller – REST API layer
- service – Business logic and caching
- repository – JPA-based DB access
- model – Product entity
- config – Redis configuration
- dto – API response format
- exception – Global exception handling

**Author**

- Harsh Chauhan
- Email: hsc5838@gmail.com
- LinkedIn: https://linkedin.com/in/harshchauhan16