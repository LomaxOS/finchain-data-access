package org.insurtech;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class EmployeeTransferTest {

    @Test
    void employeeExistsTest() {
        EmployeeTransfer employeeTransfer = new EmployeeTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn(("{\"Name\":\"Beamery\",\"Password\":\"fs-empl001?=+0\",\"Phrase\":\"68d1209e09b0e208b839dfbea406768fa938349a\"}"));

        Throwable thrown = catchThrowable(() -> employeeTransfer.createEmployeeAccount(ctx, "fs-empl001", "Beamery", "fs-empl001?=+0","phrase"));

        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause().hasMessage("Employee fs-empl001 already exists");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("EMPLOYEE_ALREADY_EXISTS".getBytes());
    }

    @Test
    void employeeDoesNotExistTest() {
        EmployeeTransfer employeeTransfer = new EmployeeTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn("");

        ClientIdentity clientIdentity = mock(ClientIdentity.class);
        when(ctx.getClientIdentity()).thenReturn(clientIdentity);
        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(true);


        Employees employee = employeeTransfer.createEmployeeAccount(ctx, "fs-empl001", "Beamery", "fs-empl001?=+0","phrase");
        assertNotNull(employee);

    }
    @Test
    void employeeIsAuthorisedTest() {
        EmployeeTransfer employeeTransfer = new EmployeeTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        ClientIdentity clientIdentity = mock(ClientIdentity.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn("");
        when(ctx.getClientIdentity()).thenReturn(clientIdentity);
        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(true);


        Employees isAuthorisedToRegister = employeeTransfer.createEmployeeAccount(ctx, "fs-empl001", "Beamery", "fs-empl001?=+0","phrase");

        assertNotNull(isAuthorisedToRegister);

    }

    @Test
    void employeeIsNotAuthorisedTest() {
        EmployeeTransfer employeeTransfer = new EmployeeTransfer();
        Context ctx = mock(Context.class);
        ClientIdentity clientIdentity = mock(ClientIdentity.class);

        when(ctx.getClientIdentity()).thenReturn(clientIdentity);

        // return false for the active_employee attr
        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(false);

        boolean isAuthorised = employeeTransfer.authorisedEmployee(ctx);

        assertFalse(isAuthorised);
    }
    @Test
    void employeeCorrectPasswordTest() {
        EmployeeTransfer employeeTransfer = new EmployeeTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        ClientIdentity clientIdentity = mock(ClientIdentity.class);

        when(ctx.getClientIdentity()).thenReturn(clientIdentity);
        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(true);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn(("{\"Name\":\"Beamery\",\"Password\":\"fs-empl001?=+0\",\"Phrase\":\"68d1209e09b0e208b839dfbea406768fa938349a\"}"));

        boolean login = employeeTransfer.login(ctx, "fs-empl001", "fs-empl001?=+0");

        assertTrue(login);
    }


    @Test
    void employeeIncorrectPasswordTest() {
        EmployeeTransfer employeeTransfer = new EmployeeTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        ClientIdentity clientIdentity = mock(ClientIdentity.class);

        when(ctx.getClientIdentity()).thenReturn(clientIdentity);
        when(clientIdentity.assertAttributeValue("active_employee", "true")).thenReturn(true);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("fs-empl001")).thenReturn(("{\"Name\":\"Beamery\",\"Password\":\"fs-empl001?=+0\",\"Phrase\":\"68d1209e09b0e208b839dfbea406768fa938349a\"}"));

        Throwable thrown = catchThrowable(() -> {
            employeeTransfer.login(ctx, "fs-empl001", "password");
        });

        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause().hasMessage("Invalid Employee Password");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INVALID_EMPLOYEE_PASSWORD".getBytes());
    }

}
