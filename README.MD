![circleci](https://circleci.com/gh/Gontrum/shorturl.svg?style=shield&circle-token=6bc963b5758d26414a4a1add0b970d31bcef8580)

# Url Shortener

This could be used to shorten your urls. 

## TODO:
- Authentication is just for presentation purposes: we should use jwt instead of basic auth
- Everything has to be a little bit more configurable
- Statistics could be more meaningful
- Exception-Handling is not very helpful at the moment/users get the wrong information

## Resources:
### Shorten: 
- POST `/shorten` with the body `{"url": "https://gontrum.io" }` will shorten your URL 
  - You could also basic authenticate here to have access to statistics later
- GET `/{hashValue}` will make a redirect to the related URL or return 404 when nothing is related
### Statistics:
You need to basic authenticate here. Users can see their own data, admin has access to all data
- GET `/statistics/{hashValue}` will return statistics for related URL
- GET `/statistics/top` will show statistics of the five most called URLs
- GET `/statistics/{hashValue}/date` will show how many times the were called on all days
- GET `/statistics/date` will show how many times all urls were called on all days

## Use:
- build via `./gradlew build` this will also execute all tests
- this should result in a shorturl.jar in ./build/libs/
- boot a mongodb-instance via `docker-compose -f ./mongodb/docker-compose.yml`
- run shortener instance via `java -jar ./build/libs/shorturl.jar`