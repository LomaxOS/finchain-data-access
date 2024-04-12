package org.insurtech.contracts;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.insurtech.exceptions.ContractException;
import org.insurtech.model.Employees;

@Contract(
        name = "insurance-smart-contract",
        info = @Info(
                title = "Finchain Contract Utility",
                description = "Finchain Smart Contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "MTU Kerry",
                        url = "https://LomaxOsomba/fyp")))

public class ContractUtility {

    //Check if the requester is authorized to perform the operation
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public static void unauthorisedIfNoPermits(Context ctx) {
        String errorMessage;
        if (!isIdentityPermit(ctx)) {
            errorMessage = "No authorised request";
            throw new ChaincodeException(errorMessage, ContractException.NO_AUTHORISED_REQUEST.toString());
        }
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public static boolean isIdentityPermit(Context ctx) {
        ClientIdentity identity = ctx.getClientIdentity();

        return identity.assertAttributeValue("active_employee", "true");
    }
    //Check if the requester exists
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public static void checkIfAlreadyExists(Context ctx, String employeeID) {

        if (isEmployeeExist(ctx, employeeID)) {
            String errorMessage = String.format("Employee %s already exists", employeeID);
            throw new ChaincodeException(errorMessage, ContractException.EMPLOYEE_ALREADY_EXISTS.toString());
        }
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public static boolean isEmployeeExist(Context ctx, String employeeID) {
        ChaincodeStub stub = ctx.getStub();
        String employeeJSON = stub.getStringState(employeeID);

        return employeeJSON != null && !employeeJSON.isEmpty();
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public static void checkIfPasswordIsValid(Context ctx, String employeeID, String password) {

        ChaincodeStub stub = ctx.getStub();
        byte[] result = stub.getState(employeeID);

        Employees employee = Employees.deserialize(result);
        String employeePassword = employee.getPassword();

        if (!employeePassword.equals(password)) {
            String errorMessage = "Invalid Employee Password";
            throw new ChaincodeException(errorMessage, ContractException.INVALID_EMPLOYEE_PASSWORD.toString());
        }
    }

}
