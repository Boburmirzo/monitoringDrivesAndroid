package ru.likada.monitoringdriver.rest_api;

/**
 * Created by andrej on 19.03.17.
 */

public class ServerConfig {
    //    String SERVER_CONFIG = "http://erp.likada.pro/PetrolCRMSystem_test/rest/";
    //http://erp.likada.pro/PetrolCRMSystem_test/rest/login?username=test&password=2256565
    public static final String SERVER_CONFIG = ":8080/PetrolCRMSystem/rest/";
    public static final String HTTP="http://";
    public static String IP_ADDRESS;

    public static String getIpAddress() {
        return IP_ADDRESS;
    }

    public static void setIpAddress(String ipAddress) {
        IP_ADDRESS = ipAddress;
    }
}
