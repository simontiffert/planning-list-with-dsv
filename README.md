# planning-list-with-dsv

A reproducer for VariableCorruptionException in combination with DSV

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn quarkus:dev
```

## Reproduce

Get the JSON for a demo
GET http://localhost:8080/api/demoData

Sent the JSON to start solving
POST http://localhost:8080/api/solve

## Issue

Check out SubTask @ShadowSource annotation in line 66/67