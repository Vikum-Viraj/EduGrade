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

import controller.DashboardController;

public class DashboardFrame extends JFrame {
    private final DashboardController controller;

    public DashboardFrame(DashboardController controller) {
        this.controller = controller;
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

        JPanel centerPanel = new JPanel(new GridLayout(2, 3, 18, 18));
        centerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton departmentsButton = new JButton("Departments");
        departmentsButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        departmentsButton.addActionListener(e -> this.controller.openDepartments());

        JButton studentsButton = new JButton("Students");
        studentsButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        studentsButton.addActionListener(e -> this.controller.openStudents());

        JButton lecturersButton = new JButton("Lecturers");
        lecturersButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        lecturersButton.addActionListener(e -> this.controller.openLecturers());

        JButton coursesButton = new JButton("Courses");
        coursesButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        coursesButton.addActionListener(e -> this.controller.openCourses());

        JButton enrollmentsButton = new JButton("Enrollments");
        enrollmentsButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        enrollmentsButton.addActionListener(e -> this.controller.openEnrollments());

        centerPanel.add(departmentsButton);
        centerPanel.add(studentsButton);
        centerPanel.add(lecturersButton);
        centerPanel.add(coursesButton);
        centerPanel.add(enrollmentsButton);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
}
