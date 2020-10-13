Web3j Open API
==============

[![Build Status](https://travis-ci.org/web3j/web3j-openapi.svg?branch=master)](https://travis-ci.org/web3j/web3j-openapi)

Web3j-OpenAPI is a [OpenAPI](https://swagger.io/specification/) client and server generator from 
[Solidity](https://solidity.readthedocs.io/) smart contracts. it provides a way to interact with the Ethereum blockchain via simple and intuitive HTTP requests, abstracting the coding layer. These interactions can be done using :
- Plain HTTP requests
- Via the Swagger-UI, which is generated with every project
- A client application utilizing the `webj3-openapi-client` client implementation

The workflow can be summed in the following steps:
- Writing a solidity smart contract
- Generating the corresponding **OpenAPI** project using **Web3j-OpenAPI**
- Running the generated project as a standalone server
- Sending HTTP requests using `Swagger-UI`, client application or `Curl` request.


# Getting started with Web3j-OpenAPI
To generate an OpenAPI project using the [Web3j-OpenAPI](https://github.com/web3j/web3j-openapi) generator, you need to have the [Epirus-CLI](https://docs.epirus.io/quickstart) installed on your machine (Note - the Epirus CLI has replaced the Web3j CLI). 
It’s easy to do (for Windows instructions head [here](https://docs.epirus.io/quickstart/#installation)):
	
```ssh
$ curl -L get.epirus.io | sh
```

### Create a Hello World project
To create a base OpenAPI project using a `Hello World` contract, run the following :

```ssh
$ epirus openapi new
```

You can also generate a `Web3j-OpenAPI` project using the [Web3j-OpenAPI-gradle-plugin](https://github.com/web3j/web3j-openapi-gradle-plugin).

### Configure the project
After having the generated project, you can use the [Epirus-CLI](https://docs.epirus.io) to run it using the following command (Note: You will need to create an [Epirus](https://www.web3labs.com/epirus) Account).

```ssh
$ epirus login
$ epirus run rinkeby|ropsten
```

Alternatively, you can configure your application with the following environment variables:

```ssh
$ export WEB3J_ENDPOINT=<link_to_your_Ethereum_node>
$ export WEB3J_PRIVATE_KEY=<your_private_key>
$ export WEB3J_OPENAPI_HOST=localhost
$ export WEB3J_OPENAPI_PORT=9090
```

### Run the project
If you aren't using the [Epirus-CLI](https://docs.epirus.io/quickstart/#deployment), you may run the project using the following Gradle target:

```ssh
$ cd <project_folder>
$ ./gradlew run
```

Then, you should be seeing the server logs.

### Interact with the project

#### SwaggerUI
The easiest way to interact with the generated project is via the generated `Swagger-UI` which can be found on `http://<host>:<port>/swagger-ui`.

![image](https://github.com/web3j/web3j-docs/blob/master/docs/img/Web3j-OpenAPI/SwaggerUI_1.png)

#### Web3j-OpenAPI client
Also, you can use our client implementation via adding the following dependency to your project:
```groovy
dependencies {
    implementation "org.web3j.openapi:web3j-openapi-client:4.7.0"
}
```

Then, within the application:

```kotlin
val service = ClientService("http://localhost:9090")
val app = ClientFactory.create(<AppNameApi>::class.java, service)

// Then you have access to all the API resources
val receipt = app.contracts.contractName.deploy()

println("Deployment receipt: ${receipt.contractAddress}")

// ...
```

#### **For more information**, please refer to the [documentation](https://docs.web3j.io/web3j_openapi).
