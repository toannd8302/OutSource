package fpt.edu.vn.exagen.APIService;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("base64Image")
    private String base64Image;
    @SerializedName("resultString")
    private String resultString;

    public String getBase64Image() {
        return base64Image;
    }
    public String getResultString() {
        return resultString;
    }
}
