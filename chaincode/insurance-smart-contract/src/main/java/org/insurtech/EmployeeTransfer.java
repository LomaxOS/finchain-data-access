package org.insurtech;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.security.MessageDigest;


@Contract (
        name = "insurance-smart-contract",
        info = @Info(
                title = "Finchain Employee Transfer",
                description = "Finchain Smart Contract for Employee Transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "MTU Kerry",
                        url = "https://github.com/LomaxOS00243/fyp")))

@Default
public final class EmployeeTransfer implements ContractInterface {

    public EmployeeTransfer(){}
    private enum EmployeeTransferErrors {
        EMPLOYEE_ALREADY_EXISTS,
        EMPTY_ID_INPUT,
        NO_AUTHORISED_REQUEST,

        INVALID_EMPLOYEE_PASSWORD
    }


    //Registration
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Employees createEmployeeAccount(final Context ctx, final String employeeID, final String name, final String password, final String phrase) {

        ChaincodeStub stub = ctx.getStub();
        Employees employee;
        String employeeDigestedPhrase;
        byte[] employeeInfoAsByte;

        String errorMessage;
        
        if (employeeExists(ctx, employeeID)) {
            errorMessage = String.format("Employee %s already exists", employeeID);
            throw new ChaincodeException(errorMessage, EmployeeTransferErrors.EMPLOYEE_ALREADY_EXISTS.toString());
        }
        checkAuthorisation(ctx);

        employeeDigestedPhrase = digestPhrase(phrase);

        employee = new Employees(employeeID, name, password, employeeDigestedPhrase);

        employeeInfoAsByte = employee.serialize();

        stub.putState(employeeID, employeeInfoAsByte);

        stub.setEvent("Employee created", employeeInfoAsByte);

        return employee;
    }

    //login
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean login(final Context ctx, final String employeeID, final String password) {

        checkAuthorisation(ctx);

        checkEmployeePasswordValidity(ctx, employeeID, password);

        return true;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean employeeExists(final Context ctx, final String employeeID) {
        ChaincodeStub stub = ctx.getStub();
        String employeeJSON = stub.getStringState(employeeID);

        return (employeeJSON != null && !employeeJSON.isEmpty());
    }
    private void checkAuthorisation(Context ctx) {
        String errorMessage;
        if (!authorisedEmployee(ctx)) {
            errorMessage = "No authorised request";
            throw new ChaincodeException(errorMessage, EmployeeTransferErrors.NO_AUTHORISED_REQUEST.toString());
        }
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean authorisedEmployee(Context ctx) {
        ClientIdentity identity = ctx.getClientIdentity();

        return identity.assertAttributeValue("active_employee", "true");

    }
    private String digestPhrase(String phrase) {
        byte[] phraseAsByte = phrase.getBytes();
        String digestedPhraseHex;
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] digest = messageDigest.digest(phraseAsByte);

            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            digestedPhraseHex = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return digestedPhraseHex;
    }


    private void checkEmployeePasswordValidity(Context ctx, String employeeID, String password) {

        ChaincodeStub stub = ctx.getStub();
        String result = stub.getStringState(employeeID);

        Employees employee = Employees.deserialize(result);
        String employeePassword = employee.getPassword();
        String errorMessage;
        if (!employeePassword.equals(password)) {
            errorMessage = "Invalid Employee Password";
            throw new ChaincodeException(errorMessage, EmployeeTransferErrors.INVALID_EMPLOYEE_PASSWORD.toString());
        }
    }

}
