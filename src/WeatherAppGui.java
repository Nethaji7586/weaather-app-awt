import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

public class WeatherAppGui extends JFrame {
    private static final String CLOUDY_IMAGE_PATH = "src/assets/cloudy.gif";
    private static final String ERROR_IMAGE_PATH = "src/assets/error.gif";
    private static final String SEARCH_ICON_PATH = "src/assets/search.png";
    private static final String HUMIDITY_ICON_PATH = "src/assets/humidity.png";
    private static final String WINDSPEED_ICON_PATH = "src/assets/windspeed.png";
    private static final String CLEAR_IMAGE_PATH = "src/assets/clear.gif";
    private static final String RAIN_IMAGE_PATH = "src/assets/rain.gif";
    private static final String SNOW_IMAGE_PATH = "src/assets/snow.gif";
    private static final String BACKGROUND_IMAGE_PATH = "src/assets/background.jpg";
    
    private JSONObject weatherData;
    private Font poppinsFont;

    public WeatherAppGui() {
        super("Weather App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 182, 193));  // Light rose background
        setSize(500, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        
        // Load Poppins Font
        loadFonts();

        addNavBar();
        addMainContent();
        addFooter();
    }

    private void loadFonts() {
        try {
            // Load Poppins font from file
            FileInputStream fontStream = new FileInputStream("src/assets/fonts/Poppins-Black.ttf");
            poppinsFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(24f); // Size 24
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }
    private void addNavBar() {
        JPanel navBar = new JPanel();
        navBar.setBackground(new Color(121, 166, 210));  // Blue background (Bootstrap primary color)
        navBar.setPreferredSize(new Dimension(getWidth(), 60));
        navBar.setLayout(new FlowLayout(FlowLayout.CENTER));
    
        JLabel titleLabel = new JLabel("Weather App");
        titleLabel.setFont(poppinsFont);  // Apply Poppins font
        titleLabel.setForeground(new Color(0,0,0));  // Bootstrap primary color for text
        navBar.add(titleLabel);
    
        add(navBar, BorderLayout.NORTH);
    }
    
    private void addMainContent() {
        BackgroundPanel mainPanel = new BackgroundPanel(BACKGROUND_IMAGE_PATH);
        mainPanel.setLayout(null);

        JTextField searchTextField = createTextField(20, 20, 380, 50);
        mainPanel.add(searchTextField);

        JLabel weatherConditionImage = new JLabel(loadImage(CLOUDY_IMAGE_PATH, 450, 250));
        weatherConditionImage.setBounds(25, 100, 450, 250);
        mainPanel.add(weatherConditionImage);

        JLabel temperatureText = createLabel("10 C", 0, 370, 500, 50, 48, SwingConstants.CENTER, new Color(255, 69, 0));  // Orange for contrast
        mainPanel.add(temperatureText);

        JLabel weatherConditionDesc = createLabel("Cloudy", 0, 430, 500, 40, 28, SwingConstants.CENTER, new Color(105, 105, 105));  // Dark gray text
        mainPanel.add(weatherConditionDesc);

        JLabel humidityImage = new JLabel(loadImage(HUMIDITY_ICON_PATH, 50, 50));
        humidityImage.setBounds(50, 500, 50, 50);
        mainPanel.add(humidityImage);

        JLabel humidityText = createLabel("<html><b>Humidity:</b> 100%</html>", 110, 500, 150, 50, 18, SwingConstants.LEFT, new Color(105, 105, 105));  // Dark gray text
        mainPanel.add(humidityText);

        JLabel windspeedImage = new JLabel(loadImage(WINDSPEED_ICON_PATH, 50, 50));
        windspeedImage.setBounds(280, 500, 50, 50);
        mainPanel.add(windspeedImage);

        JLabel windspeedText = createLabel("<html><b>Windspeed:</b> 15km/h</html>", 340, 500, 150, 50, 18, SwingConstants.LEFT, new Color(105, 105, 105));  // Dark gray text
        mainPanel.add(windspeedText);

        JButton searchButton = createSearchButton(searchTextField, weatherConditionImage, temperatureText, weatherConditionDesc, humidityText, windspeedText);
        mainPanel.add(searchButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(255, 105, 180));  // Rose color
        footer.setPreferredSize(new Dimension(getWidth(), 40));

        JLabel footerLabel = new JLabel("Project by Nethaji");
        footerLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        footerLabel.setForeground(Color.WHITE);
        footer.add(footerLabel);

        add(footer, BorderLayout.SOUTH);
    }

    private JTextField createTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setFont(poppinsFont);  // Use Poppins font
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 2));  // Rose color border
        return textField;
    }

