import java.awt.Desktop;
import java.net.URI;

public class MapOpener {
    public static void openMap(String coord) {
        try {
            String url = "https://www.google.com/maps?q=" + coord;
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ignored) {}
    }
}