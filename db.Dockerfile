FROM postgres
ENV POSTGRES_PASSWORD docker
ENV POSTGRES_DB reviewdb
COPY src/it/resources/reviews.sql /docker-entrypoint-initdb.d/
