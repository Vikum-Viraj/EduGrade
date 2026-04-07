package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Department;
import model.Student;

public class StudentDao {

    public int createStudent(Student student) throws SQLException {
        String sql = "INSERT INTO Students (name, email, age, address, phone, department_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindStudent(ps, student, false);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating student failed; no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                throw new SQLException("Creating student failed; no ID returned.");
            }
        }
    }

    public Student getStudentById(int studentId) throws SQLException {
        String sql = "SELECT s.student_id, s.name, s.email, s.age, s.address, s.phone, s.department_id, d.department_name "
                + "FROM Students s LEFT JOIN Departments d ON s.department_id = d.department_id WHERE s.student_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT s.student_id, s.name, s.email, s.age, s.address, s.phone, s.department_id, d.department_name "
                + "FROM Students s LEFT JOIN Departments d ON s.department_id = d.department_id ORDER BY s.student_id";
        List<Student> students = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        }

        return students;
    }

    public boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE Students SET name = ?, email = ?, age = ?, address = ?, phone = ?, department_id = ? WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            bindStudent(ps, student, true);
            ps.setInt(7, student.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM Students WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
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

    private void bindStudent(PreparedStatement ps, Student student, boolean includeIdAtEnd) throws SQLException {
        ps.setString(1, student.getName());
        ps.setString(2, student.getEmail());
        ps.setString(3, student.getAge());
        ps.setString(4, student.getAddress());
        ps.setString(5, student.getPhone());

        if (student.getDepartmentId() == null) {
            ps.setNull(6, java.sql.Types.INTEGER);
        } else {
            ps.setInt(6, student.getDepartmentId());
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Integer departmentId = null;
        int departmentIdValue = rs.getInt("department_id");
        if (!rs.wasNull()) {
            departmentId = departmentIdValue;
        }

        return new Student(
                rs.getInt("student_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("age"),
                rs.getString("address"),
                rs.getString("phone"),
                departmentId,
                rs.getString("department_name"));
    }
}
