package controller;

import java.sql.SQLException;
import java.util.List;

import dao.StudentDao;
import model.Department;
import model.Student;

public class StudentController {
    private final StudentDao studentDao;

    public StudentController() {
        this.studentDao = new StudentDao();
    }

    public int createStudent(Student student) throws SQLException {
        return studentDao.createStudent(student);
    }

    public Student getStudentById(int id) throws SQLException {
        return studentDao.getStudentById(id);
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDao.getAllStudents();
    }

    public boolean updateStudent(Student student) throws SQLException {
        return studentDao.updateStudent(student);
    }

    public boolean deleteStudent(int id) throws SQLException {
        return studentDao.deleteStudent(id);
    }

    public List<Department> getDepartments() throws SQLException {
        return studentDao.getDepartments();
    }
}
