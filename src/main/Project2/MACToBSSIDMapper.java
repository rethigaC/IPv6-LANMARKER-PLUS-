import java.util.*;

public class MACToBSSIDMapper {
    public static List<String> generateBSSIDs(String mac) {
        List<String> bssids = new ArrayList<>();
        bssids.add(mac); // Add original MAC
        // Add variations (e.g., last byte ±1)
        String[] parts = mac.split(":");
        if (parts.length == 6) {
            try {
                int last = Integer.parseInt(parts[5], 16);
                bssids.add(parts[0] + ":" + parts[1] + ":" + parts[2] + ":" + parts[3] + ":" + parts[4] + ":" + String.format("%02x", (last + 1) & 0xFF));
                bssids.add(parts[0] + ":" + parts[1] + ":" + parts[2] + ":" + parts[3] + ":" + parts[4] + ":" + String.format("%02x", (last - 1) & 0xFF));
            } catch (Exception ignored) {}
        }
        return bssids;
    }
}