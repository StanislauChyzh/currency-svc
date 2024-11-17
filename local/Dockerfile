FROM postgres:15.3

RUN apt-get update && apt-get install -y postgresql-15-pgvector \
    && rm -rf /var/lib/apt/lists/*

# Copy in the load-extensions script
ADD initial-script.sql /docker-entrypoint-initdb.d/

EXPOSE 5432


