package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import config.DBConnection;
import model.Department;

public class DepartmentDao {

    public int createDepartment(String departmentName) throws SQLException {
        String sql = "INSERT INTO Departments (department_name) VALUES (?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, departmentName);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating department failed; no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                throw new SQLException("Creating department failed; no ID returned.");
            }
        }
    }

    public Department getDepartmentById(int departmentId) throws SQLException {
        String sql = "SELECT department_id, department_name FROM Departments WHERE department_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<Department> getAllDepartments() throws SQLException {
        String sql = "SELECT department_id, department_name FROM Departments ORDER BY department_id";
        List<Department> departments = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                departments.add(mapRow(rs));
            }
        }

        return departments;
    }

    public boolean updateDepartment(int departmentId, String newName) throws SQLException {
        String sql = "UPDATE Departments SET department_name = ? WHERE department_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, departmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteDepartment(int departmentId) throws SQLException {
        String sql = "DELETE FROM Departments WHERE department_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            return ps.executeUpdate() > 0;
        }
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("department_id");
        String name = rs.getString("department_name");
        return new Department(id, name);
    }
}
