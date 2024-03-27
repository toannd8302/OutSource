package fpt.edu.vn.exagen.APIService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("api/v0/exams/send-image")
    Call<ApiResponse> sendImage(@Body JsonObject base64Image);
    @POST("api/v0/exams/save-result")
    Call<Boolean> saveResult(@Body JsonObject result);

    @GET("api/v0/exams/check-permission/{examCode}/{email}")
    Call<Boolean> checkPermission(
            @Path("examCode") String examCode,
            @Path("email") String email
    );
    @GET("api/v0/exams/access-exam/{examCode}/{email}")
    Call<StudentInfoApiResponse> getStudentInfo(
            @Path("examCode") String examCode,
            @Path("email") String email
    );
}
