package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

import controller.EnrollmentController;
import model.Course;
import model.Enrollment;
import model.Student;

public class EnrollmentCrudFrame extends JFrame {
    private final EnrollmentController controller;

    private final JTextField enrollmentDateField;
    private final JTextField gradeField;
    private final JComboBox<Student> studentComboBox;
    private final JComboBox<Course> courseComboBox;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public EnrollmentCrudFrame(EnrollmentController controller) {
        this.controller = controller;

        setTitle("Enrollment Management");
        setSize(1150, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Enrollment Details"));

        enrollmentDateField = new JTextField(LocalDate.now().toString());
        gradeField = new JTextField();
        studentComboBox = new JComboBox<>();
        courseComboBox = new JComboBox<>();

        formPanel.add(new JLabel("Student", SwingConstants.RIGHT));
        formPanel.add(studentComboBox);
        formPanel.add(new JLabel("Course", SwingConstants.RIGHT));
        formPanel.add(courseComboBox);
        formPanel.add(new JLabel("Enrollment Date (yyyy-MM-dd)", SwingConstants.RIGHT));
        formPanel.add(enrollmentDateField);
        formPanel.add(new JLabel("Grade", SwingConstants.RIGHT));
        formPanel.add(gradeField);

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

        tableModel = new DefaultTableModel(new String[] {
                "ID", "Student", "Course", "Department", "Enrollment Date", "Grade", "Update", "Delete"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 6;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        configureActionColumn(6, "Update", row -> editEnrollmentFromRow(row));
        configureActionColumn(7, "Delete", row -> deleteEnrollmentFromRow(row));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1120, 260));
        add(scrollPane, BorderLayout.SOUTH);

        createButton.addActionListener(e -> createEnrollment());
        clearButton.addActionListener(e -> clearInputs());
        refreshButton.addActionListener(e -> {
            loadEnrollments();
            loadReferenceData();
        });

        loadReferenceData();
        loadEnrollments();
    }

    private void createEnrollment() {
        Enrollment enrollment = collectEnrollmentFromForm(false);
        if (enrollment == null) {
            return;
        }

        try {
            int newId = controller.createEnrollment(enrollment);
            showInfo("Enrollment created with ID: " + newId);
            loadEnrollments();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadEnrollments() {
        try {
            List<Enrollment> enrollments = controller.getAllEnrollments();
            tableModel.setRowCount(0);
            for (Enrollment enrollment : enrollments) {
                tableModel.addRow(new Object[] {
                        enrollment.getEnrollmentId(),
                        enrollment.getStudentName(),
                        enrollment.getCourseName(),
                        enrollment.getDepartmentName() == null ? "-" : enrollment.getDepartmentName(),
                        enrollment.getEnrollmentDate() == null ? "" : enrollment.getEnrollmentDate().toString(),
                        enrollment.getGrade() == null ? "" : enrollment.getGrade(),
                        "Update",
                        "Delete"
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void editEnrollmentFromRow(int row) {
        try {
            int id = getEnrollmentIdAtRow(row);
            Enrollment enrollment = controller.getEnrollmentById(id);
            if (enrollment == null) {
                showWarning("Enrollment not found for ID: " + id);
                return;
            }

            JTextField gradeInput = new JTextField(enrollment.getGrade() == null ? "" : enrollment.getGrade(), 20);
            JTextField dateInput = new JTextField(enrollment.getEnrollmentDate() == null ? LocalDate.now().toString() : enrollment.getEnrollmentDate().toString(), 20);
            JComboBox<Student> studentInput = new JComboBox<>(studentComboBox.getModel());
            JComboBox<Course> courseInput = new JComboBox<>(courseComboBox.getModel());

            selectStudent(studentInput, enrollment.getStudentId());
            selectCourse(courseInput, enrollment.getCourseId());

            JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
            panel.add(new JLabel("Student"));
            panel.add(studentInput);
            panel.add(new JLabel("Course"));
            panel.add(courseInput);
            panel.add(new JLabel("Enrollment Date"));
            panel.add(dateInput);
            panel.add(new JLabel("Grade"));
            panel.add(gradeInput);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Enrollment", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            LocalDate enrollmentDate;
            try {
                enrollmentDate = LocalDate.parse(dateInput.getText().trim());
            } catch (DateTimeParseException ex) {
                showWarning("Enrollment date must be in yyyy-MM-dd format.");
                return;
            }

            Student selectedStudent = (Student) studentInput.getSelectedItem();
            Course selectedCourse = (Course) courseInput.getSelectedItem();
            if (selectedStudent == null || selectedStudent.getStudentId() == 0) {
                showWarning("Student is required.");
                return;
            }
            if (selectedCourse == null || selectedCourse.getCourseId() == 0) {
                showWarning("Course is required.");
                return;
            }

            Enrollment updatedEnrollment = new Enrollment();
            updatedEnrollment.setEnrollmentId(id);
            updatedEnrollment.setStudentId(selectedStudent.getStudentId());
            updatedEnrollment.setStudentName(selectedStudent.getName());
            updatedEnrollment.setCourseId(selectedCourse.getCourseId());
            updatedEnrollment.setCourseName(selectedCourse.getCourseName());
            updatedEnrollment.setEnrollmentDate(enrollmentDate);
            updatedEnrollment.setGrade(emptyToNull(gradeInput.getText()));

            boolean updated = controller.updateEnrollment(updatedEnrollment);
            if (!updated) {
                showWarning("No enrollment updated. Check the ID.");
                return;
            }

            showInfo("Enrollment updated.");
            loadEnrollments();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteEnrollmentFromRow(int row) {
        try {
            int id = getEnrollmentIdAtRow(row);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete enrollment with ID " + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean deleted = controller.deleteEnrollment(id);
            if (!deleted) {
                showWarning("No enrollment deleted. Check the ID.");
                return;
            }

            showInfo("Enrollment deleted.");
            clearInputs();
            loadEnrollments();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private int getEnrollmentIdAtRow(int row) {
        Object value = tableModel.getValueAt(row, 0);
        return Integer.parseInt(String.valueOf(value));
    }

    private void loadReferenceData() {
        try {
            DefaultComboBoxModel<Student> studentModel = new DefaultComboBoxModel<>();
            studentModel.addElement(new Student(0, "Select Student", null, null, null, null, null, null));
            for (Student student : controller.getStudents()) {
                studentModel.addElement(student);
            }
            studentComboBox.setModel(studentModel);

            DefaultComboBoxModel<Course> courseModel = new DefaultComboBoxModel<>();
            courseModel.addElement(new Course(0, "Select Course", 0, null, null));
            for (Course course : controller.getCourses()) {
                        courseModel.addElement(course);
            }
            courseComboBox.setModel(courseModel);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void selectStudent(JComboBox<Student> comboBox, Integer studentId) {
        if (studentId == null) {
            comboBox.setSelectedIndex(0);
            return;
        }

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Student student = comboBox.getItemAt(i);
            if (student != null && student.getStudentId() == studentId) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        comboBox.setSelectedIndex(0);
    }

    private void selectCourse(JComboBox<Course> comboBox, Integer courseId) {
        if (courseId == null) {
            comboBox.setSelectedIndex(0);
            return;
        }

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Course course = comboBox.getItemAt(i);
            if (course != null && course.getCourseId() == courseId) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        comboBox.setSelectedIndex(0);
    }

    private Enrollment collectEnrollmentFromForm(boolean requireId) {
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        if (selectedStudent == null || selectedStudent.getStudentId() == 0) {
            showWarning("Student is required.");
            return null;
        }

        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse == null || selectedCourse.getCourseId() == 0) {
            showWarning("Course is required.");
            return null;
        }

        LocalDate enrollmentDate;
        String dateText = enrollmentDateField.getText().trim();
        if (dateText.isEmpty()) {
            enrollmentDate = LocalDate.now();
        } else {
            try {
                enrollmentDate = LocalDate.parse(dateText);
            } catch (DateTimeParseException ex) {
                showWarning("Enrollment date must be in yyyy-MM-dd format.");
                return null;
            }
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(selectedStudent.getStudentId());
        enrollment.setStudentName(selectedStudent.getName());
        enrollment.setCourseId(selectedCourse.getCourseId());
        enrollment.setCourseName(selectedCourse.getCourseName());
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setGrade(emptyToNull(gradeField.getText()));
        return enrollment;
    }

    private String emptyToNull(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void clearInputs() {
        enrollmentDateField.setText(LocalDate.now().toString());
        gradeField.setText("");
        if (studentComboBox.getItemCount() > 0) {
            studentComboBox.setSelectedIndex(0);
        }
        if (courseComboBox.getItemCount() > 0) {
            courseComboBox.setSelectedIndex(0);
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
