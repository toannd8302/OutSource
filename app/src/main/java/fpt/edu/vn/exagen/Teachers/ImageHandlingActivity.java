package fpt.edu.vn.exagen.Teachers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import fpt.edu.vn.exagen.APIService.ApiInterface;
import fpt.edu.vn.exagen.APIService.ApiResponse;
import fpt.edu.vn.exagen.APIService.RetrofitClient;
import fpt.edu.vn.exagen.R;
import fpt.edu.vn.exagen.Students.ImageDisplayActivity;
import fpt.edu.vn.exagen.Students.StudentInfo;
import fpt.edu.vn.exagen.adapter.CustomSpinnerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageHandlingActivity extends AppCompatActivity {

    private ImageView imageView;
    //private TextView textViewBase64;

    private ImageView imageViewResponse;
    private Bitmap receivedBitmap;

    private Spinner spAnswers;
    Bitmap scaledBitmap;

    //private TextView textViewResponseBase64;
    private ApiResponse apiResponse;
    private String resultStringResponse;
    private ArrayList<Integer> numberString;

    private ArrayList<Character> itemsAnswers;

    private Button btnSubmit, btnBack;
    private TextView tvTestDescription, tvStudentName, tvExamCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_handling);
        numberString = new ArrayList<>();
        itemsAnswers = new ArrayList<>();

        //textViewBase64 = findViewById(R.id.textViewBase64);
        imageViewResponse = findViewById(R.id.imageViewResponse);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvExamCode = findViewById(R.id.tvExamCode);
        tvTestDescription = findViewById(R.id.tvTestDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBackCam);
        //spAnswers = findViewById(R.id.spAnswers);
        Intent intent = getIntent();
        if (intent != null) {
            String imagePath = intent.getStringExtra(ImageDisplayActivity.EXTRA_IMAGE_PATH);
            String email = intent.getStringExtra("email");
            Log.d("ImageHandlingActivity", "Email: " + email);
            String examCode = intent.getStringExtra("examCode");
            Log.d("ImageHandlingActivity", "Exam Code: " + examCode);
            String testDescription = intent.getStringExtra("testDescription");
            Log.d("ImageHandlingActivity", "Test Description: " + testDescription);
            String examMarkId = intent.getStringExtra("examMarkId");
            Log.d("ImageHandlingActivity", "Exam Mark Id: " + examMarkId);
            String name = intent.getStringExtra("studentName");
            Log.d("ImageHandlingActivity", "Name: " + name);
            String studentId = intent.getStringExtra("studentId");
            Log.d("ImageHandlingActivity", "Student Id: " + studentId);
            if (imagePath != null) {
                // Hiển thị ảnh và base64Image trong ImageHandlingActivity
                displayImageAndBase64(imagePath);
            } else {
                Toast.makeText(this, "ImagePath rỗng", Toast.LENGTH_SHORT).show();
            }
            //TODO: set Text và Style cho các TextView
            tvStudentName.setText("Họ và Tên: " + name);
            tvStudentName.setTextColor(getResources().getColor(R.color.textColor, null));
            tvStudentName.setTypeface(null, Typeface.BOLD);
            tvStudentName.setTextSize(16);

            tvExamCode.setText("Mã HS: " + studentId);
            tvExamCode.setTextColor(getResources().getColor(R.color.textColor, null));
            tvExamCode.setTypeface(null, Typeface.BOLD);
            tvExamCode.setTextSize(16);

            tvTestDescription.setText("Mã đề thi: " + testDescription);
            tvTestDescription.setTextColor(getResources().getColor(R.color.textColor, null));
            tvTestDescription.setTypeface(null, Typeface.BOLD);
            tvTestDescription.setTextSize(16);


        }
    }

    private void displayImageAndBase64(String imagePath) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());


            //Ma hoa anh thanh base64
            String base64Image = encodeImageToBase64(myBitmap);

            // Gửi yêu cầu đến API
            sendRequestToApi(base64Image);

            //Xử lý sự kiện khi nhấn nút Luu
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gửi kết quả từ Spinner đến API
                    // Lấy đáp án từ Spinner
