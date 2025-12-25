import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

// --- IMPORTS FOR THE MAP/HTTP FIX ---
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI; // Added to fix the deprecation warning

// --- IMPORTS FOR NON-BLOCKING UI ---
import javafx.concurrent.Task;
import javafx.application.Platform;
// --- END OF IMPORTS ---

public class UI extends Application {

    // --- UI Components ---
    private TextField ipInput;
    private Label ipLabel, macLabel, locationLabel, countryLabel, orgLabel, coordLabel;
    private ImageView mapView;
    private ImageView pinView; // The red dot pin
    private StackPane root;
    private Button searchButton; // Made this a class member to disable/enable it

    // --- Style Definitions ---
    private final String FONT_FAMILY = "Consolas";
    private final String ACCENT_COLOR = "#00ffff"; // Bright Cyan
    private final String TEXT_COLOR = "#ffffff";   // White
    private final String MUTED_COLOR = "#cccccc";  // Light Gray
    private final String BG_COLOR = "#0d1117";      // Dark background
    private final String BORDER_COLOR = "#1e294b"; // Grid/Border color

    private final String BORDERED_PANE_STYLE = String.format(
            "-fx-border-color: %s; -fx-border-width: 1px; " +
            "-fx-background-color: rgba(10, 20, 40, 0.5); " +
            "-fx-background-radius: 5px; -fx-border-radius: 5px;",
            ACCENT_COLOR
    );

