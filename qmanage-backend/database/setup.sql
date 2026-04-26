-- Qmanage Database Setup Script
-- Run this in MySQL Workbench or MySQL CLI
-- This script is idempotent: safe to run multiple times.

CREATE DATABASE IF NOT EXISTS Qmanage;
USE Qmanage;

-- Users table
-- Keep column name as `password` for current backend compatibility.
-- Store only hashed passwords (e.g. bcrypt hash), never plain text.
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL DEFAULT '',
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
);

-- Outlets table
CREATE TABLE IF NOT EXISTS outlets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    categories VARCHAR(255) DEFAULT '',
    rating DECIMAL(2, 1) DEFAULT 0.0,
    wait_time_minutes INT UNSIGNED DEFAULT 0,
    queue_count INT UNSIGNED DEFAULT 0,
    image_url VARCHAR(500) DEFAULT '',
    is_open BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_outlets_name (name),
    INDEX idx_outlets_open (is_open)
);

-- Menu items table
CREATE TABLE IF NOT EXISTS menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    outlet_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT DEFAULT '',
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(100) DEFAULT '',
    image_url VARCHAR(500) DEFAULT '',
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_menu_item_outlet_name (outlet_id, name),
    INDEX idx_menu_items_outlet_id (outlet_id),
    INDEX idx_menu_items_category (category),
    FOREIGN KEY (outlet_id) REFERENCES outlets(id) ON DELETE CASCADE
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    outlet_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('Received', 'Preparing', 'Ready', 'Completed', 'Cancelled') DEFAULT 'Received',
    token_number VARCHAR(20) NOT NULL,
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
    quantity INT UNSIGNED NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    UNIQUE KEY uk_order_item_unique (order_id, menu_item_id),
    INDEX idx_order_items_order_id (order_id),
    INDEX idx_order_items_menu_item_id (menu_item_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

-- Insert/update sample outlets (idempotent)
INSERT INTO outlets (
    name, categories, rating, wait_time_minutes, queue_count, image_url, is_open
) VALUES
('Campus Cafe', 'Coffee • Snacks • Beverages', 4.5, 5, 3, '', true),
('Food Court', 'Rolls • Indian • Fried', 4.2, 12, 8, '', true),
('Juice Bar', 'Juices • Smoothies • Shakes', 4.7, 3, 2, '', true),
('Quick Bites', 'Sandwiches • Burgers • Wraps', 4.0, 8, 5, '', false)
ON DUPLICATE KEY UPDATE
    categories = VALUES(categories),
    rating = VALUES(rating),
    wait_time_minutes = VALUES(wait_time_minutes),
    queue_count = VALUES(queue_count),
    image_url = VALUES(image_url),
    is_open = VALUES(is_open);

-- Insert/update sample menu items (idempotent)
INSERT INTO menu_items (
    outlet_id, name, description, price, category, image_url, is_available
) VALUES
-- Campus Cafe items
((SELECT id FROM outlets WHERE name = 'Campus Cafe'), 'Cappuccino', 'Rich and creamy cappuccino', 120.00, 'Beverages', '', true),
((SELECT id FROM outlets WHERE name = 'Campus Cafe'), 'Latte', 'Smooth cafe latte', 140.00, 'Beverages', '', true),
((SELECT id FROM outlets WHERE name = 'Campus Cafe'), 'Veg Sandwich', 'Fresh veggies with cheese', 80.00, 'Snacks', '', true),
((SELECT id FROM outlets WHERE name = 'Campus Cafe'), 'Chocolate Muffin', 'Freshly baked chocolate muffin', 60.00, 'Snacks', '', true),
-- Food Court items
((SELECT id FROM outlets WHERE name = 'Food Court'), 'Paneer Roll', 'Spicy paneer stuffed roll', 100.00, 'Rolls', '', true),
((SELECT id FROM outlets WHERE name = 'Food Court'), 'Chicken Biryani', 'Aromatic chicken biryani', 180.00, 'Main Course', '', true),
((SELECT id FROM outlets WHERE name = 'Food Court'), 'Veg Thali', 'Complete veg meal', 150.00, 'Main Course', '', true),
((SELECT id FROM outlets WHERE name = 'Food Court'), 'French Fries', 'Crispy golden fries', 70.00, 'Sides & Extras', '', true),
-- Juice Bar items
((SELECT id FROM outlets WHERE name = 'Juice Bar'), 'Mango Smoothie', 'Fresh mango smoothie', 90.00, 'Smoothies', '', true),
((SELECT id FROM outlets WHERE name = 'Juice Bar'), 'Orange Juice', 'Freshly squeezed orange juice', 60.00, 'Juices', '', true),
((SELECT id FROM outlets WHERE name = 'Juice Bar'), 'Banana Shake', 'Creamy banana milkshake', 80.00, 'Shakes', '', true),
-- Quick Bites items
((SELECT id FROM outlets WHERE name = 'Quick Bites'), 'Veg Burger', 'Classic veg burger with fries', 110.00, 'Burgers', '', true),
((SELECT id FROM outlets WHERE name = 'Quick Bites'), 'Chicken Wrap', 'Grilled chicken wrap', 130.00, 'Wraps', '', true),
((SELECT id FROM outlets WHERE name = 'Quick Bites'), 'Club Sandwich', 'Triple decker club sandwich', 150.00, 'Sandwiches', '', true)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    price = VALUES(price),
    category = VALUES(category),
    image_url = VALUES(image_url),
    is_available = VALUES(is_available);

SELECT 'Database setup completed successfully!' AS status;
