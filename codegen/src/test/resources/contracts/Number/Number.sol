pragma solidity ^0.4;


contract Number{

    int number;

    constructor() public {
        number = 1;
    }

    function newNumber(int _number) public {
        number = _number;
    }

    function getNumber() public view returns (int) {
        return number;
    }
}
