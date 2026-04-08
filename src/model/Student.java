package model;

public class Student {
    private int studentId;
    private String name;
    private String email;
    private String age;
    private String address;
    private String phone;
    private Integer departmentId;
    private String departmentName;

    public Student() {
    }

    public Student(int studentId, String name, String email, String age, String address, String phone,
            Integer departmentId, String departmentName) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.age = age;
        this.address = address;
        this.phone = phone;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        return studentId == 0 ? name : studentId + " - " + name;
    }
}
