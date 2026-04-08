package controller;

import java.sql.SQLException;
import java.util.List;

import dao.DepartmentDao;
import model.Department;

public class DepartmentController {
    private final DepartmentDao departmentDao;

    public DepartmentController() {
        this.departmentDao = new DepartmentDao();
    }

    public int createDepartment(String name) throws SQLException {
        return departmentDao.createDepartment(name);
    }

    public Department getDepartmentById(int id) throws SQLException {
        return departmentDao.getDepartmentById(id);
    }

    public List<Department> getAllDepartments() throws SQLException {
        return departmentDao.getAllDepartments();
    }

    public boolean updateDepartment(int id, String name) throws SQLException {
        return departmentDao.updateDepartment(id, name);
    }

    public boolean deleteDepartment(int id) throws SQLException {
        return departmentDao.deleteDepartment(id);
    }
}
