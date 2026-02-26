# BlogWebsite

BlogWebsite is a web application built using Spring Boot, Thymeleaf, and MySQL. It allows users to create, manage, and view blog posts through a structured and secure backend architecture.

## Features

- User registration and authentication
- Create, edit, and delete blog posts
- Category-based blog organization
- Image upload support
- Layered architecture (Controller, Service, Repository)
- Database integration using JPA

## Technologies Used

- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySQL
- Maven

## Project Structure
```
src
├── main
│ ├── java
│ │ └── net
│ │ └── sample
│ │ └── wordpress
│ │ ├── config
│ │ ├── controller
│ │ ├── entity
│ │ ├── repository
│ │ └── service
│ └── resources
│ ├── templates
│ └── application.properties
└── test
```

## How to Run

1. Clone the repository

git clone https://github.com/KARTIKYASHARMA/BlogWebsite.git  
cd BlogWebsite

2. Configure database details in application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/blogdb  
spring.datasource.username=your_username  
spring.datasource.password=your_password  

3. Run the application

mvn spring-boot:run

4. Open in browser

http://localhost:8080

## Author

Kartikya Sharma  
https://github.com/KARTIKYASHARMA
