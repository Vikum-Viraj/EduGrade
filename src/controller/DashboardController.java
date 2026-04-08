package controller;

import ui.CourseCrudFrame;
import ui.DepartmentCrudFrame;
import ui.EnrollmentCrudFrame;
import ui.LecturerCrudFrame;
import ui.StudentCrudFrame;

public class DashboardController {

    public void openDepartments() {
        new DepartmentCrudFrame(new DepartmentController()).setVisible(true);
    }

    public void openStudents() {
        new StudentCrudFrame(new StudentController()).setVisible(true);
    }

    public void openLecturers() {
        new LecturerCrudFrame(new LecturerController()).setVisible(true);
    }

    public void openCourses() {
        new CourseCrudFrame(new CourseController()).setVisible(true);
    }

    public void openEnrollments() {
        new EnrollmentCrudFrame(new EnrollmentController()).setVisible(true);
    }
}
