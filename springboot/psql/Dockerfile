FROM postgres:9.6-alpine

# Copy initialization SQL script to the Docker image
COPY init-db.sql /docker-entrypoint-initdb.d/

# Expose PostgreSQL port
EXPOSE 5432