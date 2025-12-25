public class PrefixRotationUpdater {
    public static String updatePrefix(String ipv6) {
        String[] parts = ipv6.split(":");
        if (parts.length < 2) return ipv6;
        parts[0] = "2001";
        parts[1] = "abcd";
        return String.join(":", parts);
    }
}