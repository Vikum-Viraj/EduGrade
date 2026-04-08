package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Course;
import model.Department;
import model.Enrollment;
import model.Student;

public class EnrollmentDao {

    public int createEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO Enrollments (student_id, course_id, enrollment_date, grade) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindEnrollment(ps, enrollment);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating enrollment failed; no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                throw new SQLException("Creating enrollment failed; no ID returned.");
            }
        }
    }

    public Enrollment getEnrollmentById(int enrollmentId) throws SQLException {
        String sql = "SELECT e.enrollment_id, e.student_id, s.name AS student_name, e.course_id, c.course_name, d.department_name, "
            + "e.enrollment_date, e.grade "
                + "FROM Enrollments e "
                + "JOIN Students s ON e.student_id = s.student_id "
                + "JOIN Courses c ON e.course_id = c.course_id "
            + "LEFT JOIN Departments d ON c.department_id = d.department_id "
                + "WHERE e.enrollment_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        String sql = "SELECT e.enrollment_id, e.student_id, s.name AS student_name, e.course_id, c.course_name, d.department_name, "
            + "e.enrollment_date, e.grade "
                + "FROM Enrollments e "
                + "JOIN Students s ON e.student_id = s.student_id "
                + "JOIN Courses c ON e.course_id = c.course_id "
            + "LEFT JOIN Departments d ON c.department_id = d.department_id "
                + "ORDER BY e.enrollment_id";
        List<Enrollment> enrollments = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                enrollments.add(mapRow(rs));
            }
        }

        return enrollments;
    }

    public boolean updateEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE Enrollments SET student_id = ?, course_id = ?, enrollment_date = ?, grade = ? WHERE enrollment_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            bindEnrollment(ps, enrollment);
            ps.setInt(5, enrollment.getEnrollmentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteEnrollment(int enrollmentId) throws SQLException {
        String sql = "DELETE FROM Enrollments WHERE enrollment_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Student> getStudents() throws SQLException {
        String sql = "SELECT student_id, name FROM Students ORDER BY name";
        List<Student> students = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setName(rs.getString("name"));
                students.add(student);
            }
        }

        return students;
    }

    public List<Course> getCourses() throws SQLException {
        String sql = "SELECT c.course_id, c.course_name, c.department_id, d.department_name "
                + "FROM Courses c LEFT JOIN Departments d ON c.department_id = d.department_id ORDER BY c.course_name";
        List<Course> courses = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Integer departmentId = null;
                int departmentIdValue = rs.getInt("department_id");
                if (!rs.wasNull()) {
                    departmentId = departmentIdValue;
                }

                courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        0,
                        departmentId,
                        rs.getString("department_name")));
            }
        }

        return courses;
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

    private void bindEnrollment(PreparedStatement ps, Enrollment enrollment) throws SQLException {
        if (enrollment.getStudentId() == null) {
            ps.setNull(1, java.sql.Types.INTEGER);
        } else {
            ps.setInt(1, enrollment.getStudentId());
        }

        if (enrollment.getCourseId() == null) {
            ps.setNull(2, java.sql.Types.INTEGER);
        } else {
            ps.setInt(2, enrollment.getCourseId());
        }

        LocalDate enrollmentDate = enrollment.getEnrollmentDate() == null ? LocalDate.now() : enrollment.getEnrollmentDate();
        ps.setDate(3, Date.valueOf(enrollmentDate));
        ps.setString(4, enrollment.getGrade());
    }

    private Enrollment mapRow(ResultSet rs) throws SQLException {
        Integer studentId = null;
        int studentIdValue = rs.getInt("student_id");
        if (!rs.wasNull()) {
            studentId = studentIdValue;
        }

        Integer courseId = null;
        int courseIdValue = rs.getInt("course_id");
        if (!rs.wasNull()) {
            courseId = courseIdValue;
        }

        Date dateValue = rs.getDate("enrollment_date");
        LocalDate enrollmentDate = dateValue == null ? null : dateValue.toLocalDate();

        return new Enrollment(
                rs.getInt("enrollment_id"),
                studentId,
                rs.getString("student_name"),
                courseId,
                rs.getString("course_name"),
                rs.getString("department_name"),
                enrollmentDate,
                rs.getString("grade"));
    }
}
