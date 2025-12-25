public class LandmarkEvaluator {
    public static boolean isReliable(LocationInfo info) {
        return info != null && !info.latitude.equals("0.0") && !info.longitude.equals("0.0");
    }
}