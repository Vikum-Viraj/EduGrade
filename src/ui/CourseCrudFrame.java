package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import controller.CourseController;
import model.Course;
import model.Department;

public class CourseCrudFrame extends JFrame {
    private final CourseController controller;

    private final JTextField courseNameField;
    private final JTextField creditsField;
    private final JComboBox<Department> departmentComboBox;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public CourseCrudFrame(CourseController controller) {
        this.controller = controller;

        setTitle("Course Management");
        setSize(1050, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));

        courseNameField = new JTextField();
        creditsField = new JTextField();
        departmentComboBox = new JComboBox<>();

        formPanel.add(new JLabel("Course Name", SwingConstants.RIGHT));
        formPanel.add(courseNameField);
        formPanel.add(new JLabel("Credits", SwingConstants.RIGHT));
        formPanel.add(creditsField);
        formPanel.add(new JLabel("Department", SwingConstants.RIGHT));
        formPanel.add(departmentComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton createButton = new JButton("Create");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh List");

        buttonPanel.add(createButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(topPanel);
        add(centerPanel, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new String[] { "ID", "Course Name", "Credits", "Department", "Update", "Delete" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 4;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        configureActionColumn(4, "Update", row -> editCourseFromRow(row));
        configureActionColumn(5, "Delete", row -> deleteCourseFromRow(row));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1030, 250));
        add(scrollPane, BorderLayout.SOUTH);

        createButton.addActionListener(e -> createCourse());
        clearButton.addActionListener(e -> clearInputs());
        refreshButton.addActionListener(e -> {
            loadCourses();
            loadDepartments();
        });

        loadDepartments();
        loadCourses();
    }

    private void createCourse() {
        Course course = collectCourseFromForm(false);
        if (course == null) {
            return;
        }

        try {
            int newId = controller.createCourse(course);
            showInfo("Course created with ID: " + newId);
            loadCourses();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadCourses() {
        try {
            List<Course> courses = controller.getAllCourses();
            tableModel.setRowCount(0);
            for (Course course : courses) {
                tableModel.addRow(new Object[] {
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getCredits(),
                        course.getDepartmentName() == null ? "-" : course.getDepartmentName(),
                        "Update",
                        "Delete"
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void editCourseFromRow(int row) {
        try {
            int id = getCourseIdAtRow(row);
            Course course = controller.getCourseById(id);
            if (course == null) {
                showWarning("Course not found for ID: " + id);
                return;
            }

            JTextField courseNameInput = new JTextField(course.getCourseName(), 20);
            JTextField creditsInput = new JTextField(String.valueOf(course.getCredits()), 20);
            JComboBox<Department> departmentInput = new JComboBox<>(departmentComboBox.getModel());

            if (course.getDepartmentId() == null) {
                departmentInput.setSelectedIndex(0);
            } else {
                for (int i = 0; i < departmentInput.getItemCount(); i++) {
                    Department department = departmentInput.getItemAt(i);
                    if (department != null && department.getDepartmentId() == course.getDepartmentId()) {
                        departmentInput.setSelectedIndex(i);
                        break;
                    }
                }
            }

            JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
            panel.add(new JLabel("Course Name"));
            panel.add(courseNameInput);
            panel.add(new JLabel("Credits"));
            panel.add(creditsInput);
            panel.add(new JLabel("Department"));
            panel.add(departmentInput);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Course", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String courseName = courseNameInput.getText().trim();
            if (courseName.isEmpty()) {
                showWarning("Course name is required.");
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsInput.getText().trim());
                if (credits <= 0) {
                    showWarning("Credits must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showWarning("Credits must be a valid integer.");
                return;
            }

            Course updatedCourse = new Course();
            updatedCourse.setCourseId(id);
            updatedCourse.setCourseName(courseName);
            updatedCourse.setCredits(credits);

            Department selectedDepartment = (Department) departmentInput.getSelectedItem();
            if (selectedDepartment != null && selectedDepartment.getDepartmentId() != 0) {
                updatedCourse.setDepartmentId(selectedDepartment.getDepartmentId());
                updatedCourse.setDepartmentName(selectedDepartment.getDepartmentName());
            } else {
                updatedCourse.setDepartmentId(null);
                updatedCourse.setDepartmentName(null);
            }

            boolean updated = controller.updateCourse(updatedCourse);
            if (!updated) {
                showWarning("No course updated. Check the ID.");
                return;
            }

            showInfo("Course updated.");
            loadCourses();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteCourseFromRow(int row) {
        try {
            int id = getCourseIdAtRow(row);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete course with ID " + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean deleted = controller.deleteCourse(id);
            if (!deleted) {
                showWarning("No course deleted. Check the ID.");
                return;
            }

            showInfo("Course deleted.");
            clearInputs();
            loadCourses();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private int getCourseIdAtRow(int row) {
        Object value = tableModel.getValueAt(row, 0);
        return Integer.parseInt(String.valueOf(value));
    }

    private void loadDepartments() {
        try {
            List<Department> departments = controller.getDepartments();
            DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>();
            model.addElement(new Department(0, "No Department"));
            for (Department department : departments) {
                model.addElement(department);
            }
            departmentComboBox.setModel(model);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private Course collectCourseFromForm(boolean requireId) {
        String courseName = courseNameField.getText().trim();
        if (courseName.isEmpty()) {
            showWarning("Course name is required.");
            return null;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsField.getText().trim());
            if (credits <= 0) {
                showWarning("Credits must be greater than 0.");
                return null;
            }
        } catch (NumberFormatException ex) {
            showWarning("Credits must be a valid integer.");
            return null;
        }

        Course course = new Course();
        course.setCourseName(courseName);
        course.setCredits(credits);

        Department selectedDepartment = (Department) departmentComboBox.getSelectedItem();
        if (selectedDepartment != null && selectedDepartment.getDepartmentId() != 0) {
            course.setDepartmentId(selectedDepartment.getDepartmentId());
            course.setDepartmentName(selectedDepartment.getDepartmentName());
        } else {
            showWarning("Department is required.");
            return null;
        }

        return course;
    }

    private void clearInputs() {
        courseNameField.setText("");
        creditsField.setText("");
        if (departmentComboBox.getItemCount() > 0) {
            departmentComboBox.setSelectedIndex(0);
        }
    }

    private void configureActionColumn(int columnIndex, String text, IntConsumer rowAction) {
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        ActionColumn actionColumn = new ActionColumn(text, rowAction);
        column.setCellRenderer(actionColumn);
        column.setCellEditor(actionColumn);
        column.setPreferredWidth(90);
    }

    private class ActionColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
        private final JButton renderButton;
        private final JButton editButton;
        private final IntConsumer rowAction;
        private int row;

        ActionColumn(String text, IntConsumer rowAction) {
            this.rowAction = rowAction;
            this.renderButton = new JButton(text);
            this.editButton = new JButton(text);
            this.editButton.addActionListener((ActionEvent e) -> {
                fireEditingStopped();
                rowAction.accept(row);
            });
            this.renderButton.setFocusable(false);
            this.editButton.setFocusable(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            return renderButton;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            this.row = row;
            return editButton;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(Exception exception) {
        StringBuilder message = new StringBuilder();
        message.append(exception.getMessage() == null ? "Unknown database error." : exception.getMessage());
        if (exception instanceof SQLException sqlException) {
            message.append("\nSQLState: ").append(sqlException.getSQLState());
            message.append("\nErrorCode: ").append(sqlException.getErrorCode());
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
