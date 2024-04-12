package org.insurtech;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.insurtech.contracts.ContractTransfer;
import org.insurtech.contracts.ContractUtility;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class ContractsTest {

    @Test
    void employeeExistsTest() {

        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn(("{\"employeeId\":\"fs-empl001\",\"name\":\"Beamery\",\"password\":\"fs-empl001?=+0\",\"recoveryPhrase\":\"68d1209e09b0e208b839dfbea406768fa938349a\"}"));

        assertThrows(ChaincodeException.class, () -> {
            ContractUtility.checkIfAlreadyExists(ctx, "fs-empl001");
        });
    }

    @Test
    void employeeDoesNotExistTest() {
        ContractTransfer contractTransfer = new ContractTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn("");

        ClientIdentity clientIdentity = mock(ClientIdentity.class);
        when(ctx.getClientIdentity()).thenReturn(clientIdentity);
        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(true);


        String employee = contractTransfer.createEmployeeAccount(ctx, "fs-empl001", "Beamery", "fs-empl001?=+0","phrase");
        assertNotNull(employee);

    }
    @Test
    void employeeIsAuthorisedTest() {
        ContractTransfer contractTransfer = new ContractTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        ClientIdentity clientIdentity = mock(ClientIdentity.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn("");
        when(ctx.getClientIdentity()).thenReturn(clientIdentity);
        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(true);


        String isAuthorisedToRegister = contractTransfer.createEmployeeAccount(ctx, "fs-empl001", "Beamery", "fs-empl001?=+0","phrase");

        assertNotNull(isAuthorisedToRegister);

    }

    @Test
    void employeeIsNotAuthorisedTest() {
        Context ctx = mock(Context.class);
        ClientIdentity clientIdentity = mock(ClientIdentity.class);

        when(ctx.getClientIdentity()).thenReturn(clientIdentity);

        // return false for the active_employee attr
        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(false);

        boolean isAuthorised = ContractUtility.isIdentityPermit(ctx);

        assertFalse(isAuthorised);
    }
    @Test
    void employeeCorrectPasswordTest() {
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getState("fs-empl001")).thenReturn(("{\"employeeId\":\"fs-empl001\",\"name\":\"Beamery\",\"password\":\"fs-empl001?=+0\",\"recoveryPhrase\":\"68d1209e09b0e208b839dfbea406768fa938349a\"}").getBytes());


        assertDoesNotThrow(() -> {
            ContractUtility.checkIfPasswordIsValid(ctx, "fs-empl001", "fs-empl001?=+0");
        });
    }


    @Test
    void employeeIncorrectPasswordTest() {
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getState("fs-empl001")).thenReturn(("{\"employeeId\":\"fs-empl001\",\"name\":\"Beamery\",\"password\":\"fs-empl001?=+0\",\"recoveryPhrase\":\"68d1209e09b0e208b839dfbea406768fa938349a\"}").getBytes());

        assertThrows(ChaincodeException.class, () -> {
            ContractUtility.checkIfPasswordIsValid(ctx, "fs-empl001", "wrongpassword");
        });
    }

}
