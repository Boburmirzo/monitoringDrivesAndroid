
package ru.likada.monitoringdriver.modeTest;

import java.util.HashMap;
import java.util.Map;

public class DESTINATION {

    private String iD;
    private String pOINTTYPE;
    private String oBJECT;
    private Object aDDRESS;
    private Object cONTACT;
    private String cOORDINATES;
    private Object dESCRIPTION;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getPOINTTYPE() {
        return pOINTTYPE;
    }

    public void setPOINTTYPE(String pOINTTYPE) {
        this.pOINTTYPE = pOINTTYPE;
    }

    public String getOBJECT() {
        return oBJECT;
    }

    public void setOBJECT(String oBJECT) {
        this.oBJECT = oBJECT;
    }

    public Object getADDRESS() {
        return aDDRESS;
    }

    public void setADDRESS(Object aDDRESS) {
        this.aDDRESS = aDDRESS;
    }

    public Object getCONTACT() {
        return cONTACT;
    }

    public void setCONTACT(Object cONTACT) {
        this.cONTACT = cONTACT;
    }

    public String getCOORDINATES() {
        return cOORDINATES;
    }

    public void setCOORDINATES(String cOORDINATES) {
        this.cOORDINATES = cOORDINATES;
    }

    public Object getDESCRIPTION() {
        return dESCRIPTION;
    }

    public void setDESCRIPTION(Object dESCRIPTION) {
        this.dESCRIPTION = dESCRIPTION;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
