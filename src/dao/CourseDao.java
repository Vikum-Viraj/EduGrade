package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Course;
import model.Department;

public class CourseDao {

    public int createCourse(Course course) throws SQLException {
        String sql = "INSERT INTO Courses (course_name, credits, department_id) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCourse(ps, course);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating course failed; no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                throw new SQLException("Creating course failed; no ID returned.");
            }
        }
    }

    public Course getCourseById(int courseId) throws SQLException {
        String sql = "SELECT c.course_id, c.course_name, c.credits, c.department_id, d.department_name "
                + "FROM Courses c LEFT JOIN Departments d ON c.department_id = d.department_id WHERE c.course_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<Course> getAllCourses() throws SQLException {
        String sql = "SELECT c.course_id, c.course_name, c.credits, c.department_id, d.department_name "
                + "FROM Courses c LEFT JOIN Departments d ON c.department_id = d.department_id ORDER BY c.course_id";
        List<Course> courses = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                courses.add(mapRow(rs));
            }
        }

        return courses;
    }

    public boolean updateCourse(Course course) throws SQLException {
        String sql = "UPDATE Courses SET course_name = ?, credits = ?, department_id = ? WHERE course_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            bindCourse(ps, course);
            ps.setInt(4, course.getCourseId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM Courses WHERE course_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
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

    private void bindCourse(PreparedStatement ps, Course course) throws SQLException {
        ps.setString(1, course.getCourseName());
        ps.setInt(2, course.getCredits());
        if (course.getDepartmentId() == null) {
            ps.setNull(3, java.sql.Types.INTEGER);
        } else {
            ps.setInt(3, course.getDepartmentId());
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Integer departmentId = null;
        int departmentIdValue = rs.getInt("department_id");
        if (!rs.wasNull()) {
            departmentId = departmentIdValue;
        }

        return new Course(
                rs.getInt("course_id"),
                rs.getString("course_name"),
                rs.getInt("credits"),
                departmentId,
                rs.getString("department_name"));
    }
}
