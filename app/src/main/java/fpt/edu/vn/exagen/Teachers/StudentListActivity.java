package fpt.edu.vn.exagen.Teachers;

import static fpt.edu.vn.exagen.Utils.FileUtils.createImageFile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private String studentName;
    private String examMarkId;
    private String studentId;
    private int studentNo;
    private String studentNoString;
    private String currentPhotoPath;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int DISPLAY_IMAGE_REQUEST_CODE = 102;
    private TextView headerTextView;
    private boolean stateColor = false;
    private Button exitButton;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        setupActionBar(); // Thiết lập action bar
        headerTextView = findViewById(R.id.headerTextView);
        exitButton = findViewById(R.id.exitButton);
        Intent intent = getIntent();
        if (intent != null) {
            examCode = intent.getStringExtra("examCode");
            email = intent.getStringExtra("email");
            stateColor = intent.getBooleanExtra("changeStateColor", false);
            Log.d("StudentListActivity", "stateColor: " + stateColor);
            Log.d("StudentListActivity", "Test Code: " + examCode);
            Log.d("StudentListActivity", "Email: " + email);
            callApiShowStudentList();//Gọi API để hiển thị danh sách học sinh
        }

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialog();
            }
        });
    }

    private void callApiShowStudentList() {
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

    private void setupActionBar() {
        Toolbar appBar = findViewById(R.id.appbar);
        TextView title = appBar.findViewById(R.id.txtAppBarTitle);
        title.setText("Quay lại");
        setSupportActionBar(appBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true); //Hiển thị nút back
            actionBar.setDisplayShowTitleEnabled(false);//Ẩn tiêu đề của action bar
        }
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
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
        if (stateColor) {
            //Thqau màu cho relative layout
            RelativeLayout relativeLayoutStudent = findViewById(R.id.relativeLayoutStudent);
            // Kiểm tra xem relativeLayoutStudent không phải là null trước khi thiết lập màu nền
            if (relativeLayoutStudent != null) {
                relativeLayoutStudent.setBackgroundColor(getResources().getColor(R.color.backgroundButton));
            } else {
                Log.e("Error", "RelativeLayout is null");
            }
            CheckBox checkBox = findViewById(R.id.studentCheckBox);
            if (checkBox != null) {
                checkBox.setButtonDrawable(R.drawable.custom_checkbox);
            } else {
                Log.e("Error", "CheckBox is null");
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("StudentListActivity", "Item Clicked at position: " + position);
                StudentInfo selectedStudent = studentList.get(position);
                studentName = selectedStudent.getStudentName();
                Log.d("StudentListActivity", "Student Name: " + studentName);
                examMarkId = selectedStudent.getExamMarkId();
                Log.d("StudentListActivity", "Exam Mark Id: " + examMarkId);
                studentId = selectedStudent.getStudentId();
                Log.d("StudentListActivity", "Student Id: " + studentId);
                 studentNo = selectedStudent.getNo();
                studentNoString = String.valueOf(studentNo);
                Intent intent = new Intent(StudentListActivity.this, ImageHandlingActivity.class);
                intent.putExtra("studentName", studentName);
                intent.putExtra("examMarkId", examMarkId);
                intent.putExtra("examCode", examCode);
                intent.putExtra("email", email);
                intent.putExtra("testDescription", testDescription);
                intent.putExtra("studentId", studentId);
                intent.putExtra("studentNo", studentNoString);
                Log.d("StudentListActivity", "studentNoString: " + studentNoString);
                Toast.makeText(StudentListActivity.this, "Chọn học sinh: " + studentName, Toast.LENGTH_SHORT).show();
                checkCameraPermissionAndOpenCamera(selectedStudent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadStudentList();
    }

    private void loadStudentList() {

        Intent intent = getIntent();
        if (intent != null) {
            examCode = intent.getStringExtra("examCode");
            email = intent.getStringExtra("email");
            Log.d("StudentListActivity", "Test Code sau khi quay về từ ImageHandling: " + examCode);
            Log.d("StudentListActivity", "Email sau khi quay về từ ImageHandling:: " + email);
        }
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
                openCamera(null);
            } else {
                Toast.makeText(this, "Quyền truy cập Camera bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera(StudentInfo selectedStudent) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                currentPhotoPath = imageFile.getAbsolutePath();
                Uri imageUri = FileProvider.getUriForFile(this, "fpt.edu.vn.exagen.fileprovider", imageFile);
                Log.d("StudentListActivity", "URI của ảnh: " + imageUri);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                if (selectedStudent != null) {
                    cameraIntent.putExtra("studentId", selectedStudent.getStudentId());
                }
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        } else {
            Toast.makeText(this, "Không thể mở camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (currentPhotoPath != null) {
                File imageFile = new File(currentPhotoPath);
                if (imageFile.exists()) {
                    Log.d("StudentListActivity", "Đường dẫn ảnh: " + imageFile.getAbsolutePath());
                    saveImageToFile(Uri.fromFile(imageFile));
                    displayCapturedImage(imageFile);
                } else {
                    Toast.makeText(this, "Không thể tìm thấy tệp ảnh", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Đường dẫn ảnh không khả dụng", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == DISPLAY_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Xử lý các hành động sau khi hiển thị ảnh (nếu cần)
        }
    }

    private File saveImageToFile(Uri imageUri) {
        File imageFile = null;
        try {
            imageFile = createImageFile(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
             FileOutputStream fos = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Kiểm tra kích thước tệp sau khi lưu
        long fileSize = imageFile.length();
        Log.d("StudentListActivity", "Kích thước tệp sau khi lưu: " + fileSize + " bytes");

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
