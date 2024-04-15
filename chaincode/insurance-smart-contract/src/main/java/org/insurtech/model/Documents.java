package org.insurtech.model;


import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@DataType
public class Documents {

    @Property
    private final String employeeId;

    @Property
    private final String documentName;

    private final String documentHashValue;

    public Documents(String employeeId, String documentName, String documentHashValue) {
        this.employeeId = employeeId;
        this.documentName = documentName;
        this.documentHashValue = documentHashValue;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocumentHashValue() {
        return documentHashValue;
    }

    public byte[] serialize() {
        Map<String, Object> eMap = new HashMap<>();
        eMap.put("employeeId", getEmployeeId());
        eMap.put("documentName",  getDocumentName());
        eMap.put("documentHashValue",  getDocumentHashValue());

        String jsonString = new JSONObject(eMap).toString();

        return jsonString.getBytes(UTF_8);
    }
}
