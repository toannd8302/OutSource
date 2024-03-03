package fpt.edu.vn.exagen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fpt.edu.vn.exagen.APIService.ApiInterface;
import fpt.edu.vn.exagen.APIService.RetrofitClient;
import fpt.edu.vn.exagen.Teachers.StudentListActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText examCodeEditText;
    private Button startButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ các thành phần giao diện
        examCodeEditText = findViewById(R.id.editTextTestCode);
        emailEditText = findViewById(R.id.editTextEmail);
        startButton = findViewById(R.id.btnStartExam);
        progressBar = findViewById(R.id.progressBar);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String examCode = examCodeEditText.getText().toString();// Lấy thông tin từ EditText
                String email = emailEditText.getText().toString();
                //Show progress bar lúc bấm nút v thành công thì ẩn đi
                //validate
                showProgressBar(true);
                // Gọi hàm kiểm tra quyền chấm bài
                checkPermissionAndStartActivity(examCode, email);

            }
        });
    }


    private void showProgressBar(Boolean isLoading) {
        if (isLoading) {
            startButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            startButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void checkPermissionAndStartActivity(String examCode, String email) {
        try {
            // Kiểm tra xem người dùng đã nhập đủ thông tin chưa
            if (examCode.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã đề thi và email.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy instance của ApiInterface từ Retrofit
            ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

            // Gọi API để kiểm tra quyền
            Call<Boolean> call = apiInterface.checkPermission(examCode, email);

            call.enqueue(new Callback<Boolean>() {

                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("TAG", "response: " + response.toString());
                        boolean hasPermission = response.body();
                        if (hasPermission) {
                            // Nếu có quyền, chuyển sang màn hình StudentListActivity
                            Intent intent = new Intent(LoginActivity.this, StudentListActivity.class);
                            // Đính kèm thông tin vào Intent
                            intent.putExtra("examCode", examCode);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish(); // Đóng màn hình hiện tại
                        } else {
                            Toast.makeText(LoginActivity.this, "Bạn không có quyền chấm bài cho đề thi này.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Đã xảy ra lỗi khi kiểm tra quyền.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e("LoginActivity", "onFailure: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Đã xảy ra lỗi khi kết nối đến server.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            showProgressBar(false);
        }

    }
}
