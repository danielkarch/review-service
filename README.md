# Review Service

This repository contains the _review-service_, a web service that returns helpful insights about product reviews.

## Run application

The requirements for this task mentioned that the input might be too large to fit into memory. Hence I have decided 
to store the data in a database. 
The application expects a running PostgreSQL database:

```shell
docker build -t review-db -f db.Dockerfile . 
docker run --rm -d --name review-db-container -p 5432:5432 review-db
```

Input data is read from a file given at the command line:

```shell
./sbt "run data/input.txt"
```

Once the service is running, you can post data to the `/best-rated` endpoint:
```
curl http://localhost:8086/best-rated -d'{
       "start": "01.01.2010",
       "end": "31.12.2020",
       "limit": 2,
       "min_number_reviews": 2
     }'
```
The API documentation can be found under [http://localhost:8086/docs](http://localhost:8086/docs)

## Run tests

```shell
./sbt test It/test
```
