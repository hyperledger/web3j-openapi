Web3j Open API
==============

[![Build Status](https://travis-ci.org/web3j/web3j-openapi.svg?branch=master)](https://travis-ci.org/web3j/web3j-openapi)

Web3j-OpenAPI is an OpenAPI generator from solidity smart contracts. 

## Description 
Web3j-OpenAPI provides a way to interact with the Ethereum blockchain via simple and intuitive HTTP requests, abstracting the coding layer. These interactions can be done using plain HTTP requests or via the Swagger-UI, which is generated with every project.

The workflow can be summed in the following steps:
- Writing a solidity smart contract
- Generating the corresponding OpenAPI project using Web3j-OpenAPI
- Running the generated project
- Sending HTTP requests using Curls or Swagger-UI

### Why an OpenAPI generator
As stated above, the Web3j-OpenAPI generator generates an OpenAPI project from a smart contract. Thus, providing a way to interact with the Etheruem blockchain using HTTP requests.

Such generator is beneficial in the following ways:
#### Making it easier to interact with the Ethereum blockchain:
Interacting with the Ethereum blockchain before required knowing a programming language besides solidity, and then hardcoding the desired logic. This, makes it hard for people wishing to get involved in the smart contracts world and adds extra technical requirements in addition to knowing the smart contracts language.
#### Interact with smart contracts without code:
Being able to generate an OpenAPI project from a smart contract and interacting with it using HTTP requests, eliminates the need to code any interactions and being able to send them to the blockchain. Thus, it is legitimate to say that the Web3j-OpenAPI lets you interact with the Ethereum blockchain using no code, besides solidity.


## Use cases
An OpenAPI project can be generated using the [Epirus-CLI](https://github.com/epirus-io/epirus-cli) as follows:

```ssh
$ epirus generate openapi -p com.helloworld -o . --abi helloworld.abi --bin helloworld.bin --name helloworld
```

Then, the generated project can be used in the following ways:

**Creating a Gradle project that can be run using the application plugin:**
```ssh
$ cd helloworld
$ ./gradlew run // Starts the server exposing Helloworld.sol
...
```

**Starting the server using the ShadowJar:**
```ssh
$ ./gradlew shadowJar // Create an executable JAR that starts the server
$ java -jar helloworld-server-all.jar
...
```

**Using the CLI:**

```ssh
$ ./gradlew installDist // Create an executable to start the server
$ ./helloworld-server
```

### Interact with the generated project:
Interactions can be done using HTTP requests either through the `SwaggerUI` on `{host}:{post}/swagger-ui` or `Curls`:
```ssh
$ curl -X POST "http://{host}:{port}/{application name}/contracts/helloworld/{contractAddress}/SayIt" -H  "accept: application/json" -H  "Content-Type: application/json" -d "{\"greeting\":\"Hello OpenAPI\"}"
```

To interact via Java/Kotlin:

```groovy
dependencies {
    implementation "web3j-openapi:web3j-openapi-client:0.1.0"
}
```

And within a client application:

```
val service = ClientService("http://localhost:8080")
val helloWorld = ClientFactory.create(HelloWorldApi::class.java, service)

val receipt = helloWorld.contracts.greeter.deploy(
    GreeterDeployParameters("Test greeter")
)

val greeter = helloWorld.contracts.greeter.load(receipt.contractAddress)
```

**For more explanations**, check the following blog post: (link to blog post), or the demo project
