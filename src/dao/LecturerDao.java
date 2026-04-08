package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Department;
import model.Lecturer;

public class LecturerDao {

    public int createLecturer(Lecturer lecturer) throws SQLException {
        String sql = "INSERT INTO Lecturers (name, email, age, address, phone, department_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindLecturer(ps, lecturer);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating lecturer failed; no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                throw new SQLException("Creating lecturer failed; no ID returned.");
            }
        }
    }

    public Lecturer getLecturerById(int lecturerId) throws SQLException {
        String sql = "SELECT l.lecturer_id, l.name, l.email, l.age, l.address, l.phone, l.department_id, d.department_name "
                + "FROM Lecturers l LEFT JOIN Departments d ON l.department_id = d.department_id WHERE l.lecturer_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<Lecturer> getAllLecturers() throws SQLException {
        String sql = "SELECT l.lecturer_id, l.name, l.email, l.age, l.address, l.phone, l.department_id, d.department_name "
                + "FROM Lecturers l LEFT JOIN Departments d ON l.department_id = d.department_id ORDER BY l.lecturer_id";
        List<Lecturer> lecturers = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lecturers.add(mapRow(rs));
            }
        }

        return lecturers;
    }

    public boolean updateLecturer(Lecturer lecturer) throws SQLException {
        String sql = "UPDATE Lecturers SET name = ?, email = ?, age = ?, address = ?, phone = ?, department_id = ? WHERE lecturer_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            bindLecturer(ps, lecturer);
            ps.setInt(7, lecturer.getLecturerId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteLecturer(int lecturerId) throws SQLException {
        String sql = "DELETE FROM Lecturers WHERE lecturer_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Department> getDepartments() throws SQLException {
        String sql = "SELECT department_id, department_name FROM Departments ORDER BY department_name";
        List<Department> departments = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                departments.add(new Department(rs.getInt("department_id"), rs.getString("department_name")));
            }
        }

        return departments;
    }

    private void bindLecturer(PreparedStatement ps, Lecturer lecturer) throws SQLException {
        ps.setString(1, lecturer.getName());
        ps.setString(2, lecturer.getEmail());
        ps.setString(3, lecturer.getAge());
        ps.setString(4, lecturer.getAddress());
        ps.setString(5, lecturer.getPhone());

        if (lecturer.getDepartmentId() == null) {
            ps.setNull(6, java.sql.Types.INTEGER);
        } else {
            ps.setInt(6, lecturer.getDepartmentId());
        }
    }

    private Lecturer mapRow(ResultSet rs) throws SQLException {
        Integer departmentId = null;
        int departmentIdValue = rs.getInt("department_id");
        if (!rs.wasNull()) {
            departmentId = departmentIdValue;
        }

        return new Lecturer(
                rs.getInt("lecturer_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("age"),
                rs.getString("address"),
                rs.getString("phone"),
                departmentId,
                rs.getString("department_name"));
    }
}
