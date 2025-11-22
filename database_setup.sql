-- Create database (run this first if database doesn't exist)
CREATE DATABASE lithuanian_word_db;

-- Connect to the database
\c lithuanian_word_db

-- Words table
CREATE TABLE words (
    id SERIAL PRIMARY KEY,
    word VARCHAR(255) NOT NULL,
    definition_lt TEXT NOT NULL,
    definition_en TEXT NOT NULL,
    usage_example_lt TEXT,
    usage_example_en TEXT,
    image_url VARCHAR(500),
    word_date DATE NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT idx_word_date UNIQUE (word_date)
);

CREATE INDEX idx_word_date ON words(word_date);

-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Claimed cards table
CREATE TABLE claimed_cards (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    word_id BIGINT NOT NULL,
    claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_word FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_word UNIQUE (user_id, word_id)
);

CREATE INDEX idx_user_id ON claimed_cards(user_id);
CREATE INDEX idx_word_id ON claimed_cards(word_id);