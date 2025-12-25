import java.util.*;

public class IPv6Parser {
    public static String extractMAC(String ipv6) {
        String[] rawParts = ipv6.split(":");
        List<String> parts = new ArrayList<>();
        for (String p : rawParts) {
            if (!p.isEmpty()) parts.add(p);
        }

        if (parts.size() < 4) return "Invalid";

        try {
            String s5 = parts.get(parts.size() - 4);
            String s6 = parts.get(parts.size() - 3);
            String s7 = parts.get(parts.size() - 2);
            String s8 = parts.get(parts.size() - 1);

            String eui64 = s5 + s6 + s7 + s8;
            if (!eui64.contains("fffe") || eui64.length() != 16) return "Invalid";

            String macHex = eui64.substring(0, 6) + eui64.substring(10);
            int firstByte = Integer.parseInt(macHex.substring(0, 2), 16);
            firstByte ^= 0x02;
            String flipped = String.format("%02x", firstByte);

            return (flipped + macHex.substring(2))
                .replaceAll("(.{2})(?!$)", "$1:")
                .toLowerCase();
        } catch (Exception e) {
            return "Invalid";
        }
    }
}