#!/bin/bash
# set global variables
export PATH=${PWD}/../bin:$PATH
export FABRIC_CFG_PATH=$PWD/../config/
#Set org1 msp to Fabric CA client
export FABRIC_CA_CLIENT_HOME=${PWD}/organizations/peerOrganizations/org1.example.com


employeeID=${1:-"employee1"}

. scripts/utils.sh

# Check if no employeeID was provided or it's the default value
if [ -z "$employeeID" ]; then
    warnln "No employeeID provided. Please run the script with a valid employeeID."
else
    println "-----------Issuing Identity for Org1-------------"
    
    # Check if the employeeID is already registered
    if fabric-ca-client identity list --tls.certfiles "${PWD}/organizations/fabric-ca/org1/ca-cert.pem" | grep -q "$employeeID"; then
        warnln "Identity '$employeeID' is already registered."
    else
        registerEmployeeOrg1() {

            infoln "Registering $employeeID in Org1"
            fabric-ca-client register --id.name $employeeID --id.secret "${employeeID}pw" --id.type client --id.attrs "active_employee=true:ecert" --tls.certfiles "${PWD}/organizations/fabric-ca/org1/ca-cert.pem"


            successln "EmployeeID $employeeID registration in org1 is successful."

        }

        registerEmployeeOrg1

        enrollEmployeeOrg1() {

        infoln "Enrollement $employeeID in Org1"
        fabric-ca-client enroll -u https://$employeeID:"${employeeID}pw"@localhost:7054 --caname ca-org1 -M "${PWD}/organizations/peerOrganizations/org1.example.com/users/$employeeID@finchain.com/msp" --tls.certfiles "${PWD}/organizations/fabric-ca/org1/ca-cert.pem"
        successln "EmployeeID $employeeID enrollement in org1 is successful."

        infoln "Copying config file to $employeeID msp folder"
        cp "${PWD}/organizations/peerOrganizations/org1.example.com/msp/config.yaml" "${PWD}/organizations/peerOrganizations/org1.example.com/users/$employeeID@finchain.com.com/msp/config.yaml"

        println "--------------------------------------"
        }

        enrollEmployeeOrg1
    fi
 
fi