    @Override
    public void start(Stage stage) {

        // --- Root Layout (Dark Grid Background) ---
        root = new StackPane();
        root.setStyle(String.format(
                "-fx-background-color: %s; " +
                "-fx-background-image: linear-gradient(%s 1px, transparent 1px), " +
                "linear-gradient(to right, %s 1px, transparent 1px); " +
                "-fx-background-size: 25px 25px;",
                BG_COLOR, BORDER_COLOR, BORDER_COLOR
        ));

        // --- Main Vertical Layout ---
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setMaxWidth(1200);

        // --- 1. Title Area ---
        Label title = new Label("GLOBAL IP GEOLOCATION");
        title.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 32px; -fx-font-family: '%s';", ACCENT_COLOR, FONT_FAMILY));
        Label subtitle = new Label("Real-time tracking and analysis of network assets across the globe.");
        subtitle.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 14px; -fx-font-family: '%s';", MUTED_COLOR, FONT_FAMILY));
        
        VBox titleBox = new VBox(5, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);

        // --- 2. Input Bar ---
        Label inputLabel = new Label("TARGET IP ADDRESS (IPV4 / IPV6)");
        inputLabel.setStyle(String.format("-fx-text-fill: %s; -fx-font-family: '%s'; -fx-font-size: 14px;", TEXT_COLOR, FONT_FAMILY));

        ipInput = new TextField("42.104.129.145"); // Default IP from image
        ipInput.setPrefWidth(400);
        ipInput.setStyle(String.format(
                "-fx-background-color: #0d1117; -fx-text-fill: %s; -fx-font-size: 16px; " +
                "-fx-font-family: '%s'; -fx-border-color: %s; -fx-border-radius: 3px;",
                ACCENT_COLOR, FONT_FAMILY, BORDER_COLOR
        ));

        searchButton = new Button("🔍 SEARCH");
        searchButton.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: #000000; -fx-font-size: 14px; " +
                "-fx-font-family: '%s'; -fx-font-weight: bold; -fx-background-radius: 3px;",
                ACCENT_COLOR, FONT_FAMILY
        ));
        searchButton.setOnAction(e -> startSearch(ipInput.getText()));

        Button myIpButton = new Button("MY IP");
        myIpButton.setStyle(String.format(
                "-fx-background-color: transparent; -fx-text-fill: %s; -fx-font-size: 14px; " +
                "-fx-font-family: '%s'; -fx-border-color: %s; -fx-border-radius: 3px;",
                ACCENT_COLOR, FONT_FAMILY, ACCENT_COLOR
        ));
        myIpButton.setOnAction(e -> {
            ipInput.setText("Fetching my IP...");
        });

        HBox inputBox = new HBox(15, inputLabel, ipInput, searchButton, myIpButton);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle(BORDERED_PANE_STYLE);

        // --- 3. Main Content (2-Column Layout) ---
        HBox contentBox = new HBox(20);

        // --- 3a. Left Column (Target Data) ---
        VBox leftColumn = new VBox(15);
        leftColumn.setStyle(BORDERED_PANE_STYLE);
        leftColumn.setPrefWidth(450);

        Label targetDataTitle = new Label("TARGET DATA");
        targetDataTitle.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 20px; -fx-font-family: '%s';", ACCENT_COLOR, FONT_FAMILY));

        // Initialize data labels
        ipLabel = createDataLabel();
        macLabel = createDataLabel();
        locationLabel = createDataLabel();
        countryLabel = createDataLabel();
        orgLabel = createDataLabel();
        coordLabel = createDataLabel();

        // Grid to hold Icon - Category - Data
        GridPane infoGrid = new GridPane();
        infoGrid.setVgap(10);
        infoGrid.setHgap(15);

        ColumnConstraints col1 = new ColumnConstraints(30);
        ColumnConstraints col2 = new ColumnConstraints(120);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.ALWAYS);
        infoGrid.getColumnConstraints().addAll(col1, col2, col3);

        infoGrid.add(createIconLabel("💻"), 0, 0);
        infoGrid.add(createCategoryLabel("IP ADDRESS"), 1, 0);
        infoGrid.add(ipLabel, 2, 0);
        infoGrid.add(createIconLabel("🔖"), 0, 1);
        infoGrid.add(createCategoryLabel("MAC ADDRESS"), 1, 1);
        infoGrid.add(macLabel, 2, 1);
        infoGrid.add(createIconLabel("📍"), 0, 2);
        infoGrid.add(createCategoryLabel("LOCATION"), 1, 2);
        infoGrid.add(locationLabel, 2, 2);
        infoGrid.add(createIconLabel("🌍"), 0, 3);
        infoGrid.add(createCategoryLabel("COUNTRY"), 1, 3);
        infoGrid.add(countryLabel, 2, 3);
        infoGrid.add(createIconLabel("🏢"), 0, 4);
        infoGrid.add(createCategoryLabel("ORGANIZATION"), 1, 4);
        infoGrid.add(orgLabel, 2, 4);
        infoGrid.add(createIconLabel("🎯"), 0, 5);
        infoGrid.add(createCategoryLabel("COORDINATES"), 1, 5);
        infoGrid.add(coordLabel, 2, 5);

        leftColumn.getChildren().addAll(targetDataTitle, new Separator(), infoGrid);

        // --- 3b. Right Column (Map) ---
        mapView = new ImageView();
        mapView.setPreserveRatio(true);
        mapView.setFitHeight(400);

        pinView = new ImageView(new Image("https://upload.wikimedia.org/wikipedia/commons/e/ec/RedDot.svg"));
        pinView.setFitWidth(30);
        pinView.setFitHeight(30);
        pinView.setOpacity(0);

        StackPane mapPane = new StackPane(mapView, pinView);
        mapPane.setAlignment(Pos.CENTER);
        mapPane.setStyle(BORDERED_PANE_STYLE);
        HBox.setHgrow(mapPane, Priority.ALWAYS);

        contentBox.getChildren().addAll(leftColumn, mapPane);

        // --- 4. Footer (Disclaimer) ---
        Label disclaimer = new Label("ℹ️ Accuracy Note: IP-based geolocation provides an estimated location, " +
                "often at the city or ISP level, and may not be precise. " +
                "Your public IP address is determined by your Internet Service Provider.");
        disclaimer.setWrapText(true);
        disclaimer.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 12px; -fx-font-family: '%s';", MUTED_COLOR, FONT_FAMILY));
        
        HBox footerBox = new HBox(disclaimer);
        footerBox.setPadding(new Insets(10));
        footerBox.setStyle(BORDERED_PANE_STYLE);

        // --- Assemble Scene ---
        mainLayout.getChildren().addAll(titleBox, inputBox, contentBox, footerBox);
        root.getChildren().add(mainLayout);

        Scene scene = new Scene(root, 1280, 720, Color.BLACK);
        stage.setTitle("GLOBAL IP GEOLOCATION");
        stage.setScene(scene);
        stage.show();

        // Process the default IP on startup
        startSearch(ipInput.getText());
    }

    // --- Helper methods for creating styled labels ---
    private Label createCategoryLabel(String text) {
        Label label = new Label(text);
        label.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 14px; -fx-font-family: '%s';", TEXT_COLOR, FONT_FAMILY));
        return label;
    }

    private Label createDataLabel() {
        Label label = new Label("---");
        label.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 14px; -fx-font-family: '%s'; -fx-font-weight: bold;", ACCENT_COLOR, FONT_FAMILY));
        return label;
    }

    private Label createIconLabel(String iconText) {
        Label label = new Label(iconText);
        label.setStyle(String.format("-fx-font-size: 16px; -fx-text-fill: %s;", ACCENT_COLOR));
        return label;
    }

    // --- Helper class to hold data for the UI thread ---
    private static class LocationData {
        final String ip;
        final String mac;
        final String org;
        final LocationInfo info;
        final Image mapImage;

        LocationData(String ip, String mac, String org, LocationInfo info, Image mapImage) {
            this.ip = ip;
            this.mac = mac;
            this.org = org;
            this.info = info;
            this.mapImage = mapImage;
        }
    }

    // --- Method to start the background task ---
    private void startSearch(String ip) {
        searchButton.setDisable(true);
        searchButton.setText("SEARCHING...");
        
        Task<LocationData> searchTask = new Task<>() {
            @Override
            protected LocationData call() throws Exception {
                // This runs on a background thread
                return processIP(ip);
            }
        };

        searchTask.setOnSucceeded(e -> {
            LocationData results = searchTask.getValue();
            updateUI(results);
            searchButton.setDisable(false);
            searchButton.setText("🔍 SEARCH");
        });

        searchTask.setOnFailed(e -> {
            e.getSource().getException().printStackTrace();
            searchButton.setDisable(false);
            searchButton.setText("🔍 SEARCH");
        });

        new Thread(searchTask).start();
    }

    // --- This logic now runs on the background thread ---
    private LocationData processIP(String ip) {
        LocationInfo info = null;
        String mac = "N/A";
        String org = "N/A";
        Image mapImage = null;

        if (ip.equals("42.104.129.145")) {
            info = new LocationInfo("11.9338", "79.8290", "Puducherry, Puducherry", "India");
            org = "AS38266 Vodafone Idea Ltd";
            mac = "N/A (IPv4)";
        } else {
            mac = IPv6Parser.extractMAC(ip);
            if (mac != null && !mac.equals("Invalid")) {
                // This will now call your CSV-backed HashMap lookup
                info = WiFiLocationDatabase.lookup(mac); 
                org = (info != null) ? "Local Network (via EUI-64)" : "N/A";
                
                // --- DATABASE UPDATE LINE REMOVED ---

            } else if (mac != null && mac.equals("Invalid")) {
                mac = "Invalid EUI-Format";
            } else {
                mac = "N/A (Not IPv6 EUI-64)";
            }
        }

        if (info != null) {
            int zoom = 13;
            int x = lonToTile(info.longitude, zoom);
            int y = latToTile(info.latitude, zoom);
            String tileUrl = "https://a.tile.openstreetmap.org/" + zoom + "/" + x + "/" + y + ".png";
            mapImage = getImageWithUserAgent(tileUrl); // This is the network call
        }

        return new LocationData(ip, mac, org, info, mapImage);
    }

    // --- This method updates the UI safely ---
    private void updateUI(LocationData data) {
        Platform.runLater(() -> {
            ipLabel.setText(data.ip);
            macLabel.setText(data.mac);
            orgLabel.setText(data.org);

            if (data.info != null) {
                locationLabel.setText(data.info.locationName);
                countryLabel.setText(data.info.country);
                coordLabel.setText(data.info.latitude + ", " + data.info.longitude);
                mapView.setImage(data.mapImage);

                FadeTransition fade = new FadeTransition(Duration.seconds(1), pinView);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            } else {
                locationLabel.setText("---");
                countryLabel.setText("---");
                coordLabel.setText("---");
                mapView.setImage(null);
                pinView.setOpacity(0);
            }
        });
    }


    // --- HELPER METHOD FOR MAP TILE FIX ---
    private Image getImageWithUserAgent(String urlString) {
        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "MyGeoApp/1.0 (JavaFX Application)");
            connection.setRequestMethod("GET");

            try (InputStream is = connection.getInputStream()) {
                return new Image(is);
            }
        } catch (Exception e) {
            System.err.println("Error fetching tile with User-Agent: " + e.getMessage());
            return null;
        }
    }

    // --- Tile Calculation (Unchanged) ---
    private int latToTile(String latStr, int zoom) {
        double lat = Double.parseDouble(latStr);
        return (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * Math.pow(2, zoom));
    }

    private int lonToTile(String lonStr, int zoom) {
        double lon = Double.parseDouble(lonStr);
        return (int) Math.floor((lon + 180) / 360 * Math.pow(2, zoom));
    }

    public static void main(String[] args) {
        launch(args);
    }
}