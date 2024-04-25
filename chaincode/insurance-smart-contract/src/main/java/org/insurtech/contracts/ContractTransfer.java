package org.insurtech.contracts;

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
import org.insurtech.exceptions.ContractException;
import org.insurtech.model.Documents;
import org.insurtech.model.Employees;


@Contract (
        name = "insurance-smart-contract",
        info = @Info(
                title = "Finchain Employee Accounts",
                description = "Finchain Employee Account Smart Contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "MTU Kerry",
                        url = "https://LomaxOsomba/fyp")))

@Default
public final class ContractTransfer implements ContractInterface {

    public ContractTransfer(){}

    //Change the state of the ledger by recording employee details and return the transaction ID
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String createEmployeeAccount(final Context ctx, final String employeeID, final String name,
                                        final String password, final String phrase) {

        Employees employee = new Employees(employeeID, name, password, phrase);

        byte[] employeeDetailAsByte = employee.serialize();

        ctx.getStub().putState(employeeID, employeeDetailAsByte);

        ctx.getStub().setEvent("Account created", employeeDetailAsByte);

        return ctx.getStub().getTxId();
    }
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean uploadDocument(Context ctx, String documentId, String employeeId, String documentName) {

        ChaincodeStub stub = ctx.getStub();
        boolean isAllowed = false;

        Documents document = new Documents(documentId, employeeId, documentName);

        byte[] documentDetailAsByte = document.serialize();

        stub.putState(documentId, documentDetailAsByte);

        stub.setEvent("File Uploaded", documentDetailAsByte);

        return isAllowed;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public void unauthorisedIfNoPermits(Context ctx) {
        String errorMessage;
        if (!isIdentityPermit(ctx)) {
            errorMessage = "No authorised request";
            throw new ChaincodeException(errorMessage, ContractException.NO_AUTHORISED_REQUEST.toString());
        }
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isIdentityPermit(Context ctx) {
        ClientIdentity identity = ctx.getClientIdentity();

        return identity.assertAttributeValue("active_employee", "true");
    }
    //Check if the requester exists
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public void checkIfAlreadyExists(Context ctx, String employeeID) {

        if (isEmployeeExist(ctx, employeeID)) {
            String errorMessage = String.format("Employee %s already exists", employeeID);
            throw new ChaincodeException(errorMessage, ContractException.EMPLOYEE_ALREADY_EXISTS.toString());
        }
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isEmployeeExist(Context ctx, String employeeID) {
        ChaincodeStub stub = ctx.getStub();
        String employeeJSON = stub.getStringState(employeeID);

        return employeeJSON != null && !employeeJSON.isEmpty();
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public void checkIfPasswordIsValid(Context ctx, String employeeID, String password) {

        ChaincodeStub stub = ctx.getStub();

        String result = stub.getStringState(employeeID);

        Employees employee = Employees.deserialize(result);

        String employeePassword = employee.getPassword();

        if (!employeePassword.equals(password)) {
            String errorMessage = "Invalid Employee Password";
            throw new ChaincodeException(errorMessage, ContractException.INVALID_EMPLOYEE_PASSWORD.toString());
        }
    }


}
