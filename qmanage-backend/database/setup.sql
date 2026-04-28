-- Qmanage Database Setup Script
-- Run this in MySQL Workbench or MySQL CLI
-- This script is idempotent: safe to run multiple times.

CREATE DATABASE IF NOT EXISTS Qmanage;
USE Qmanage;

-- =============================================================================
-- 0. CLEANUP (Optional: Use for fresh setup)
-- =============================================================================
SET FOREIGN_KEY_CHECKS = 0;
DROP VIEW IF EXISTS view_order_details;
DROP VIEW IF EXISTS view_outlet_performance;
DROP TABLE IF EXISTS outlet_categories;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS outlets;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 1. NORMALIZATION & DDL (Data Definition Language)
-- =============================================================================

-- Categories table (3NF: Separating categories into their own entity)
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL DEFAULT '',
    email VARCHAR(255) NOT NULL UNIQUE, -- Candidate Key
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email) -- Indexing for faster lookups
);

-- Outlets table
-- Added email and password for vendor login compatibility
CREATE TABLE IF NOT EXISTS outlets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE, -- Candidate Key
    password VARCHAR(255) NOT NULL,
    rating DECIMAL(2, 1) DEFAULT 0.0,
    wait_time_minutes INT UNSIGNED DEFAULT 0,
    queue_count INT UNSIGNED DEFAULT 0,
    image_url VARCHAR(500) DEFAULT '',
    is_open BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_outlets_name (name),
    INDEX idx_outlets_open (is_open)
);

