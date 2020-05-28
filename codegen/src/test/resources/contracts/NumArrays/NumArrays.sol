pragma solidity ^0.4.2;

contract NumArrays {

    function getNum() public pure returns(uint[3] result) {
        return [1, 10, 2**256 - 1];
    }

    function idNum(uint[5] numArr) public pure returns(uint[5] result) {
        return numArr;
    }

}