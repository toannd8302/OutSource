package fpt.edu.vn.exagen.Students;

import com.google.gson.annotations.SerializedName;

public class StudentInfo {
    @SerializedName("studentId")
    private String studentId;

    @SerializedName("name")
    private String studentName;

    @SerializedName("mark")
    private String mark;

    @SerializedName("no")
    private int no;

    public StudentInfo(String studentId, String studentName, String mark, int no) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.mark = mark;
        this.no = no;
    }
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getMark() {
        return mark;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}
