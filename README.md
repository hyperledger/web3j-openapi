Web3j Open API
==============

## Modules

 * openapi: JAX-RS, Web3j Core (only interfaces), Swagger annotations
 * core: openapi, Jersey Core, Swager UI
 * server: core, Jersey Server, Jetty, integration tests
 * codegen: Mustache, Kotlinpoet?, openapi (Based on CorDappGenerator for Gradle project)
 * client: core, Jersey Client
 * helloworld:
   * openapi
   * server: web3j-openapi-server (org.web3j.openapi.server.MainKt or generated main)

## Use cases

```ssh
$ epirus generate openapi Greeter.sol -p com.helloworld -o /home/rachid/helloworld
```

Creates Gradle project with one module to run a server (with application plugin) and another with interfaces.
```ssh
$ cd /home/rachid/helloworld
$ ./gradle run // Starts the server exposing Greeter.sol
...
```

Also start the server with:
```ssh
$ ./gradle shadowJar // Create an executable JAR that starts the server
$ java -jar helloworld-0.1.0-all.jar
...
```

To interact via Java/Kotlin with this API:

```groovy
dependencies {
    implementation "web3j-openapi:web3j-openapi-client:0.1.0"
}
```

And within a client application:

```
val service = ClientService("http://localhost:8080")
val helloWorld = ClientBuilder.build(HelloWorldApi::class.java, service)

val receipt = helloWorldApi.contracts.greeter.deploy(
    GreeterDeployParameters("Test greeter")
)

val greeter = helloWorldApi.contracts.greeter.load(receipt.contractAddress)
```
