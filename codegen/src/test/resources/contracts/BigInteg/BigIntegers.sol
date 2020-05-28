pragma solidity ^0.4.2;

contract BigIntegers {

    function bigInt() public constant returns(uint256 result) {
        return 2**256 - 1;
    }

    function smallInt() public constant returns(uint result) {
        return 10;
    }
    
    function smallInt(uint number) public constant returns(uint result) {
        return number;
    }
}
