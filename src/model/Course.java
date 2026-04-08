package model;

public class Course {
    private int courseId;
    private String courseName;
    private int credits;
    private Integer departmentId;
    private String departmentName;

    public Course() {
    }

    public Course(int courseId, String courseName, int credits, Integer departmentId, String departmentName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return courseId == 0 ? courseName : courseId + " - " + courseName;
    }
}
