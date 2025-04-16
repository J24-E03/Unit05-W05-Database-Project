-- Trigger: Clean up old queries after insert
DROP TRIGGER IF EXISTS clean_old_queries ON queries;

CREATE TRIGGER clean_old_queries
AFTER INSERT ON queries
FOR EACH STATEMENT
EXECUTE FUNCTION delete_old_queries();

-- Trigger: Prevent duplicate email update
DROP TRIGGER IF EXISTS trigger_prevent_duplicate_email ON user_details;

CREATE TRIGGER trigger_prevent_duplicate_email
BEFORE UPDATE ON user_details
FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_email();

