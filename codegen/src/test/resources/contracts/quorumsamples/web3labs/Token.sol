pragma solidity ^0.5.2;

import "../openzeppelin/token/ERC20/ERC20.sol";
import "../openzeppelin/token/ERC20/ERC20Burnable.sol";
import "../openzeppelin/token/ERC20/ERC20Detailed.sol";
import "../openzeppelin/token/ERC20/ERC20Mintable.sol";
import "../openzeppelin/token/ERC20/ERC20Pausable.sol";

contract Token is ERC20, ERC20Burnable, ERC20Detailed, ERC20Mintable, ERC20Pausable {

    constructor(
        uint totalSupply,
        string memory name, string memory symbol, uint8 decimals)
        public ERC20Detailed(name, symbol, decimals) {

        _mint(msg.sender, totalSupply);
    }
}
