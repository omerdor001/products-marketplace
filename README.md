# Products Marketplace

A **Full-Stack Products Marketplace** application that allows users to browse, buy, and manage products. Supports multiple user roles, secure product management, and a responsive frontend interface.

---

## Features

* **User Roles**: Admins, Resellers, and Customers.
* **Product Management**: Add, edit, and delete products (with images and descriptions).
* **Marketplace Operations**: Users can browse, search, and purchase products.
* **Frontend**: Responsive UI for a smooth user experience.
* **Secure Transactions**: Handles pricing, cost, and margin calculations.
* **UUID-based Database Entries**: Each product and admin has a unique identifier.

---

## Tech Stack

* **Backend**: Java (Spring Boot, JPA/Hibernate)
* **Frontend**: JavaScript / React
* **Database**: PostgreSQL
* **Tools**: Docker (for local database setup), Maven, JUnit (for testing)
* **Others**: RESTful API design

---

## Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/omerdor001/products-marketplace.git
   cd products-marketplace
   ```

2. **Configure PostgreSQL** in `application.properties`:

   ```properties
   spring.application.name=demo
   spring.datasource.url=jdbc:postgresql://db:5432/coupons
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

3. **Run the backend**

   Make sure you have Docker installed and start the PostgreSQL container:

   ```bash
   docker-compose up --build
   ```

   Backend will run at `http://localhost:8080`.
   Frontend will run at `http://localhost:5173`.

   
5. **Stop the backend and Docker containers**

   Stop the backend process (Ctrl+C if running in terminal) and then stop Docker containers:

   ```bash
   docker-compose down
   ```

---

## Usage

* **Admin**: Manage products.
* **Reseller**: Browse marketplace, purchase products.
* **Customer**: Browse marketplace, purchase products.

The frontend communicates with the backend API to perform all operations.

---

## Running Tests

### Backend

```bash
./mvnw test
```
