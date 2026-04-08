package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Edugrade Dashboard");
        setSize(700, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 41, 59));
        JLabel titleLabel = new JLabel("Edugrade Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel subtitleLabel = new JLabel("Choose a section to manage records", SwingConstants.CENTER);
        subtitleLabel.setForeground(new Color(203, 213, 225));
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 18, 18));
        centerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton departmentsButton = new JButton("Departments");
        departmentsButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        departmentsButton.addActionListener(e -> new DepartmentCrudFrame().setVisible(true));

        JButton studentsButton = new JButton("Students");
        studentsButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        studentsButton.addActionListener(e -> new StudentCrudFrame().setVisible(true));

        JButton lecturersButton = new JButton("Lecturers");
        lecturersButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        lecturersButton.addActionListener(e -> new LecturerCrudFrame().setVisible(true));

        JButton coursesButton = new JButton("Courses");
        coursesButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        coursesButton.addActionListener(e -> new CourseCrudFrame().setVisible(true));

        centerPanel.add(departmentsButton);
        centerPanel.add(studentsButton);
        centerPanel.add(lecturersButton);
        centerPanel.add(coursesButton);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
}
