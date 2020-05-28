pragma solidity ^0.4.2;

contract Greeter{

    string greeting;

    constructor(string greet) public {
        greeting = greet;
    }

    function newGreeting(string greet) public {
        greeting = greet;
    }

    function greet() public view returns (string) {
        return greeting;
    }
}
