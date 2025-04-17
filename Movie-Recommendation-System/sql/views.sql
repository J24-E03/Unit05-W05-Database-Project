-- View: User Profile
CREATE OR REPLACE VIEW user_profile_view AS
SELECT u.username, u.type, d.full_name, d.date_of_birth, d.email
FROM users u
JOIN user_details d ON u.user_id = d.user_id;

-- View: Movie Actors
CREATE OR REPLACE VIEW movie_actor_view AS
SELECT m.title AS "Movie Title", a.name AS "Actor Name"
FROM movies m
JOIN movie_actors ma ON m.movie_id = ma.movie_id
JOIN actors a ON ma.actor_id = a.actor_id;

-- View: Active Users
CREATE VIEW active_users AS
SELECT u.username, ud.full_name, COUNT(q.query_id) AS total_queries
FROM users u
JOIN queries q ON u.user_id = q.user_id
JOIN user_details ud On u.user_id = ud.user_id
GROUP BY u.username, ud.full_name
ORDER BY total_queries DESC;

-- View: Multi-Genre Movies
CREATE VIEW multi_genre_movies AS
SELECT m.title, COUNT(mg.genre_id) AS genre_count
FROM movies m
JOIN movie_genres mg ON m.movie_id = mg.movie_id
GROUP BY m.title
HAVING COUNT(mg.genre_id) > 1;

-- View: Active Actors
CREATE VIEW active_actors AS
SELECT a.name, COUNT(ma.movie_id) AS movie_count
FROM actors a
JOIN movie_actors ma ON a.actor_id = ma.actor_id
GROUP BY a.name
HAVING COUNT(ma.movie_id) > 1;

-- View: User Query History
CREATE VIEW user_query_history AS
SELECT u.username, m.title, q.query, q.timestamp
FROM users u
JOIN queries q ON u.user_id = q.user_id
JOIN query_movies qm ON q.query_id = qm.query_id
JOIN movies m ON qm.movie_id = m.movie_id;

