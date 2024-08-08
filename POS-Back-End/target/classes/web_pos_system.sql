
DROP DATABASE IF EXISTS pos_system;

CREATE DATABASE IF NOT EXISTS pos_system;

USE pos_system;

CREATE TABLE customer (
                          id VARCHAR(15) PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          address VARCHAR(255) NOT NULL,
                          salary DOUBLE NOT NULL
);

CREATE TABLE item (
                      code VARCHAR(15) PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      qty INT NOT NULL,
                      price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE orders (
                        orderId VARCHAR(15) PRIMARY KEY,
                        orderDate DATE NOT NULL,
                        custId VARCHAR(15) NOT NULL,
                        total DECIMAL(10, 2) NOT NULL,
                        discount DECIMAL(5, 2) NOT NULL,
                        subTotal DECIMAL(10, 2) NOT NULL,
                        cash DECIMAL(10, 2) NOT NULL,
                        balance DECIMAL(10, 2) NOT NULL,
                        CONSTRAINT FOREIGN KEY (custId) REFERENCES customer(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE order_details (
                               orderId VARCHAR(15) NOT NULL,
                               itemCode VARCHAR(15) NOT NULL,
                               unit_price DECIMAL(10, 2) NOT NULL,
                               qty INT,
                               PRIMARY KEY (orderId, itemCode),
                               CONSTRAINT fk_order FOREIGN KEY (orderId) REFERENCES orders(orderId) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT fk_item FOREIGN KEY (itemCode) REFERENCES item(code) ON DELETE CASCADE ON UPDATE CASCADE
);
