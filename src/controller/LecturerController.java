package controller;

import java.sql.SQLException;
import java.util.List;

import dao.LecturerDao;
import model.Department;
import model.Lecturer;

public class LecturerController {
    private final LecturerDao lecturerDao;

    public LecturerController() {
        this.lecturerDao = new LecturerDao();
    }

    public int createLecturer(Lecturer lecturer) throws SQLException {
        return lecturerDao.createLecturer(lecturer);
    }

    public Lecturer getLecturerById(int id) throws SQLException {
        return lecturerDao.getLecturerById(id);
    }

    public List<Lecturer> getAllLecturers() throws SQLException {
        return lecturerDao.getAllLecturers();
    }

    public boolean updateLecturer(Lecturer lecturer) throws SQLException {
        return lecturerDao.updateLecturer(lecturer);
    }

    public boolean deleteLecturer(int id) throws SQLException {
        return lecturerDao.deleteLecturer(id);
    }

    public List<Department> getDepartments() throws SQLException {
        return lecturerDao.getDepartments();
    }
}