//                    String answer = spAnswers.getSelectedItem().toString();
//                    Log.d("Answer", "Answer: " + answer);
//                    // Gửi kết quả đến API
//                    sendResultToApi(answer);
                    Toast.makeText(ImageHandlingActivity.this, "Kết quả đã được gửi", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            Toast.makeText(this, "imagePath không tồn tại", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendResultToApi(String answer) {
    }

    //Ma hoa  anh base64
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    //Giải mã base64Image thành ảnh từ API
    private Bitmap decodeBase64ToImage(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void sendRequestToApi(String base64Image) {
        // Tạo JSON Object để chứa dữ liệu Base64
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("base64Image", base64Image);

        // In ra dữ liệu yêu cầu gửi đi
        Log.d("API Request", "Request Data: " + jsonObject.toString());

        // Sử dụng Retrofit để gửi yêu cầu POST đến API
        ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        // Truyền JsonObject thay vì RequestBody
        Call<ApiResponse> call = apiInterface.sendImage(jsonObject);

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

                // Xử lý ApiResponse ở đây (nếu cần)
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
            //Log kiểm tra kết quả
            Log.d("API Response", "Response Data Base64Image: " + apiResponse.getBase64Image());
            Log.d("API Response", "Response Data Result String: " + apiResponse.getResultString());
            resultStringResponse = apiResponse.getResultString();

            if (apiResponse != null) {
                // Giải mã base64Image thành ảnh và hiển thị ảnh
                Bitmap bitmap = decodeBase64ToImage(apiResponse.getBase64Image());

                // width
                int newWidth = bitmap.getWidth() * 3;
                // height
                int newHeight = bitmap.getHeight() * 3;


                // Tạo một bitmap mới có kích thước mới
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                receivedBitmap = scaledBitmap;
                imageViewResponse.setImageBitmap(scaledBitmap);
                displayImageViewAndSpinner();

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

    private void displayImageViewAndSpinner() {
        // Xử lý chuỗi kết quả từ API
        ArrayList<Object[]> answers = stringProcessing(resultStringResponse);
        for (Object[] answer : answers) {
            Log.d("Answer", "Answer: " + Arrays.toString(answer));
            numberString.add((Integer) answer[0]);
            Log.d("NumberString", "NumberString: " + numberString);

            // Lấy ký tự đầu tiên của phần tử thứ hai của mảng
            char firstChar = answer[1].toString().charAt(0);
            Log.d("FirstChar", "FirstChar: " + firstChar);
            itemsAnswers.add(firstChar);
        }
        // Khởi tạo ConstraintLayout
        ConstraintLayout constraintLayout = findViewById(R.id.parentLayout);

        // Tạo LinearLayout mới để chứa các cặp TextView và Spinner
        LinearLayout llMain = findViewById(R.id.llMain);
        llMain.setOrientation(LinearLayout.VERTICAL);

        // Tạo ImageView mới từ mã Java
        ImageView imageView = findViewById(R.id.imageViewResponse);
        imageView.setImageBitmap(receivedBitmap); // Đặt ảnh của ImageView




        for (int i = 0; i < numberString.size(); i++) {
            // Tạo một LinearLayout mới để chứa mỗi cặp TextView và Spinner
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // width
                    LinearLayout.LayoutParams.WRAP_CONTENT  // height
            );
            // Thiết lập margin cho LinearLayout
            int leftMargin = 20; // margin bên trái
            int topMargin = 10; // margin phía trên
            int rightMargin = 20; // margin bên phải
            int bottomMargin = 10; // margin phía dưới
            layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            // Gán LayoutParams cho LinearLayout
            rowLayout.setLayoutParams(layoutParams);
            // Tạo một TextView mới
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(10, 0, 10, 0);
            textView.setText(String.valueOf(i + 1) + ".");

            rowLayout.addView(textView);
            // Tạo một TextView mới
            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView1.setPadding(10, 0, 10, 0);
            textView1.setText(itemsAnswers.get(i).toString());

            rowLayout.addView(textView1);



            // Thêm LinearLayout vào LinearLayout chứa các cặp TextView và Spinner
            llMain.addView(rowLayout);
        }
    }

    private static ArrayList<Object[]> stringProcessing(String resultStringResponse) {
        String[] parts = resultStringResponse.split("\\|");

        // Tạo danh sách ArrayList để lưu trữ các mảng chuỗi kí tự và số nguyên
        ArrayList<Object[]> arraysList = new ArrayList<>();

        // Lặp qua từng phần và chia thành các chuỗi kí tự đơn lẻ
        for (String part : parts) {
            // Tạo một mảng con từ phần tử
            String[] subParts = part.split(":");

            // Lấy ký tự đầu tiên của phần tử thứ hai nếu có
            char character = subParts.length > 1 ? subParts[1].charAt(0) : 'A';

            // Kiểm tra độ dài của mảng con trước khi truy cập
            if (subParts.length > 1) {
                // Nếu mảng có ít nhất 2 phần tử, thêm mảng con vào danh sách
                arraysList.add(new Object[]{Integer.parseInt(subParts[0]), character});
            } else {
                // Nếu mảng chỉ có 1 phần tử hoặc không có phần tử nào, thêm một phần tử mới có giá trị mặc định "A"
                arraysList.add(new Object[]{Integer.parseInt(subParts[0]), 'A'});
            }
        }
        return arraysList;
    }



}
