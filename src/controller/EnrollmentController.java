package controller;

import java.sql.SQLException;
import java.util.List;

import dao.EnrollmentDao;
import model.Course;
import model.Enrollment;
import model.Student;

public class EnrollmentController {
    private final EnrollmentDao enrollmentDao;

    public EnrollmentController() {
        this.enrollmentDao = new EnrollmentDao();
    }

    public int createEnrollment(Enrollment enrollment) throws SQLException {
        return enrollmentDao.createEnrollment(enrollment);
    }

    public Enrollment getEnrollmentById(int id) throws SQLException {
        return enrollmentDao.getEnrollmentById(id);
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        return enrollmentDao.getAllEnrollments();
    }

    public boolean updateEnrollment(Enrollment enrollment) throws SQLException {
        return enrollmentDao.updateEnrollment(enrollment);
    }

    public boolean deleteEnrollment(int id) throws SQLException {
        return enrollmentDao.deleteEnrollment(id);
    }

    public List<Student> getStudents() throws SQLException {
        return enrollmentDao.getStudents();
    }

    public List<Course> getCourses() throws SQLException {
        return enrollmentDao.getCourses();
    }
}
