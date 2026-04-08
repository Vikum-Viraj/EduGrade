import javax.swing.SwingUtilities;

import controller.DashboardController;
import ui.DashboardFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame frame = new DashboardFrame(new DashboardController());
            frame.setVisible(true);
        });
    }
}
