package controller;

import java.sql.SQLException;
import java.util.List;

import dao.CourseDao;
import model.Course;
import model.Department;

public class CourseController {
    private final CourseDao courseDao;

    public CourseController() {
        this.courseDao = new CourseDao();
    }

    public int createCourse(Course course) throws SQLException {
        return courseDao.createCourse(course);
    }

    public Course getCourseById(int id) throws SQLException {
        return courseDao.getCourseById(id);
    }

    public List<Course> getAllCourses() throws SQLException {
        return courseDao.getAllCourses();
    }

    public boolean updateCourse(Course course) throws SQLException {
        return courseDao.updateCourse(course);
    }

    public boolean deleteCourse(int id) throws SQLException {
        return courseDao.deleteCourse(id);
    }

    public List<Department> getDepartments() throws SQLException {
        return courseDao.getDepartments();
    }
}
