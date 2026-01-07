-- =========================
-- Customers Dataset
-- =========================
CREATE TABLE IF NOT EXISTS olist_customers_dataset (
    customer_id VARCHAR(32) PRIMARY KEY,
    customer_unique_id VARCHAR(32),
    customer_zip_code_prefix VARCHAR(10),
    customer_city VARCHAR(255),
    customer_state VARCHAR(2)
);

-- =========================
-- Geolocation Dataset
-- =========================
CREATE TABLE IF NOT EXISTS olist_geolocation_dataset (
    geolocation_zip_code_prefix VARCHAR(10),
    geolocation_lat DECIMAL(18, 15),
    geolocation_lng DECIMAL(18, 15),
    geolocation_city VARCHAR(255),
    geolocation_state VARCHAR(2)
);

-- =========================
-- Sellers Dataset
-- =========================
CREATE TABLE IF NOT EXISTS olist_sellers_dataset (
    seller_id VARCHAR(32) PRIMARY KEY,
    seller_zip_code_prefix VARCHAR(10),
    seller_city VARCHAR(255),
    seller_state VARCHAR(2)
);

-- =========================
-- Products Dataset
-- =========================
CREATE TABLE IF NOT EXISTS olist_products_dataset (
    product_id VARCHAR(32) PRIMARY KEY,
    product_category_name VARCHAR(255),
    product_name_lenght INTEGER,
    product_description_lenght INTEGER,
    product_photos_qty INTEGER,
    product_weight_g INTEGER,
    product_length_cm INTEGER,
    product_height_cm INTEGER,
    product_width_cm INTEGER
);

-- =========================
-- Orders Dataset
-- =========================
CREATE TABLE IF NOT EXISTS olist_orders_dataset (
    order_id VARCHAR(32) PRIMARY KEY,
    customer_id VARCHAR(32),
    order_status VARCHAR(50),
    order_purchase_timestamp TIMESTAMP,
    order_approved_at TIMESTAMP,
    order_delivered_carrier_date TIMESTAMP,
    order_delivered_customer_date TIMESTAMP,
    order_estimated_delivery_date TIMESTAMP
);

-- =========================
-- Order Items Dataset
-- =========================
CREATE TABLE IF NOT EXISTS olist_order_items_dataset (
    order_id VARCHAR(32),
    order_item_id INTEGER,
    product_id VARCHAR(32),
    seller_id VARCHAR(32),
    shipping_limit_date TIMESTAMP,
    price DECIMAL(10, 2),
    freight_value DECIMAL(10, 2),

    CONSTRAINT pk_order_items
        PRIMARY KEY (order_id, order_item_id)

);

-- =========================
-- Order Payments Dataset
-- =========================
CREATE TABLE IF NOT EXISTS olist_order_payments_dataset (
    order_id VARCHAR(32),
    payment_sequential INTEGER,
    payment_type VARCHAR(50),
    payment_installments INTEGER,
    payment_value DECIMAL(10, 2),

    CONSTRAINT pk_order_payments
        PRIMARY KEY (order_id, payment_sequential)

);

-- =========================
-- Order Reviews Dataset
-- =========================
CREATE TABLE IF NOT EXISTS olist_order_reviews_dataset (
    review_id VARCHAR(32) PRIMARY KEY,
    order_id VARCHAR(32),
    review_score INTEGER,
    review_comment_title VARCHAR(255),
    review_comment_message TEXT,
    review_creation_date TIMESTAMP,
    review_answer_timestamp TIMESTAMP
);


ALTER TABLE olist_orders_dataset
ADD CONSTRAINT fk_orders_customer
FOREIGN KEY (customer_id)
REFERENCES olist_customers_dataset(customer_id)
NOT VALID;
ALTER TABLE olist_order_items_dataset

ADD CONSTRAINT fk_order_items_order
FOREIGN KEY (order_id)
REFERENCES olist_orders_dataset(order_id)
NOT VALID;

ALTER TABLE olist_order_items_dataset
ADD CONSTRAINT fk_order_items_product
FOREIGN KEY (product_id)
REFERENCES olist_products_dataset(product_id)
NOT VALID;

ALTER TABLE olist_order_items_dataset
ADD CONSTRAINT fk_order_items_seller
FOREIGN KEY (seller_id)
REFERENCES olist_sellers_dataset(seller_id)
NOT VALID;

ALTER TABLE olist_order_reviews_dataset
ADD CONSTRAINT fk_reviews_order
FOREIGN KEY (order_id)
REFERENCES olist_orders_dataset(order_id)
NOT VALID;

ALTER TABLE olist_order_payments_dataset
ADD CONSTRAINT fk_payments_order
FOREIGN KEY (order_id)
REFERENCES olist_orders_dataset(order_id)
NOT VALID;

ALTER TABLE olist_order_items_dataset
ADD CONSTRAINT pk_order_items
PRIMARY KEY (order_id, order_item_id);

ALTER TABLE olist_order_payments_dataset
ADD CONSTRAINT pk_order_payments
PRIMARY KEY (order_id, payment_sequential);

ALTER TABLE olist_orders_dataset
VALIDATE CONSTRAINT fk_orders_customer;

ALTER TABLE olist_order_items_dataset
VALIDATE CONSTRAINT fk_order_items_order;

-- =========================
-- Indexes for Performance
-- =========================
CREATE INDEX IF NOT EXISTS idx_orders_customer_id
    ON olist_orders_dataset(customer_id);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id
    ON olist_order_items_dataset(order_id);

CREATE INDEX IF NOT EXISTS idx_order_items_seller_id
    ON olist_order_items_dataset(seller_id);

CREATE INDEX IF NOT EXISTS idx_order_items_product_id
    ON olist_order_items_dataset(product_id);

CREATE INDEX IF NOT EXISTS idx_reviews_order_id
    ON olist_order_reviews_dataset(order_id);

ALTER TABLE olist_orders_dataset
VALIDATE CONSTRAINT fk_orders_customer;