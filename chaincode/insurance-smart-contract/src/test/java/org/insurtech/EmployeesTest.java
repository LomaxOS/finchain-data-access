package org.insurtech;
import org.insurtech.model.Employees;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class EmployeesTest {


    @Test
    public void testSerialize() {
        Employees employee = new Employees("fs-empl001", "Beamery", "fs-empl001Loe4@","This is a sample phrase for hashing.");
        //This test returns a byte array
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("employeeId", "fs-empl001");
        expectedJson.put("name", "Beamery");
        expectedJson.put("password",  "fs-empl001Loe4@");
        expectedJson.put("recoveryPhrase", "This is a sample phrase for hashing.");

        byte[] expectedBytes = expectedJson.toString().getBytes(UTF_8);

        byte[] actualBytes = employee.serialize();

        assertArrayEquals(expectedBytes, actualBytes, "The serialised byte array did not match the expected value.");
    }
}
