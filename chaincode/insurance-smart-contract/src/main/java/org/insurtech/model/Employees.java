package org.insurtech.model;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import static java.nio.charset.StandardCharsets.UTF_8;


@DataType
public class Employees {
    @Property
    private final String employeeId;

    @Property
    private final String name;

    @Property
    private final String password;

    @Property
    private final String recoveryPhrase;

    public Employees(String _employeeId, String _name, String _password, String _phrase) {
        this.employeeId = _employeeId;
        this.name = _name;
        this.password = _password;
        this.recoveryPhrase = _phrase;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRecoveryPhrase() {
        return recoveryPhrase;
    }

    public byte[] serialize() {
        Map<String, Object> eMap = new LinkedHashMap<>();
        eMap.put("employeeId", getEmployeeId());
        eMap.put("name",  getName());
        eMap.put("password", getPassword());
        eMap.put("recoveryPhrase",  getRecoveryPhrase());

        String jsonString = new JSONObject(eMap).toString();

        return jsonString.getBytes(UTF_8);
    }

    public static Employees deserialize(final String employeeJSON) {

        JSONObject json = new JSONObject(employeeJSON);
        Map<String, Object> eMap = json.toMap();
        final String id = (String) eMap.get("employeeId");
        final String name = (String) eMap.get("name");
        final String password = (String) eMap.get("password");
        final String phrase = (String) eMap.get("recoveryPhrase");

        return new Employees(id, name, password, phrase);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employees)) return false;
        Employees employees = (Employees) o;
        return Objects.equals(employeeId, employees.employeeId) && Objects.equals(name, employees.name) && Objects.equals(password, employees.password) && Objects.equals(recoveryPhrase, employees.recoveryPhrase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, name, password, recoveryPhrase);
    }

    @Override
    public String toString() {
        return "Employees{" +
                "employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", phrase='" + recoveryPhrase + '\'' +
                '}';
    }
}
