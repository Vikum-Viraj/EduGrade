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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
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

import controller.DepartmentController;
import model.Department;

public class DepartmentCrudFrame extends JFrame {
    private final DepartmentController controller;

    private final JTextField nameField;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public DepartmentCrudFrame(DepartmentController controller) {
        this.controller = controller;

        setTitle("Department Management");
        setSize(780, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Department Details"));

        JLabel nameLabel = new JLabel("Department Name", SwingConstants.RIGHT);
        nameField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(nameField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
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

        tableModel = new DefaultTableModel(new String[] { "Department ID", "Department Name", "Update", "Delete" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        configureActionColumn(2, "Update", row -> editDepartmentFromRow(row));
        configureActionColumn(3, "Delete", row -> deleteDepartmentFromRow(row));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Departments"));
        scrollPane.setPreferredSize(new Dimension(760, 220));

        add(scrollPane, BorderLayout.SOUTH);

        createButton.addActionListener(e -> createDepartment());
        clearButton.addActionListener(e -> clearInputs());
        refreshButton.addActionListener(e -> loadDepartments());

        loadDepartments();
    }

    private void createDepartment() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showWarning("Department name is required.");
            return;
        }

        try {
            int newId = controller.createDepartment(name);
            showInfo("Department created with ID: " + newId);
            loadDepartments();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadDepartments() {
        try {
            List<Department> departments = controller.getAllDepartments();
            tableModel.setRowCount(0);
            for (Department department : departments) {
                tableModel.addRow(new Object[] { department.getDepartmentId(), department.getDepartmentName(), "Update", "Delete" });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void editDepartmentFromRow(int row) {
        try {
            int id = getDepartmentIdAtRow(row);
            Department department = controller.getDepartmentById(id);
            if (department == null) {
                showWarning("Department not found for ID: " + id);
                return;
            }

            JTextField nameInput = new JTextField(department.getDepartmentName(), 20);
            JPanel panel = new JPanel(new GridLayout(1, 2, 8, 8));
            panel.add(new JLabel("Department Name"));
            panel.add(nameInput);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Department", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String newName = nameInput.getText().trim();
            if (newName.isEmpty()) {
                showWarning("Department name is required.");
                return;
            }

            boolean updated = controller.updateDepartment(id, newName);
            if (!updated) {
                showWarning("No department updated. Check the ID.");
                return;
            }

            showInfo("Department updated.");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteDepartmentFromRow(int row) {
        try {
            int id = getDepartmentIdAtRow(row);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete department with ID " + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean deleted = controller.deleteDepartment(id);
            if (!deleted) {
                showWarning("No department deleted. Check the ID.");
                return;
            }

            showInfo("Department deleted.");
            clearInputs();
            loadDepartments();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private int getDepartmentIdAtRow(int row) {
        Object value = tableModel.getValueAt(row, 0);
        return Integer.parseInt(String.valueOf(value));
    }

    private void configureActionColumn(int columnIndex, String text, IntConsumer rowAction) {
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        ActionColumn actionColumn = new ActionColumn(table, text, rowAction);
        column.setCellRenderer(actionColumn);
        column.setCellEditor(actionColumn);
        column.setPreferredWidth(90);
    }

    private class ActionColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
        private final JButton renderButton;
        private final JButton editButton;
        private final JTable table;
        private final IntConsumer rowAction;
        private int row;

        ActionColumn(JTable table, String text, IntConsumer rowAction) {
            this.table = table;
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

    private void clearInputs() {
        nameField.setText("");
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

        JOptionPane.showMessageDialog(this,
                message.toString(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
