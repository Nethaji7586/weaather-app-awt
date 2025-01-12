import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure thread safety when creating the GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create and display the WeatherAppGui
                new WeatherAppGui().setVisible(true);
            }
        });
    }
}
