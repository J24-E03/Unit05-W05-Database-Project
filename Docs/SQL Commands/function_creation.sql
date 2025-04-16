-- Function: Get user profile
CREATE OR REPLACE FUNCTION get_user_profile(id_of_user INT)
RETURNS TABLE(full_name VARCHAR, date_of_birth DATE, email VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT d.full_name, d.date_of_birth, d.email
    FROM users u
    JOIN user_details d ON u.user_id = d.user_id
    WHERE u.user_id = id_of_user;
END;
$$ LANGUAGE plpgsql;

-- Function: Get user queries with movie title
CREATE OR REPLACE FUNCTION get_user_queries(user_id_input INT)
RETURNS TABLE(query TEXT, movie_title TEXT, query_time TIMESTAMP) AS $$
BEGIN
    RETURN QUERY
    SELECT q.query,
           COALESCE(m.title, 'No Movie Returned'),
           q.timestamp
    FROM queries q
    LEFT JOIN query_movies qm ON q.query_id = qm.query_id
    LEFT JOIN movies m ON m.movie_id = qm.movie_id
    WHERE q.user_id = user_id_input;
END;
$$ LANGUAGE plpgsql;

-- Function: Get user's favorite genres
CREATE OR REPLACE FUNCTION get_favorite_genres(user_input_id INT)
RETURNS TABLE(genre_name TEXT, count INT) AS $$
BEGIN
    RETURN QUERY
    SELECT g.name, COUNT(*) AS count
    FROM queries q
    JOIN query_movies qm ON q.query_id = qm.query_id
    JOIN movie_genres mg ON mg.movie_id = qm.movie_id
    JOIN genres g ON g.genre_id = mg.genre_id
    WHERE q.user_id = user_input_id
    GROUP BY g.name
    ORDER BY count DESC
    LIMIT 3;
END;
$$ LANGUAGE plpgsql;

-- Function: Delete old queries
CREATE OR REPLACE FUNCTION delete_old_queries()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM queries
    WHERE timestamp < NOW() - INTERVAL '6 months';
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Function: Prevent duplicate email
CREATE OR REPLACE FUNCTION prevent_duplicate_email()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM user_details
        WHERE email = NEW.email AND user_id != NEW.user_id
    ) THEN
        RAISE EXCEPTION 'The email address "%" is already in use by another user.', NEW.email;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

