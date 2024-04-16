package org.insurtech.model;


import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@DataType
public class Documents {
    @Property
    private final String documentId;
    @Property
    private final String employeeId;

    @Property
    private final String documentName;


    public Documents(String documentId, String employeeId, String documentName) {
        this.employeeId = employeeId;
        this.documentName = documentName;
        this.documentId = documentId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public byte[] serialize() {
        Map<String, Object> dMap = new LinkedHashMap<>();
        dMap.put("documentId",  getDocumentId());
        dMap.put("employeeId", getEmployeeId());
        dMap.put("documentName",  getDocumentName());


        String jsonString = new JSONObject(dMap).toString();

        return jsonString.getBytes(UTF_8);
    }

    public static Documents deserialize(final byte[] documentJSON) {
        return deserialize(new String(documentJSON, UTF_8));
    }
     public static Documents deserialize(final String documentJSON) {
         JSONObject json = new JSONObject(documentJSON);
         Map<String, Object> eMap = json.toMap();

         final String id = (String) eMap.get("documentId");
         final String employeeId = (String) eMap.get("employeeId");
         final String name = (String) eMap.get("documentName");
         return new Documents(id, employeeId, name);
     }
}
