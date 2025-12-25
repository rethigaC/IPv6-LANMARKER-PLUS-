import java.util.Map;

public class CoordinateFilter {
    public static String filterBestCoordinate(Map<String, String> bssidCoords) {
        return bssidCoords.values().stream().findFirst().orElse(null);
    }
}