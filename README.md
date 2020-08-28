This repo contains a way to reproduce the issue logged here:
https://github.com/graphql-java-kickstart/graphql-java-servlet/issues/264

Clone the repo then run `./gradlew test`

You should see one successful test, and one failing test. Both tests simply make a GraphQL request, then a REST request
to the Actuator endpoint to see if it's got data for the expected operation name. One test succeeds as it posts this
JSON:

```json
{
  "operationName": "MyOperationName",
  "query": "query MyOperationName {...}"
}
```

And one test fails as it posts this:

```json
{
  "query": "query MyOperationName {...}"
}
```

I believe both tests should pass.
