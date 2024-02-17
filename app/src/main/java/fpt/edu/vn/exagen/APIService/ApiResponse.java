package fpt.edu.vn.exagen.APIService;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("resultString")
    private String resultString;

    public String getResultString() {
        return resultString;
    }
}
