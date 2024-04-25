package org.insurtech;
import org.insurtech.model.Documents;
import org.insurtech.model.Employees;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelTests {


    @Test
    public void testEmployeeAccountSerializer() {
        Employees employee = new Employees("fs-empl001", "Beamery", "fs-empl001Loe4@", "This is a sample phrase for hashing.");

        JSONObject expectedJson = new JSONObject();
        expectedJson.put("employeeId", "fs-empl001");
        expectedJson.put("name", "Beamery");
        expectedJson.put("password", "fs-empl001Loe4@");
        expectedJson.put("recoveryPhrase", "This is a sample phrase for hashing.");

        byte[] actualBytes = employee.serialize();
        String actualJsonString = new String(actualBytes, StandardCharsets.UTF_8);
        JSONObject actualJson = new JSONObject(actualJsonString);

        assertEquals(expectedJson.toString(), actualJson.toString(), "The serialized JSON string did not match the expected value.");

    }
    @Test
    public void testEmployeeAccountDeserializer() {
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("employeeId", "fs-empl001");
        expectedJson.put("name", "Beamery");
        expectedJson.put("password", "fs-empl001Loe4@");
        expectedJson.put("recoveryPhrase", "This is a sample phrase for hashing.");

        String expectedBytes = expectedJson.toString();

        Employees actualEmployee = Employees.deserialize(expectedBytes);

        assertEquals("fs-empl001", actualEmployee.getEmployeeId(), "The employeeId did not match the expected value.");
        assertEquals("Beamery", actualEmployee.getName(), "The name did not match the expected value.");
        assertEquals("fs-empl001Loe4@", actualEmployee.getPassword(), "The password did not match the expected value.");
        assertEquals("This is a sample phrase for hashing.", actualEmployee.getRecoveryPhrase(), "The recovery phrase did not match the expected value.");
    }
    @Test
    public void testDocumentSerializer() {
        Documents doc = new Documents("68d1209e09b0e208b839dfbea406768fa938349a", "fs-empl001", "fypIsAwesome.pdf");

        JSONObject expectedJson = new JSONObject();
        expectedJson.put("documentId", "68d1209e09b0e208b839dfbea406768fa938349a");
        expectedJson.put("employeeId", "fs-empl001");
        expectedJson.put("documentName", "fypIsAwesome.pdf");

        byte[] actualBytes = doc.serialize();

        String actualJsonString = new String(actualBytes, StandardCharsets.UTF_8);
        JSONObject actualJson = new JSONObject(actualJsonString);

        assertEquals(expectedJson.toString(), actualJson.toString(), "The serialized JSON string did not match the expected value.");
    }
    @Test
    public void testDocumentDeserializer() {
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("documentId", "68d1209e09b0e208b839dfbea406768fa938349a");
        expectedJson.put("employeeId", "fs-empl001");
        expectedJson.put("documentName", "fypIsAwesome.pdf");

        String expectedBytes = expectedJson.toString();

        Documents actualDoc = Documents.deserialize(expectedBytes);

        assertEquals("68d1209e09b0e208b839dfbea406768fa938349a", actualDoc.getDocumentId(), "The documentId did not match the expected value.");
        assertEquals("fs-empl001", actualDoc.getEmployeeId(), "The employeeId did not match the expected value.");
        assertEquals("fypIsAwesome.pdf", actualDoc.getDocumentName(), "The documentName did not match the expected value.");
    }
}
