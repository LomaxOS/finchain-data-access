package org.insurtech;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class EmployeesTest {

    private final Employees employee = new Employees("fs-empl001", "Beamery", "fs-empl001Loe4@","This is a sample phrase for hashing.");

    @Test
    public void testSerialize() {
        //This test returns a byte array
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("Employee ID", "fs-empl001");
        expectedJson.put("Name", "Beamery");
        expectedJson.put("Password",  "fs-empl001Loe4@");
        expectedJson.put("HashPhrase", "This is a sample phrase for hashing.");

        byte[] expectedBytes = expectedJson.toString().getBytes(UTF_8);

        byte[] actualBytes = employee.serialize();

        assertArrayEquals(expectedBytes, actualBytes, "The serialised byte array did not match the expected value.");
    }
}
