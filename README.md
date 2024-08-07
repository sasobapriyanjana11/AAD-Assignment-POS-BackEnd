# AAD-Assignment-POS-BackEnd

## Project Description
The POS System Backend is a RESTful API that supports the frontend operations of a Point of Sale system. This backend service manages customer, order, and inventory data, providing essential functionalities such as creating, reading, updating, and deleting records.

## Table of Contents
- [Project Description](#project-description)
- [Features](#features)
- [Technologies](#technologies)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Features
- Manage customer records
- Process orders and track inventory
- Update item quantities
- Apply discounts and calculate totals
- Secure endpoints with authentication

## Technologies
- **Backend Framework**: Java EE
- **Database**: MySQL
- **Build Tool**: Maven
- **Application Server**: Apache Tomcat 10
- **JDK**: OpenJDK 17

## Installation

### Prerequisites
- Java 17 (OpenJDK 17)
- Maven
- MySQL
- Apache Tomcat 10  


### Steps
1. **Clone the repository**
   ```bash
   git clone https://github.com/sasobapriyanjana11/AAD-Assignment-POS-BackEnd.git

2. **Configure the database**
   - Create a MySQL database named `pos_system`.
   - Update the `persistence.xml` file with your MySQL credentials:
     ```xml
     <properties>
         <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/pos_system"/>
         <property name="javax.persistence.jdbc.user" value="your_username"/>
         <property name="javax.persistence.jdbc.password" value="your_password"/>
         <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
         <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
         <property name="hibernate.hbm2ddl.auto" value="update"/>
     </properties>
     ```



3. **Build the project**
   ```bash
   mvn clean install


4. **Deploy to Tomcat**
* Ensure Tomcat is installed and running.
* Copy the generated WAR file from the `target` directory to the Tomcat `webapps` directory.
* Restart Tomcat.


## Usage
**Running the server**
After installation, run the server by starting Tomcat.

**Project Structure**
The back-end code is organized to follow best practices and maintainability. Important directories and files include:

* src/main/java: Directory containing Java classes.
* src/main/resources/schema: Database schema.
* src/main/resources/File: Tomcat Text File.
* src/main/webapp/WEB-INF/: Configuration files for the JavaEE application.

**Project Packages**
The back-end codebase is further organized into the following packages:

* `src/main/java/lk/ijse/gdse68/pos_system_back_end`
  * `api`: Contains classes defining the API endpoints and services.
  * `bo`: Business Objects - classes that encapsulate business logic.
  * `dao`: Data Access Objects - classes responsible for database interactions.
  * `entity`: Entity classes representing database tables.
  * `dto`: Data Transfer Objects - classes used for data exchange between layers.
  * `filter`: Contains classes implementing filters for intercepting and processing requests.


**Getting Started**
To set up and run the Shop Management project locally, follow these steps:

1. **Clone the repository.**
2. **Set up the back-end dependencies.**
3. **Configure the database connection.**
4. **Deploy the JavaEE application on the Apache Tomcat server.**


**Dependencies**
### Back-end

* Java EE: Enterprise Edition of the Java platform for building robust and scalable enterprise applications.

* Apache Tomcat: Servlet container that implements the Java Servlet and JavaServer Pages technologies. (Version 10.1.24)

### Database

* MySQL Connector: Java-based driver for connecting to MySQL databases. (Version 8.0.33)
* Java Naming and Directory Interface (JNDI): Java API for connecting to directory services, used for managing database connections efficiently through connection pooling, enhancing performance and scalability.


### Development Tools

* Maven: Build automation and project management tool. (Version 4.0.0)
 

**Accessing the API**

*The API will be available at `http://localhost:8080/`.*


## API Documentation
For detailed API documentation, please refer to the project’s Swagger UI available.

* [Customer API documentation](#customer-api-link)
* [Item API documentation](#item-api-link)
* [Order API documentation](#order-api-link)
* [Order Details API documentation](#order-details-api-link)



## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

© 2024 All Rights Reserved, Designed By [sasobapriyanjana11]

## Contact

This version includes all the detailed installation and configuration steps, along with the necessary license and contact information. Let me know if there's anything else you'd like to add!

