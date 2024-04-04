package org.insurtech;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import java.util.HashMap;
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
    private final String phrase;



    public Employees(String _employeeId, String _name, String _password, String _phrase) {
        this.employeeId = _employeeId;
        this.name = _name;
        this.password = _password;
        this.phrase = _phrase;
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

    public String getPhrase() {
        return phrase;
    }


    public byte[] serialize() {
        Map<String, Object> eMap = new HashMap<>();
        eMap.put("Employee ID", getEmployeeId());
        eMap.put("Name",  getName());
        eMap.put("Password", getPassword());
        eMap.put("HashPhrase",  getPhrase());

        String jsonString = new JSONObject(eMap).toString();

        return jsonString.getBytes(UTF_8);
    }
    public static Employees deserialize(final byte[] employeeJSON) {
        return deserialize(new String(employeeJSON, UTF_8));
    }

    public static Employees deserialize(final String employeeJSON) {

        JSONObject json = new JSONObject(employeeJSON);
        Map<String, Object> eMap = json.toMap();
        final String id = (String) eMap.get("Employee ID");
        final String name = (String) eMap.get("Name");
        final String password = (String) eMap.get("Password");
        final String phrase = (String) eMap.get("HashPhrase");

        return new Employees(id, name, password, phrase);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Employees other = (Employees) obj;

        return Objects.deepEquals(
                new String[]{getEmployeeId(), getName(),getPassword(), getPhrase()},
                new String[]{other.getEmployeeId(), other.getName(), other.getPassword(), other.getPhrase()});

    }

    @Override
    public int hashCode(){
        return Objects.hash(getEmployeeId(), getName(), getPassword(), getPhrase());
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
                + "[EmployeeID = " +getEmployeeId()+ ", name = "+getName() + ", password = "+getPassword()+", hash phrase = "+ getPhrase()+"]";
    }

}
