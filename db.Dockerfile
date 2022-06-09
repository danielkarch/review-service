FROM postgres
ENV POSTGRES_PASSWORD docker
ENV POSTGRES_DB reviewdb
COPY reviews.sql /docker-entrypoint-initdb.d/
