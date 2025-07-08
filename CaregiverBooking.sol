// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract CaregiverBooking {
    
    struct Caregiver {
        string name;
        string gender;
        bool available;
    }

    struct Elder {
        string name;
        string gender;
        string shiftStartTime;
        string shiftEndTime;
        address caregiverAssigned;
    }

    address public owner;
    mapping(address => Caregiver) public caregivers;
    mapping(address => Elder) public elders;

    event CaregiverAssigned(address elder, address caregiver);

    modifier onlyOwner() {
        require(msg.sender == owner, "Only the owner can perform this action");
        _;
    }

    constructor() {
        owner = msg.sender;
    }

    // Function to add caregiver
    function addCaregiver(address _caregiverAddress, string memory _name, string memory _gender) public onlyOwner {
        caregivers[_caregiverAddress] = Caregiver({
            name: _name,
            gender: _gender,
            available: true
        });
    }

    // Function to assign caregiver to elder
    function assignCaregiver(address _elderAddress, address _caregiverAddress, string memory shiftStartTime, string memory shiftEndTime) public onlyOwner {
        require(caregivers[_caregiverAddress].available == true, "Caregiver is not available");

        // Assign caregiver to elder
        elders[_elderAddress] = Elder({
            name: "Elder Name",  // You can add this as an input parameter if needed
            gender: "Elder Gender",  // You can add this as an input parameter if needed
            shiftStartTime: shiftStartTime,
            shiftEndTime: shiftEndTime,
            caregiverAssigned: _caregiverAddress
        });

        // Mark caregiver as unavailable
        caregivers[_caregiverAddress].available = false;

        emit CaregiverAssigned(_elderAddress, _caregiverAddress);
    }

    // Function to mark caregiver as available again
    function markCaregiverAvailable(address _caregiverAddress) public onlyOwner {
        caregivers[_caregiverAddress].available = true;
    }

    // Get caregiver details
    function getCaregiverDetails(address _caregiverAddress) public view returns (string memory, string memory, bool) {
        Caregiver memory caregiver = caregivers[_caregiverAddress];
        return (caregiver.name, caregiver.gender, caregiver.available);
    }

    // Get elder details
    function getElderDetails(address _elderAddress) public view returns (string memory, string memory, string memory, string memory, address) {
        Elder memory elder = elders[_elderAddress];
        return (elder.name, elder.gender, elder.shiftStartTime, elder.shiftEndTime, elder.caregiverAssigned);
    }
	mapping(address => CaregiverAssignment) public assignments;

    function assignCaregiver(
        string memory _caregiverName,
        string memory _patientName,
        string memory _shiftStart,
        string memory _shiftEnd
    ) public {
        assignments[msg.sender] = CaregiverAssignment(_caregiverName, _patientName, _shiftStart, _shiftEnd);
    }
}
