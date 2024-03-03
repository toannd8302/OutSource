package fpt.edu.vn.exagen.Teachers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import fpt.edu.vn.exagen.APIService.ApiInterface;
import fpt.edu.vn.exagen.APIService.ApiResponse;
import fpt.edu.vn.exagen.APIService.RetrofitClient;
import fpt.edu.vn.exagen.R;
import fpt.edu.vn.exagen.Students.ImageDisplayActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageHandlingActivity extends AppCompatActivity {

    private ImageView imageViewResponse;
    private Bitmap receivedBitmap;
    private Button btnSubmit, btnBack;
    private TextView tvTestDescription, tvStudentName, tvExamCode;
    private String studentNo;
    private String examMarkId;
    private String paperCode;
    private String resultStringResponse;
    private String examCode;
    private String email;
    private ImageView imageView;
    //private TextView textViewBase64;

    private ImageView imageViewResponseView;
    private Bitmap receivedBitmapBitmap;

    private Spinner spAnswers;
    private String examMarkIdString;
    Bitmap scaledBitmap;

    //private TextView textViewResponseBase64;
    private ApiResponse apiResponse;
    private String resultStringResponseString;
    private String examCodeString;
    private String emailString;
    private ArrayList<Integer> numberString;

    private ArrayList<Character> itemsAnswers;

    private Button btnSubmitButton, btnBackButton;
    private TextView tvStudentNo, tvStudentNameTextView, tvPaperCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_handling);

        imageViewResponse = findViewById(R.id.imageViewResponse);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentNo = findViewById(R.id.tvStudentNo);
        tvPaperCode = findViewById(R.id.tvPaperCode);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBackCam);

        Intent intent = getIntent();

        if (intent != null) {
            String imagePath = intent.getStringExtra(ImageDisplayActivity.EXTRA_IMAGE_PATH);
            email = intent.getStringExtra("email");
            examCode = intent.getStringExtra("examCode");
            String testDescription = intent.getStringExtra("testDescription");
            examMarkId = intent.getStringExtra("examMarkId");
            String name = intent.getStringExtra("studentName");

            if (imagePath != null) {
                // Hiển thị ảnh và base64Image trong ImageHandlingActivity
                displayImageAndBase64(imagePath);
            } else {
                Toast.makeText(this, "ImagePath rỗng", Toast.LENGTH_SHORT).show();
            }

            // Set Text và Style cho các TextView
            tvStudentName.setText("Họ và Tên: " + name);

        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lí API
                sendResultToApi(resultStringResponse);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trở về màn hình trước đó
                onBackPressed();
            }
        });
    }

    private void displayImageAndBase64(final String imagePath) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    final String base64Image = encodeImageToBase64(myBitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendRequestToApi(base64Image);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ImageHandlingActivity.this, "imagePath không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void sendResultToApi(String answer) {
        // Tạo JSON Object để chứa dữ liệu cần gửi đến API
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("paperCode", paperCode);
        jsonObject.addProperty("answersSelected", answer);
        jsonObject.addProperty("examMarkId", examMarkId);


        // Sử dụng Retrofit để gửi yêu cầu POST đến API
        ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);
        Call<Double> call = apiInterface.saveResult(jsonObject);

        // Thực hiện cuộc gọi bất đồng bộ và xử lý kết quả
        call.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful()) {
                    Double result = response.body();
                    if (result != null) {
                        // Điều hướng đến màn hình danh sách sinh viên và hiển thị thông báo
                        navigateToStudentList(result);
                    }
                } else {
                    Log.e("Error", "Unsuccessful response from API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                // Xử lý lỗi khi gửi yêu cầu
                Log.e("Error", "Error sending request to API: " + t.getMessage());
            }
        });
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private void sendRequestToApi(String base64Image) {
        ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        SendRequestTask.SendRequestListener listener = new SendRequestTask.SendRequestListener() {
            @Override
            public void onRequestSuccess(ApiResponse response) {
                handleResponse(response);
                tvStudentNo.setText("Mã HS: " + studentNo);
                Log.d("ImageHandlingActivity", "studentNo: " + studentNo);
                tvPaperCode.setText("Mã đề thi: " + paperCode);
                Log.d("ImageHandlingActivity", "paperCode: " + paperCode);
            }

            @Override
            public void onRequestFailure() {
                // Xử lý trường hợp gửi yêu cầu thất bại
                Toast.makeText(ImageHandlingActivity.this, "Failed to send request to API", Toast.LENGTH_SHORT).show();
                Log.e("Error", "Failed to send request to API");
            }
        };

        SendRequestTask sendRequestTask = new SendRequestTask(apiInterface, listener);
        sendRequestTask.execute(base64Image);
    }

    private void handleResponse(ApiResponse response) {

        if (response != null) {
            Log.d("ImageHandlingActivity", "responseStudentNo " + response.getStudentNo());
            studentNo = response.getStudentNo();
            Log.d("ImageHandlingActivity", "responsePaperCode: " + response.getPaperCode());
            paperCode = response.getPaperCode();
            resultStringResponse = response.getResultString();
            Log.d("ImageHandlingActivity", "resultStringResponse: " + resultStringResponse);

            // Giải mã base64Image thành ảnh và hiển thị ảnh
            receivedBitmap = decodeBase64ToImage(response.getBase64Image());
            //
            // Hiển thị ảnh và spinner
            displayImageViewAndSpinner();
        }
    }

    private void displayImageViewAndSpinner() {
        // Xử lý chuỗi kết quả từ API
        ArrayList<Object[]> answers = stringProcessing(resultStringResponse);
        ArrayList<Integer> numberString = new ArrayList<>();
        ArrayList<Character> itemsAnswers = new ArrayList<>();
        for (Object[] answer : answers) {
            Log.d("Answer", "Answer: " + Arrays.toString(answer));
            numberString.add((Integer) answer[0]);
            Log.d("NumberString", "NumberString: " + numberString);

            char firstChar = answer[1].toString().charAt(0);
            Log.d("FirstChar", "FirstChar: " + firstChar);
            itemsAnswers.add(firstChar);
        }
        ConstraintLayout constraintLayout = findViewById(R.id.parentLayout);
        // Tạo LinearLayout mới để chứa các cặp TextView và Spinner
        LinearLayout llMain = findViewById(R.id.llMain);
        llMain.setOrientation(LinearLayout.VERTICAL);
        ImageView imageView = findViewById(R.id.imageViewResponse);
        imageView.setImageBitmap(receivedBitmap);
        for (int i = 0; i < numberString.size(); i++) {
            // Tạo một LinearLayout mới để chứa mỗi cặp TextView và Spinner
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            // Thiết lập margin cho LinearLayout
            int leftMargin = 20;
            int topMargin = 10;
            int rightMargin = 20;
            int bottomMargin = 10;
            layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            rowLayout.setLayoutParams(layoutParams);
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(10, 0, 10, 0);
            textView.setText(String.valueOf(i + 1) + ".");
            rowLayout.addView(textView);
            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView1.setPadding(10, 0, 10, 0);
            textView1.setText(itemsAnswers.get(i).toString());
            rowLayout.addView(textView1);
            // Thêm LinearLayout vào LinearLayout chứa các cặp TextView và Spinner
            llMain.addView(rowLayout);
        }
    }

    private Bitmap decodeBase64ToImage(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void navigateToStudentList(Double result) {
        // Điều hướng đến màn hình danh sách sinh viên và hiển thị thông báo
        boolean changeStateColor = true;
        Intent intent = new Intent(ImageHandlingActivity.this, StudentListActivity.class);
        intent.putExtra("examCode", examCode);
        intent.putExtra("email", email);
        intent.putExtra("changeStateColor", changeStateColor);
        startActivity(intent);
        Toast.makeText(ImageHandlingActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();

    }


    //xử lí chuỗi kết quả từ API
    private static ArrayList<Object[]> stringProcessing(String resultStringResponse) {
        // Kiểm tra và loại bỏ dấu | cuối cùng nếu có
        if (resultStringResponse.endsWith("|")) {
            resultStringResponse = resultStringResponse.substring(0, resultStringResponse.length() - 1);
        }

        String[] parts = resultStringResponse.split("\\|");
        ArrayList<Object[]> arraysList = new ArrayList<>();

        for (String part : parts) {
            String[] subParts = part.split(":");
            int questionNumber = Integer.parseInt(subParts[0]);
            char answer = subParts.length > 1 ? subParts[1].charAt(0) : 'A';
            arraysList.add(new Object[]{questionNumber, answer});
        }
        return arraysList;
    }

}