    private JLabel createLabel(String text, int x, int y, int width, int height, int fontSize, int alignment, Color color) {
        JLabel label = new JLabel(text, alignment);
        label.setBounds(x, y, width, height);
        label.setFont(poppinsFont.deriveFont((float) fontSize));  // Use Poppins font with adjusted size
        label.setForeground(color);
        return label;
    }

    private JButton createSearchButton(JTextField searchTextField, JLabel weatherConditionImage, JLabel temperatureText, JLabel weatherConditionDesc, JLabel humidityText, JLabel windspeedText) {
        JButton searchButton = new JButton();
        ImageIcon searchIcon = new ImageIcon(SEARCH_ICON_PATH);
        ImageIcon scaledIcon = new ImageIcon(searchIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        searchButton.setIcon(scaledIcon);
        searchButton.setBounds(410, 20, 50, 50);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Set the background color to gray
        searchButton.setBackground(new Color(0, 85, 255));  // Gray background
        
        // Set the border color to black with slim thickness
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(0,0,0), 2));  // Slim black border
    
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(true); 

        searchButton.addActionListener(e -> {
            String userInput = searchTextField.getText().trim();
            if (userInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a location.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new SwingWorker<JSONObject, Void>() {
                @Override
                protected JSONObject doInBackground() {
                    return WeatherApp.getWeatherData(userInput);
                }

                @Override
                protected void done() {
                    try {
                        weatherData = get();
                        if (weatherData == null) {
                            updateUIForError(weatherConditionImage, temperatureText, weatherConditionDesc, humidityText, windspeedText);
                        } else {
                            updateUIForWeather(weatherConditionImage, temperatureText, weatherConditionDesc, humidityText, windspeedText);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        updateUIForError(weatherConditionImage, temperatureText, weatherConditionDesc, humidityText, windspeedText);
                    }
                }
            }.execute();
        });

        return searchButton;
    }

    private void updateUIForError(JLabel weatherConditionImage, JLabel temperatureText, JLabel weatherConditionDesc, JLabel humidityText, JLabel windspeedText) {
        weatherConditionImage.setIcon(loadImage(ERROR_IMAGE_PATH, 450, 250));
        temperatureText.setText("Error");
        weatherConditionDesc.setText("Not found");
        humidityText.setText("<html><b>Humidity:</b> N/A</html>");
        windspeedText.setText("<html><b>Windspeed:</b> N/A</html>");
    }

    private void updateUIForWeather(JLabel weatherConditionImage, JLabel temperatureText, JLabel weatherConditionDesc, JLabel humidityText, JLabel windspeedText) {
        String weatherCondition = (String) weatherData.get("weather_condition");
        weatherConditionImage.setIcon(loadImage(getWeatherIconPath(weatherCondition), 450, 250));

        double temperature = (double) weatherData.get("temperature");
        temperatureText.setText(temperature + " C");

        weatherConditionDesc.setText(weatherCondition);

        long humidity = (long) weatherData.get("humidity");
        humidityText.setText("<html><b>Humidity:</b> " + humidity + "%</html>");

        double windspeed = (double) weatherData.get("windspeed");
        windspeedText.setText("<html><b>Windspeed:</b> " + windspeed + "km/h</html>");
    }

    private String getWeatherIconPath(String weatherCondition) {
        weatherCondition = weatherCondition.toLowerCase();
        if (weatherCondition.contains("sunny")) return CLEAR_IMAGE_PATH;
        if (weatherCondition.contains("rain")) return RAIN_IMAGE_PATH;
        if (weatherCondition.contains("snow")) return SNOW_IMAGE_PATH;
        if (weatherCondition.contains("cloud")) return CLOUDY_IMAGE_PATH;
        return ERROR_IMAGE_PATH;
    }

    private ImageIcon loadImage(String resourcePath, int targetWidth, int targetHeight) {
        try {
            if (resourcePath.endsWith(".gif")) {
                return new ImageIcon(resourcePath);
            }

            BufferedImage image = ImageIO.read(new File(resourcePath));
            Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("Image not found: " + resourcePath);
        }
        return new ImageIcon(); // Return an empty icon to avoid null references
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherAppGui().setVisible(true));
    }
}
