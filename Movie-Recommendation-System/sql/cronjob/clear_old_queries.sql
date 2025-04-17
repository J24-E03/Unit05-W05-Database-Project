-- Delete old query-movie relations first (to avoid foreign key issues)
DELETE FROM query_movies
WHERE query_id IN (
    SELECT query_id FROM queries WHERE timestamp < CURRENT_DATE - INTERVAL '6 months'
);

-- Delete old queries
DELETE FROM queries
WHERE timestamp < CURRENT_DATE - INTERVAL '6 months';
