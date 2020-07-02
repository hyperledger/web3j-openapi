pragma solidity ^0.6.8;

contract DuplicateField {

    string public constant NAME = "A";

    string public constant Name = "B";

    string public constant SYMBOL = "SMBL";

    function name() public pure returns (string memory) {
        return NAME;
    }
    
    function NAme() public pure returns (string memory) {
        return Name;
    }
    
    function sum(int c1, int c2) public pure returns (int) {
        return c1+c2;
    }
    
    function sum(int c1, int c2, int c3) public pure returns (int) {
        return c1+c2+c3;
    }
    
    function Sum(int c1, int C1) public pure returns (int) {
        return c1+C1;
    }

    function decimals() public returns (uint8) {
    	emit Decimals(1);
        return 0;
    }
    
    event Decimals(int a);

}
