// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.28;

contract ElderCareContract {
    // Declare a variable to store the elder's name
    string public elderName;

    // Declare a variable to store the elder's age
    uint public elderAge;

    // Declare an event to notify when a new elder is added
    event ElderAdded(string name, uint age);

    // Constructor to initialize the elder's name and age
    constructor(string memory name, uint age) {
        elderName = name;
        elderAge = age;
    }

    // Function to set a new elder's name and age
    function setElderDetails(string memory name, uint age) public {
        elderName = name;
        elderAge = age;
        emit ElderAdded(name, age);
    }

    // Function to get elder details
    function getElderDetails() public view returns (string memory, uint) {
        return (elderName, elderAge);
    }
}
