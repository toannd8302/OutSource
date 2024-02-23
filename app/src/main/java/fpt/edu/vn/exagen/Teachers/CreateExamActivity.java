package fpt.edu.vn.exagen.Teachers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import fpt.edu.vn.exagen.APIService.ApiInterface;
import fpt.edu.vn.exagen.APIService.ApiResponse;
import fpt.edu.vn.exagen.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateExamActivity extends AppCompatActivity {


    private ImageView imageView;
    private ImageButton btnBack;
    private ImageButton btnScanNewExam;


    private ImageView base64image;
    private TextView textViewResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_exam_layout);


        btnBack = findViewById(R.id.btnBackCam);
        btnScanNewExam = findViewById(R.id.btnScanNewExam);
        base64image = findViewById(R.id.base64image);
        textViewResponse = findViewById(R.id.textViewResponse);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackButtonClick(v);
            }
        });

        btnScanNewExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy ảnh từ drawable
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test1);

                // Chuyển ảnh thành Base64
                String base64Image = encodeImageToBase64(bitmap);

                // Gửi yêu cầu đến API
                sendRequestToApi(base64Image);
            }
        });
    }


    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void sendRequestToApi(String base64Image) {
        // Tạo JSON Object để chứa dữ liệu Base64
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("base64Image", base64Image);

        // In ra dữ liệu yêu cầu gửi đi
        Log.d("API Request", "Request Data: " + jsonObject.toString());

        // Sử dụng Retrofit để gửi yêu cầu POST đến API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://exagen.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);

        // Truyền JsonObject thay vì RequestBody
        Call<ApiResponse> call = apiService.sendImage(jsonObject);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // Chuyển đổi response bất đồng bộ sang đồng bộ
                ApiResponse apiResponse = null;
                try {
                    apiResponse = handleResponse(response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Xử lý ApiResponse ở đây
                if (apiResponse != null) {
                    String resultString = apiResponse.getResultString();

                    // Hiển thị resultString
                    new Handler(Looper.getMainLooper()).post(() -> {
                        textViewResponse.setText( resultString);
                        base64image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test1));
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Xử lý lỗi gửi yêu cầu
                Log.e("Error", "Error sending request to API: " + t.getMessage());
            }
        });
    }

    private ApiResponse handleResponse(Response<ApiResponse> response) throws IOException {
        Log.d("API Response", "Response Code: " + response.code());

        if (response.isSuccessful()) {
            // Xử lý kết quả thành công
            ApiResponse apiResponse = response.body();
            if (apiResponse != null) {
                return apiResponse;
            }
        } else {
            // Xử lý kết quả lỗi
            String errorBody = response.errorBody().string();
            Log.e("Error", "Error response from API: " + errorBody);

            // Thực hiện xử lý của bạn với thông báo lỗi trong errorBody
            // Ví dụ: chuyển đổi errorBody thành đối tượng ApiResponse để xử lý thông báo lỗi
            ApiResponse errorResponse = new Gson().fromJson(errorBody, ApiResponse.class);

            // Tiếp theo, bạn có thể truy cập thông báo lỗi từ errorResponse
            if (errorResponse != null) {
                String errorMessage = errorResponse.getResultString();
                // Xử lý thông báo lỗi ở đây
            }
        }

        return null;
    }
    public void onBackButtonClick(View view) {
        onBackPressed();
    }
}
