# Reviews Service

todo

## Run application

The application expects a running PostgreSQL database:

```shell
docker build -t my-postgres-db -f db.Dockerfile . 
docker run --rm -d --name my-postgresdb-container -p 5432:5432 my-postgres-db
```

Input data is read from a file given at the command line:

```shell
sbt "run data/input.txt"
```

## Run tests

```shell
sbt test
```
