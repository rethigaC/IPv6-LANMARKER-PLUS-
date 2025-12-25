import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WiFiLocationDatabase {
    // This map will hold all your data from the CSV file
    private static final Map<String, LocationInfo> macToLocation = new HashMap<>();

    // This static block runs once when the class is loaded
    static {
        // Change this to the correct path if your file is not in the 'src' directory
        String csvFile = "mac_locations.csv"; 
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1); // Split by comma
                if (parts.length < 5) continue; // Skip bad lines

                String mac = parts[0].toLowerCase().trim();
                String lat = parts[1].trim();
                String lng = parts[2].trim();
                String name = parts[3].trim();
                String country = parts[4].trim();

                // Add the data to the map
                macToLocation.put(mac, new LocationInfo(lat, lng, name, country));
            }
            System.out.println("Loaded " + macToLocation.size() + " landmarks from " + csvFile);
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Could not load mac_locations.csv: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Looks up a MAC address from the in-memory HashMap.
     *
     * @param mac The MAC address to search for.
     * @return A LocationInfo object if found, otherwise null.
     */
    public static LocationInfo lookup(String mac) {
        if (mac == null) {
            return null;
        }
        // This is a very fast lookup from the map
        return macToLocation.get(mac.toLowerCase());
    }
}