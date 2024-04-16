package org.insurtech;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.insurtech.contracts.ContractUtility;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class ContractTests {

    @Test
    void employeeExistsTest() {

        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn(("{\"employeeId\":\"fs-empl001\",\"name\":\"Beamery\",\"password\":\"fs-empl001?=+0\",\"recoveryPhrase\":\"68d1209e09b0e208b839dfbea406768fa938349a\"}"));

        assertTrue(ContractUtility.isEmployeeExist(ctx, "fs-empl001"));
    }

    @Test
    void employeeDoesNotExistTest() {
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn("");

        assertFalse(ContractUtility.isEmployeeExist(ctx, "fs-empl001"));

    }
    @Test
    void employeeIsAuthorisedTest() {
        Context ctx = mock(Context.class);
        ClientIdentity clientIdentity = mock(ClientIdentity.class);

        when(ctx.getClientIdentity()).thenReturn(clientIdentity);
        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(true);

        assertTrue(ContractUtility.isIdentityPermit(ctx));

    }

    @Test
    void employeeIsNotAuthorisedTest() {
        Context ctx = mock(Context.class);
        ClientIdentity clientIdentity = mock(ClientIdentity.class);

        when(ctx.getClientIdentity()).thenReturn(clientIdentity);

        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(false);

        assertFalse(ContractUtility.isIdentityPermit(ctx));
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
