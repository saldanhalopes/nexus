-- ==========================================
-- Local Database Setup Script for Nexus
-- ==========================================

-- 1. Create the user used by the application
-- Note: Replace 'Beast666@0123' with your preferred password if needed, 
-- but match it in application.yml.
DO
$do$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'db_lablims') THEN
      CREATE ROLE db_lablims WITH LOGIN PASSWORD 'Beast666@0123';
   END IF;
END
$do$;

-- 2. Create the database
-- Note: You might need to run this outside of a transaction or simply use:
-- CREATE DATABASE treinamentos OWNER db_lablims;
SELECT 'CREATE DATABASE treinamentos OWNER db_lablims'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'treinamentos')\gexec

-- 3. Grant privileges
GRANT ALL PRIVILEGES ON DATABASE treinamentos TO db_lablims;
