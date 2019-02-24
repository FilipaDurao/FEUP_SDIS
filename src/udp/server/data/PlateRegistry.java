package udp.server.data;

import java.util.HashMap;

public class PlateRegistry {
    private static PlateRegistry ourInstance = new PlateRegistry();

    public static PlateRegistry getInstance() {
        return ourInstance;
    }

    private HashMap<String, String> plateRegistry;

    private PlateRegistry() {
        this.plateRegistry = new HashMap<>();
    }


    public String getOwner(String plateNumber) {
        return plateRegistry.getOrDefault(plateNumber, "-1");
    }

    public String addRegistry(String plateNumber, String owner) {
        if (plateRegistry.containsKey(plateNumber)) {
            return "-1";
        } else {
            plateRegistry.put(plateNumber, owner);
            return String.valueOf(plateRegistry.size());
        }
    }
}
