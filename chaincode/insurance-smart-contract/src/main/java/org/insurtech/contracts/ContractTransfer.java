package org.insurtech.contracts;

import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
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
    //Register employee and return transaction id when successful
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String createEmployeeAccount(final Context ctx, final String employeeID, final String name, final String password, final String phrase) {

        Employees employee = new Employees(employeeID, name, password, phrase);

        byte[] employeeDetailAsByte = employee.serialize();

        ctx.getStub().putState(employeeID, employeeDetailAsByte);

        ctx.getStub().setEvent("Account created", employeeDetailAsByte);

        return ctx.getStub().getTxId();
    }

    //Upload Documents and return its transaction id when successful
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String uploadDocument(Context ctx, String employeeId, String documentName, String documentHash){

        Documents document = new Documents(employeeId, documentName, documentHash);

        byte[] documentAsByte = document.serialize();

        ctx.getStub().putState(employeeId, documentAsByte);

        ctx.getStub().setEvent("Document stored", documentAsByte);

        return ctx.getStub().getTxId();
    }

}
