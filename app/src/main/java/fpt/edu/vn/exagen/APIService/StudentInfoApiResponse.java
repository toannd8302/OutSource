package fpt.edu.vn.exagen.APIService;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import fpt.edu.vn.exagen.Students.StudentInfo;

public class StudentInfoApiResponse {

    @SerializedName("testCode")
    private int testCode;

    @SerializedName("studentInExam")
    private List<StudentInfo> studentInExam;

    @SerializedName("descriptionOfTest")
    private String descriptionOfTest;

    public int getTestCode() {
        return testCode;
    }

    public List<StudentInfo> getStudentInExam() {
        return studentInExam;
    }

    public String getDescriptionOfTest() {
        return descriptionOfTest;
    }
}
