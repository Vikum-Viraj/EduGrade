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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import dao.StudentDao;
import model.Department;
import model.Student;

public class StudentCrudFrame extends JFrame {
    private final StudentDao studentDao;

    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField ageField;
    private final JTextField addressField;
    private final JTextField phoneField;
    private final JComboBox<Department> departmentComboBox;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public StudentCrudFrame() {
        this.studentDao = new StudentDao();

        setTitle("Student CRUD");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        nameField = new JTextField();
        emailField = new JTextField();
        ageField = new JTextField();
        addressField = new JTextField();
        phoneField = new JTextField();
        departmentComboBox = new JComboBox<>();

        formPanel.add(new JLabel("Name", SwingConstants.RIGHT));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email", SwingConstants.RIGHT));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Age", SwingConstants.RIGHT));
        formPanel.add(ageField);
        formPanel.add(new JLabel("Address", SwingConstants.RIGHT));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Phone", SwingConstants.RIGHT));
        formPanel.add(phoneField);
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

        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Email", "Age", "Address", "Phone", "Department", "Update", "Delete" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 7;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        configureActionColumn(7, "Update", row -> editStudentFromRow(row));
        configureActionColumn(8, "Delete", row -> deleteStudentFromRow(row));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1060, 250));
        add(scrollPane, BorderLayout.SOUTH);

        createButton.addActionListener(e -> createStudent());
        clearButton.addActionListener(e -> clearInputs());
        refreshButton.addActionListener(e -> {
            loadStudents();
            loadDepartments();
        });

        loadDepartments();
        loadStudents();
    }

    private void createStudent() {
        Student student = collectStudentFromForm(false);
        if (student == null) {
            return;
        }

        try {
            int newId = studentDao.createStudent(student);
            showInfo("Student created with ID: " + newId);
            loadStudents();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadStudents() {
        try {
            List<Student> students = studentDao.getAllStudents();
            tableModel.setRowCount(0);
            for (Student student : students) {
                tableModel.addRow(new Object[] {
                        student.getStudentId(),
                        student.getName(),
                        student.getEmail(),
                        student.getAge(),
                        student.getAddress(),
                        student.getPhone(),
                        student.getDepartmentName() == null ? "-" : student.getDepartmentName(),
                        "Update",
                        "Delete"
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void editStudentFromRow(int row) {
        try {
            int id = getStudentIdAtRow(row);
            Student student = studentDao.getStudentById(id);
            if (student == null) {
                showWarning("Student not found for ID: " + id);
                return;
            }

            JTextField nameInput = new JTextField(student.getName(), 20);
            JTextField emailInput = new JTextField(student.getEmail() == null ? "" : student.getEmail(), 20);
            JTextField ageInput = new JTextField(student.getAge() == null ? "" : student.getAge(), 20);
            JTextField addressInput = new JTextField(student.getAddress() == null ? "" : student.getAddress(), 20);
            JTextField phoneInput = new JTextField(student.getPhone() == null ? "" : student.getPhone(), 20);
            JComboBox<Department> departmentInput = new JComboBox<>(departmentComboBox.getModel());

            if (student.getDepartmentId() == null) {
                departmentInput.setSelectedIndex(0);
            } else {
                for (int i = 0; i < departmentInput.getItemCount(); i++) {
                    Department department = departmentInput.getItemAt(i);
                    if (department != null && department.getDepartmentId() == student.getDepartmentId()) {
                        departmentInput.setSelectedIndex(i);
                        break;
                    }
                }
            }

            JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
            panel.add(new JLabel("Name"));
            panel.add(nameInput);
            panel.add(new JLabel("Email"));
            panel.add(emailInput);
            panel.add(new JLabel("Age"));
            panel.add(ageInput);
            panel.add(new JLabel("Address"));
            panel.add(addressInput);
            panel.add(new JLabel("Phone"));
            panel.add(phoneInput);
            panel.add(new JLabel("Department"));
            panel.add(departmentInput);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Student", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String name = nameInput.getText().trim();
            if (name.isEmpty()) {
                showWarning("Name is required.");
                return;
            }

            Student updatedStudent = new Student();
            updatedStudent.setStudentId(id);
            updatedStudent.setName(name);
            updatedStudent.setEmail(emptyToNull(emailInput.getText()));
            updatedStudent.setAge(emptyToNull(ageInput.getText()));
            updatedStudent.setAddress(emptyToNull(addressInput.getText()));
            updatedStudent.setPhone(emptyToNull(phoneInput.getText()));

            Department selectedDepartment = (Department) departmentInput.getSelectedItem();
            if (selectedDepartment != null && selectedDepartment.getDepartmentId() != 0) {
                updatedStudent.setDepartmentId(selectedDepartment.getDepartmentId());
                updatedStudent.setDepartmentName(selectedDepartment.getDepartmentName());
            } else {
                updatedStudent.setDepartmentId(null);
                updatedStudent.setDepartmentName(null);
            }

            boolean updated = studentDao.updateStudent(updatedStudent);
            if (!updated) {
                showWarning("No student updated. Check the ID.");
                return;
            }

            showInfo("Student updated.");
            loadStudents();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteStudentFromRow(int row) {
        try {
            int id = getStudentIdAtRow(row);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete student with ID " + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean deleted = studentDao.deleteStudent(id);
            if (!deleted) {
                showWarning("No student deleted. Check the ID.");
                return;
            }

            showInfo("Student deleted.");
            clearInputs();
            loadStudents();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private int getStudentIdAtRow(int row) {
        Object value = tableModel.getValueAt(row, 0);
        return Integer.parseInt(String.valueOf(value));
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

    private void loadDepartments() {
        try {
            List<Department> departments = studentDao.getDepartments();
            DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>();
            model.addElement(nullDepartmentOption());
            for (Department department : departments) {
                model.addElement(department);
            }
            departmentComboBox.setModel(model);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private Department nullDepartmentOption() {
        return new Department(0, "No Department");
    }

    private Student collectStudentFromForm(boolean requireId) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showWarning("Name is required.");
            return null;
        }

        Student student = new Student();
        student.setName(name);
        student.setEmail(emptyToNull(emailField.getText()));
        student.setAge(emptyToNull(ageField.getText()));
        student.setAddress(emptyToNull(addressField.getText()));
        student.setPhone(emptyToNull(phoneField.getText()));

        Department selectedDepartment = (Department) departmentComboBox.getSelectedItem();
        if (selectedDepartment != null && selectedDepartment.getDepartmentId() != 0) {
            student.setDepartmentId(selectedDepartment.getDepartmentId());
            student.setDepartmentName(selectedDepartment.getDepartmentName());
        } else {
            student.setDepartmentId(null);
            student.setDepartmentName(null);
        }

        return student;
    }

    private void populateForm(Student student) {
        nameField.setText(student.getName());
        emailField.setText(student.getEmail() == null ? "" : student.getEmail());
        ageField.setText(student.getAge() == null ? "" : student.getAge());
        addressField.setText(student.getAddress() == null ? "" : student.getAddress());
        phoneField.setText(student.getPhone() == null ? "" : student.getPhone());

        if (student.getDepartmentId() == null) {
            departmentComboBox.setSelectedIndex(0);
            return;
        }

        for (int i = 0; i < departmentComboBox.getItemCount(); i++) {
            Department department = departmentComboBox.getItemAt(i);
            if (department != null && department.getDepartmentId() == student.getDepartmentId()) {
                departmentComboBox.setSelectedIndex(i);
                return;
            }
        }

        departmentComboBox.setSelectedIndex(0);
    }

    private String emptyToNull(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void clearInputs() {
        nameField.setText("");
        emailField.setText("");
        ageField.setText("");
        addressField.setText("");
        phoneField.setText("");
        if (departmentComboBox.getItemCount() > 0) {
            departmentComboBox.setSelectedIndex(0);
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