-- Outlet Categories Junction Table (Normalization: Handling Many-to-Many)
CREATE TABLE IF NOT EXISTS outlet_categories (
    outlet_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (outlet_id, category_id),
    FOREIGN KEY (outlet_id) REFERENCES outlets(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Menu items table
CREATE TABLE IF NOT EXISTS menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    outlet_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(100) DEFAULT '', -- Kept for simplicity, could also be normalized
    is_veg BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(500) DEFAULT '',
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_menu_item_outlet_name (outlet_id, name),
    INDEX idx_menu_items_outlet_id (outlet_id),
    FOREIGN KEY (outlet_id) REFERENCES outlets(id) ON DELETE CASCADE
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    outlet_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('Received', 'Preparing', 'Ready', 'Completed', 'Cancelled') DEFAULT 'Received',
    token_number VARCHAR(20) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_orders_token_number (token_number),
    INDEX idx_orders_user_id (user_id),
    INDEX idx_orders_outlet_id (outlet_id),
    INDEX idx_orders_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (outlet_id) REFERENCES outlets(id) ON DELETE CASCADE
);

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    UNIQUE KEY uk_order_item_unique (order_id, menu_item_id),
    INDEX idx_order_items_order_id (order_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    outlet_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (outlet_id) REFERENCES outlets(id) ON DELETE CASCADE
);

-- =============================================================================
-- 2. DML (Data Manipulation Language) & Sample Data
-- =============================================================================

-- Insert Categories
INSERT IGNORE INTO categories (name) VALUES ('Coffee'), ('Snacks'), ('Beverages'), ('Indian'), ('Rolls'), ('Fried'), ('Juices'), ('Smoothies'), ('Shakes'), ('Burgers');

-- Insert sample user (for testing order placement)
-- Password is 'password123' hashed (simulated)
INSERT IGNORE INTO users (id, name, email, password, phone) 
VALUES (1, 'Test User', 'test@user.com', 'hashed_password', '1234567890');

-- Insert sample outlets
INSERT INTO outlets (name, email, password, rating, wait_time_minutes, queue_count, image_url, is_open) VALUES
('Campus Cafe', 'campus@cafe.com', 'hashed_pass', 4.5, 5, 3, '/public/images/campus_cafe.png', true),
('Food Court', 'food@court.com', 'hashed_pass', 4.2, 12, 8, '/public/images/food_court.png', true),
('Juice Bar', 'juice@bar.com', 'hashed_pass', 4.7, 3, 2, '/public/images/juice_bar.png', true),
('Quick Bites', 'quick@bites.com', 'hashed_pass', 4.0, 8, 5, '/public/images/quick_bites.png', false)
ON DUPLICATE KEY UPDATE rating = VALUES(rating), image_url = VALUES(image_url);

-- Link Outlets and Categories
INSERT IGNORE INTO outlet_categories (outlet_id, category_id)
SELECT o.id, c.id FROM outlets o, categories c 
WHERE o.name = 'Campus Cafe' AND c.name IN ('Coffee', 'Snacks', 'Beverages');

INSERT IGNORE INTO outlet_categories (outlet_id, category_id)
SELECT o.id, c.id FROM outlets o, categories c 
WHERE o.name = 'Food Court' AND c.name IN ('Rolls', 'Indian', 'Fried');

-- Insert sample menu items
INSERT INTO menu_items (outlet_id, name, description, price, category, is_veg, image_url, is_available) VALUES
((SELECT id FROM outlets WHERE name = 'Campus Cafe'), 'Cappuccino', 'Rich and creamy cappuccino', 120.00, 'Beverages', true, '/public/images/cappuccino.png', true),
((SELECT id FROM outlets WHERE name = 'Campus Cafe'), 'Veg Sandwich', 'Fresh veggies with cheese', 80.00, 'Snacks', true, '/public/images/veg_burger.png', true),
((SELECT id FROM outlets WHERE name = 'Food Court'), 'Paneer Roll', 'Spicy paneer stuffed roll', 100.00, 'Rolls', true, '/public/images/paneer_roll.png', true),
((SELECT id FROM outlets WHERE name = 'Food Court'), 'Chicken Biryani', 'Aromatic chicken biryani', 180.00, 'Main Course', false, '/public/images/food_court.png', true),
((SELECT id FROM outlets WHERE name = 'Juice Bar'), 'Mango Smoothie', 'Fresh mango smoothie', 90.00, 'Smoothies', true, '/public/images/mango_smoothie.png', true),
((SELECT id FROM outlets WHERE name = 'Quick Bites'), 'Veg Burger', 'Classic veg burger with fries', 110.00, 'Burgers', true, '/public/images/veg_burger.png', true)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    price = VALUES(price),
    category = VALUES(category),
    is_veg = VALUES(is_veg),
    image_url = VALUES(image_url),
    is_available = VALUES(is_available);

-- =============================================================================
-- 3. ADVANCED SQL: VIEWS, FUNCTIONS, PROCEDURES
-- =============================================================================

-- VIEW: Detailed Order Summary (Joins & DQL)
CREATE OR REPLACE VIEW view_order_details AS
SELECT 
    o.id AS id,
    o.user_id,
    o.outlet_id,
    u.name AS user_name,
    ot.name AS outlet_name,
    o.total_amount,
    o.status,
    o.token_number,
    o.created_at,
    IFNULL(GROUP_CONCAT(mi.name SEPARATOR ', '), '') AS items_ordered
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN outlets ot ON o.outlet_id = ot.id
LEFT JOIN order_items oi ON o.id = oi.order_id
LEFT JOIN menu_items mi ON oi.menu_item_id = mi.id
GROUP BY o.id;

-- VIEW: Outlet Performance (Group By & Having)
CREATE OR REPLACE VIEW view_outlet_performance AS
SELECT 
    ot.name,
    COUNT(o.id) AS total_orders,
    AVG(ot.rating) AS avg_rating,
    SUM(o.total_amount) AS revenue
FROM outlets ot
LEFT JOIN orders o ON ot.id = o.outlet_id
GROUP BY ot.id
HAVING revenue > 0 OR total_orders = 0;

-- SCALAR FUNCTION: Calculate Discounted Price
DROP FUNCTION IF EXISTS fn_calculate_discount;
DELIMITER //
CREATE FUNCTION fn_calculate_discount(price DECIMAL(10,2), discount_pct INT) 
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN price - (price * discount_pct / 100);
END //
DELIMITER ;

-- STORED PROCEDURE: Place Order with TRANSACTION (ACID)
DROP PROCEDURE IF EXISTS sp_place_order;
DELIMITER //
CREATE PROCEDURE sp_place_order(
    IN p_user_id INT,
    IN p_outlet_id INT,
    IN p_total_amount DECIMAL(10,2),
    IN p_token_number VARCHAR(20),
    OUT p_order_id INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        -- ROLLBACK on error
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Order placement failed, rolling back.';
    END;

    -- START TRANSACTION
    START TRANSACTION;

    -- 1. Insert Order
    INSERT INTO orders (user_id, outlet_id, total_amount, token_number)
    VALUES (p_user_id, p_outlet_id, p_total_amount, p_token_number);
    
    SET p_order_id = LAST_INSERT_ID();

    -- 2. Update Outlet Queue Count (Aggregation concept)
    UPDATE outlets 
    SET queue_count = queue_count + 1 
    WHERE id = p_outlet_id;

    -- COMMIT if everything is fine
    COMMIT;
END //
DELIMITER ;

SELECT '✅ Database setup completed successfully!' AS status;
