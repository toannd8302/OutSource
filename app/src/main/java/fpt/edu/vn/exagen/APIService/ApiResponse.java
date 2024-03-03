package fpt.edu.vn.exagen.APIService;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("Image")
    private String base64Image;
    @SerializedName("Result")
    private String resultString;
    @SerializedName("PaperCode")
    private String paperCode;
    @SerializedName("StudentNo")
    private String studentNo;

    public String getBase64Image() {
        return base64Image;
    }

    public String getResultString() {
        return resultString;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public String getStudentNo() {
        return studentNo;
    }


}
