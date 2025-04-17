-- Create Table Users
CREATE TABLE users (
    user_id  SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    type     VARCHAR(50) CHECK (type IN ('NORMAL_USER', 'PREMIUM_USER'))
);

-- Create Table user_details
CREATE TABLE user_details (
    user_id       INT PRIMARY KEY,
    full_name     VARCHAR(100),
    email         VARCHAR(100) UNIQUE,
    date_of_birth DATE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

ALTER TABLE user_details
ADD CONSTRAINT check_user_age
CHECK (date_of_birth <= CURRENT_DATE - INTERVAL '18 years');

-- Create Table Movies
CREATE TABLE movies (
    movie_id     SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    release_date DATE         NOT NULL,
    overview     TEXT,
    rating       DECIMAL(3, 1) CHECK (rating >= 0.0 AND rating <= 10.0),
    UNIQUE (title, release_date)
);

-- Create Table Genres
CREATE TABLE genres (
    genre_id SERIAL PRIMARY KEY,
    name     VARCHAR(100) NOT NULL UNIQUE
);

-- Create Table Movie_Genres
CREATE TABLE movie_genres (
    movie_id INT REFERENCES movies (movie_id) ON DELETE CASCADE,
    genre_id INT REFERENCES genres (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, genre_id)
);

-- Create Table Actors
CREATE TABLE actors (
    actor_id SERIAL PRIMARY KEY,
    name     VARCHAR(150) NOT NULL
);

-- Create Table Movie_Actors
CREATE TABLE movie_actors (
    movie_id INT REFERENCES movies (movie_id) ON DELETE CASCADE,
    actor_id INT REFERENCES actors (actor_id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, actor_id)
);

-- Create Table Queries
CREATE TABLE queries (
    query_id  SERIAL PRIMARY KEY,
    query     TEXT NOT NULL,
    user_id   INT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Create Table Query_Movies
CREATE TABLE query_movies (
    query_id INT NOT NULL,
    movie_id INT NOT NULL,
    PRIMARY KEY (query_id, movie_id),
    FOREIGN KEY (query_id) REFERENCES queries (query_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies (movie_id) ON DELETE CASCADE
);

