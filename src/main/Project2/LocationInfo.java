public class LocationInfo {
    public final String latitude;
    public final String longitude;
    public final String locationName;
    public final String country;

    public LocationInfo(String lat, String lng, String name, String country) {
        this.latitude = lat;
        this.longitude = lng;
        this.locationName = name;
        this.country = country;
    }
}