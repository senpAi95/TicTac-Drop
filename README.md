## TicTac Drop ##
This code, based on [Dropwizard](http://www.dropwizard.io/1.1.0/docs/), requires maven and Java 1.8.

## Compile ##
`mvn clean verify`
## Run Service ##
`java -jar target/tictac-drop-1.0-SNAPSHOT.jar server src/main/resources/local.yml`
## Test service manually ##
```
 curl --header "Content-type: Application/json" -X POST http://localhost:8080/drop_token -d'{ "players":["p1", "p2"], "rows":4, "columns":4}'
```

## Database setup
1. Download mongoDb from https://www.mongodb.com/try/download/community
2. Create database ``` droptoken```
    1. Create Collections ```game```, ```moves```, ```players```, ```status```.
3. Have fun.

## images
Game Screenshots can be seen [here](screenshots)