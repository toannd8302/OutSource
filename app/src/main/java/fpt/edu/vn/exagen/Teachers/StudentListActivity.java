package fpt.edu.vn.exagen.Teachers;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import fpt.edu.vn.exagen.APIService.ApiInterface;
import fpt.edu.vn.exagen.APIService.RetrofitClient;
import fpt.edu.vn.exagen.APIService.StudentInfoApiResponse;
import fpt.edu.vn.exagen.LoginActivity;
import fpt.edu.vn.exagen.R;
import fpt.edu.vn.exagen.Students.ImageDisplayActivity;
import fpt.edu.vn.exagen.Students.StudentInfo;
import fpt.edu.vn.exagen.Students.StudentListAdapter;
import fpt.edu.vn.exagen.Utils.FileUtils;
import fpt.edu.vn.exagen.Teachers.ImageHandlingActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentListActivity extends AppCompatActivity {
    private String examCode;
    private String email;
    private String testDescription;
    private String studentName; // Tên học sinh được chọn truyền qua activity ImageHandlingActivity
    private String examMarkId; // ID của bài thi được chọn truyền qua activity ImageHandlingActivity

    private String studentId; // ID của học sinh được chọn truyền qua activity ImageHandlingActivity

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int DISPLAY_IMAGE_REQUEST_CODE = 102;

    private TextView headerTextView;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        headerTextView = findViewById(R.id.headerTextView);
        exitButton = findViewById(R.id.exitButton);

        Intent intent = getIntent();
        if (intent != null) {
            examCode = intent.getStringExtra("examCode");
            email = intent.getStringExtra("email");

            Log.d("StudentListActivity", "Test Code: " + examCode);
            Log.d("StudentListActivity", "Email: " + email);

            ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);
            Call<StudentInfoApiResponse> call = apiInterface.getStudentInfo(examCode, email);
            call.enqueue(new Callback<StudentInfoApiResponse>() {
                @Override
                public void onResponse(Call<StudentInfoApiResponse> call, Response<StudentInfoApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        StudentInfoApiResponse studentInfoApiResponse = response.body();
                        Log.d("StudentListActivity", "API Response: " + studentInfoApiResponse.toString());
                        List<StudentInfo> studentList = studentInfoApiResponse.getStudentInExam();
                        testDescription = studentInfoApiResponse.getDescriptionOfTest();
                        headerTextView.setText(testDescription);
                        showStudentList(studentList);
                    } else {
                        Toast.makeText(StudentListActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<StudentInfoApiResponse> call, Throwable t) {
                    Toast.makeText(StudentListActivity.this, "Đã xảy ra lỗi" + t, Toast.LENGTH_SHORT).show();
                    Log.e("StudentListActivity", "Lỗi: " + t);
                }
            });
        }

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialog();
            }
        });
    }

    private void showStudentList(List<StudentInfo> studentList) {
        ListView listView = findViewById(R.id.listView);
        listView.setClickable(true);
        listView.setFocusable(true);

        StudentListAdapter adapter = new StudentListAdapter(this, studentList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("StudentListActivity", "Item Clicked at position: " + position);
                StudentInfo selectedStudent = studentList.get(position);
                studentName = selectedStudent.getStudentName();
                examMarkId = selectedStudent.getExamMarkId();
                studentId = selectedStudent.getStudentId();
                Intent intent = new Intent(StudentListActivity.this, ImageHandlingActivity.class);
                Toast.makeText(StudentListActivity.this, "Chọn học sinh: " + studentName, Toast.LENGTH_SHORT).show();
                checkCameraPermissionAndOpenCamera(selectedStudent);
            }
        });
    }

    private void checkCameraPermissionAndOpenCamera(StudentInfo selectedStudent) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera(selectedStudent);
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu quyền truy cập Camera được cấp, mở camera
                openCamera(null);
            } else {
                Toast.makeText(this, "Quyền truy cập Camera bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera(StudentInfo selectedStudent) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Không tìm thấy ứng dụng Camera trên thiết bị", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Xử lý hình ảnh đã chụp
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                // Lưu ảnh vào một tệp tin
                File imageFile = saveImageToFile(imageBitmap);

                // Bắt đầu một activity mới để hiển thị ảnh đã chụp
                displayCapturedImage(imageFile);
            }
        } else if (requestCode == DISPLAY_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Xử lý các hành động sau khi hiển thị ảnh (nếu cần)
        }
    }

    private File saveImageToFile(Bitmap imageBitmap) {
        File imageFile = null;
        try {
            imageFile = FileUtils.createImageFile(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    private void displayCapturedImage(File imageFile) {
        Intent displayImageIntent = new Intent(this, ImageHandlingActivity.class);
        displayImageIntent.putExtra(ImageDisplayActivity.EXTRA_IMAGE_PATH, imageFile.getAbsolutePath());
        displayImageIntent.putExtra("examCode", examCode);
        Log.d("StudentListActivity", "Exam Code: " + examCode);
        displayImageIntent.putExtra("email", email);
        Log.d("StudentListActivity", "Email: " + email);
        displayImageIntent.putExtra("testDescription", testDescription);
        Log.d("StudentListActivity", "Test Description: " + testDescription);
        displayImageIntent.putExtra("studentName", studentName);
        Log.d("StudentListActivity", "Student Name: " + studentName);
        displayImageIntent.putExtra("examMarkId", examMarkId);
        Log.d("StudentListActivity", "Exam Mark Id: " + examMarkId);
        displayImageIntent.putExtra("studentId", studentId);
        Log.d("StudentListActivity", "Student Id: " + studentId);
        startActivity(displayImageIntent);
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc muốn thoát?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        navigateToLogin();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(StudentListActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
