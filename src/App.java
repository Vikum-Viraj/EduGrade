import javax.swing.SwingUtilities;

import ui.DashboardFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame frame = new DashboardFrame();
            frame.setVisible(true);
        });
    }
}
