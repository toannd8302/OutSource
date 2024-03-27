package fpt.edu.vn.exagen.Teachers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fpt.edu.vn.exagen.APIService.ApiInterface;
import fpt.edu.vn.exagen.APIService.ApiResponse;
import fpt.edu.vn.exagen.APIService.RetrofitClient;
import fpt.edu.vn.exagen.APIService.SendRequestTasks;
import fpt.edu.vn.exagen.R;
import fpt.edu.vn.exagen.Students.ImageDisplayActivity;
import fpt.edu.vn.exagen.adapter.AnswerSpinnerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageHandlingActivity extends AppCompatActivity {

    private ImageView imageViewResponse;
    private Bitmap receivedBitmap;
    private Button btnSubmit, btnBack;
    private TextView tvStudentName;
    private String studentNo;
    private String examMarkId;
    private int paperCode;
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

    String studentNoFromSudentList;
    private ArrayList<Integer> numberString;

    private ArrayList<Character> itemsAnswers;

    private EditText edtPaperCode;
    private TextView tvStudentNo, tvStudentNameTextView, tvPaperCode;
    private ProgressBar progressBarResult, progressBarStudentNo, progressBarPaperCode;
    Character selectedAnswer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_handling);
        imageViewResponse = findViewById(R.id.imageViewResponse);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentNo = findViewById(R.id.tvStudentNo);
        tvPaperCode = findViewById(R.id.tvPaperCode);
        edtPaperCode = findViewById(R.id.edtPaperCode);
        btnSubmit = findViewById(R.id.btnSummit);
        btnBack = findViewById(R.id.btnBack);
        progressBarResult = findViewById(R.id.progressBarResult);
        progressBarStudentNo = findViewById(R.id.progressBarStudentNo);
        progressBarPaperCode = findViewById(R.id.progressBarPaperCode);
        Intent intent = getIntent();
        if (intent != null) {
            String imagePath = intent.getStringExtra(ImageDisplayActivity.EXTRA_IMAGE_PATH);
            email = intent.getStringExtra("email");
            Log.d("ImageHandlingActivity", "email: " + email);
            examCode = intent.getStringExtra("examCode");
            Log.d("ImageHandlingActivity", "examCode: " + examCode);
            String testDescription = intent.getStringExtra("testDescription");
            Log.d("ImageHandlingActivity", "testDescription: " + testDescription);
            examMarkId = intent.getStringExtra("examMarkId");
            Log.d("ImageHandlingActivity", "examMarkId: " + examMarkId);
            String name = intent.getStringExtra("studentName");
            studentNoFromSudentList = intent.getStringExtra("studentNo");
            Log.d("ImageHandlingActivity", "studentNo: " + studentNoFromSudentList);
            if (imagePath != null) {
                displayImageAndBase64(imagePath);
            } else {
                Toast.makeText(this, "ImagePath rỗng", Toast.LENGTH_SHORT).show();
            }
            tvStudentName.setText("Họ và Tên: " + name);
            tvStudentName.setTextColor(getResources().getColor(R.color.textColor));
            tvStudentName.setTypeface(null, Typeface.BOLD);
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

    // Hiển thị ảnh và mã học sinh, mã đề thi
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
                            sendRequestBase64Image(base64Image);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() { // Hiển thị thông báo nếu imagePath không tồn tại
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
        // Cập nhật mã đề thi + 1 vì mã đề thi bắt đầu từ 0 còn mã đề thi bắt đầu từ 1
        jsonObject.addProperty("paperCode", Integer.parseInt(edtPaperCode.getText().toString()));
        jsonObject.addProperty("answersSelected", generateResultString(itemsAnswers));
        jsonObject.addProperty("examMarkId", examMarkId);
        Log.d("ImageHandlingActivity", "jsonObjec: " + jsonObject.toString());
        ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);
        Call<Boolean> call = apiInterface.saveResult(jsonObject);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.d("ImageHandlingActivity", "Save result response: " + response.body());
                Log.d("ImageHandlingActivity", "Save result response message : " + response.message());
                Log.d("ImageHandlingActivity", "Save result response code: " + response.code());
                if (response.body() != null) {
                    if (response.body()) {
                        Toast.makeText(ImageHandlingActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                        navigateToStudentList();

                    } else {
                        Toast.makeText(ImageHandlingActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ImageHandlingActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ImageHandlingActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void sendRequestBase64Image(String base64Image) {
        try {
            ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);
            SendRequestTasks.SendRequestListener listener = new SendRequestTasks.SendRequestListener() {
                @Override
                public void onRequestSuccess(ApiResponse response) {
                    try {
                        progressBarResult.setVisibility(View.VISIBLE);
                        progressBarStudentNo.setVisibility(View.VISIBLE);
                        progressBarPaperCode.setVisibility(View.VISIBLE);
                        handleResponse(response);

                        // Kiểm tra nếu response không rỗng và studentNoFromSudentList không rỗng
                        if (response != null && response.getStudentNo() != null) {
                            // Kiểm tra nếu studentNo không rỗng
                            if (!TextUtils.isEmpty(studentNo) && studentNo.equals(response.getStudentNo())) {
                                tvStudentNo.setText("Mã học sinh: " + studentNo);
                                tvStudentNo.setTextColor(getResources().getColor(R.color.textColor));
                                tvStudentNo.setTypeface(null, Typeface.BOLD);
                            } else {
                                //Hiển thị dialog Y/N
                                showConfirmationDialog();
                            }
                        } else {
                            // Xử lý trường hợp response hoặc studentNoFromSudentList là null
                            Toast.makeText(ImageHandlingActivity.this, "Dữ liệu không hợp lệ", Toast.LENGTH_LONG).show();
                            Log.e("Error", "Dữ liệu không hợp lệ");
                            progressBarResult.setVisibility(View.GONE);
                            onBackPressed();
                            return; // Thoát khỏi phương thức nếu dữ liệu không hợp lệ
                        }


                        edtPaperCode.setText(String.valueOf(paperCode));
                        edtPaperCode.setTextColor(getResources().getColor(R.color.textColor));
                        edtPaperCode.setTypeface(null, Typeface.BOLD);

                        Log.d("ImageHandlingActivity", "paperCode: " + paperCode);
                        progressBarStudentNo.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ImageHandlingActivity.this, "Lỗi ở xử lí studentNo : " + e, Toast.LENGTH_LONG).show();
                        Log.e("Error", "Lỗi ở xử lí studentNo : " + e);
                        progressBarResult.setVisibility(View.GONE);
                        onBackPressed();
                    }
                }

                @Override
                public void onRequestFailure() {
                    Toast.makeText(ImageHandlingActivity.this, "Failed to send request to API", Toast.LENGTH_SHORT).show();
                    Log.e("Error", "Failed to send request to API");
                    onBackPressed();
                    progressBarStudentNo.setVisibility(View.GONE);
                    progressBarPaperCode.setVisibility(View.GONE);
                }
            };
            SendRequestTasks sendRequestTask = new SendRequestTasks(apiInterface, listener);
            sendRequestTask.execute(base64Image);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi ở sendRequestBase64Image : " + e, Toast.LENGTH_LONG).show();
            Log.e("Error", "Lỗi ở sendRequestBase64Image : " + e);
            onBackPressed();
        }

    }

    private void handleResponse(ApiResponse response) {
        try {
            if (response != null) {
                Log.d("ImageHandlingActivity", "responseStudentNo " + response.getStudentNo());
                studentNo = response.getStudentNo();

                paperCode = response.getPaperCode();
                edtPaperCode.setText(String.valueOf(paperCode));
                //set giá trị cho paperCode

                // Thực hiện kiểm tra và xử lý chuỗi paperCode từ edtPaperCode
                String paperCodeString = edtPaperCode.getText().toString().trim();
                if (!TextUtils.isEmpty(paperCodeString)) {
                    paperCode = Integer.parseInt(paperCodeString);
                } else {

                    Toast.makeText(ImageHandlingActivity.this, "Mã đề thi không được để trống", Toast.LENGTH_SHORT).show();
                    return; // Thoát khỏi phương thức nếu mã đề thi rỗng
                }

                resultStringResponse = response.getResultString();
                Log.d("ImageHandlingActivity", "resultStringResponse: " + resultStringResponse);

                // Giải mã base64Image thành ảnh và hiển thị ảnh
                receivedBitmap = decodeBase64ToImage(response.getBase64Image());
                // Hiển thị ảnh và spinner
                displayImageViewAndSpinner();
            } else {
                Toast.makeText(this, "response rỗng", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi ở handleResponse" + e, Toast.LENGTH_LONG).show();
            Log.e("Error", "Lỗi ở handleResponse" + e);
            onBackPressed();
        }
    }


    private void displayImageViewAndSpinner() {
        try {
            // Xử lý chuỗi kết quả từ API
            ArrayList<Object[]> answers = stringProcessing(resultStringResponse);
            numberString = new ArrayList<>();
            itemsAnswers = new ArrayList<>();
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
            if (llMain.getParent() != null) { // Xóa LinearLayout cũ nếu đã tồn tại
                ((ViewGroup) llMain.getParent()).removeView(llMain);
            } else {
                llMain = new LinearLayout(this);
            }

            llMain.setOrientation(LinearLayout.VERTICAL);
            ImageView imageView = findViewById(R.id.imageViewResponse);
            imageView.setImageBitmap(receivedBitmap);
            for (int i = 0; i < numberString.size(); i++) {
                // Tạo một biến cuối cùng để lưu trữ giá trị của i để dành mục đích cập nhât đáp án người dùng chọn tại vị trí i
                //Vì i = 0 nến các vị trí sẽ bị -1 so với bình thường
                //Nêu cap nhat cau 120 => i = 119
                //Nêu cap nhat cau 1 => i = 0
                final int index = i;
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int leftMargin = 20;
                int topMargin = 5;
                int rightMargin = 20;
                int bottomMargin = 5;
                layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                rowLayout.setLayoutParams(layoutParams);
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)

                );
                textView.setPadding(10, 0, 10, 0);
                textView.setText(String.valueOf(i + 1) + ".");
                textView.setTypeface(null, Typeface.BOLD);
                textView.setTextColor(getResources().getColor(R.color.textColor));
                rowLayout.addView(textView);
                Spinner spinner = new Spinner(this);
                spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                spinner.setAdapter(new AnswerSpinnerAdapter(this, R.layout.item_answers, itemsAnswers, itemsAnswers.get(i).charValue()));
                Log.d("SelectedAnswer", "SelectedAnswer1: " + itemsAnswers.get(i));
                if (itemsAnswers.get(i) == 'A') {
                    spinner.setSelection(0);
                } else if (itemsAnswers.get(i) == 'B') {
                    spinner.setSelection(1);
                } else if (itemsAnswers.get(i) == 'C') {
                    spinner.setSelection(2);
                } else if (itemsAnswers.get(i) == 'D') {
                    spinner.setSelection(3);
                } else {
                    spinner.setSelection(0);
                }
                //Cứ cách 5 câu hỏi thì margin bottom = 20
                if (i != 0 && (i + 1) % 5 == 0) {
                    layoutParams.setMargins(leftMargin, topMargin, rightMargin, 20);
                }

                Log.d("SelectedAnswer", "SelectedAnswerBeforeSubmit: " + selectedAnswer);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Character selectedAnswer = (Character) parent.getItemAtPosition(position);
                        Log.d("SelectedAnswer", "SelectedAnswerAfterSubmit: " + selectedAnswer);
                        itemsAnswers.set(index, selectedAnswer);

                        Log.d("SelectedAnswer", "SelectedAnswer: " + selectedAnswer);
                        Log.d("Position", "Position: " + index);
                        Log.d("SelectedAnswer", "SelectedAnswerListUpdate: " + itemsAnswers);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                rowLayout.addView(spinner);
                llMain.addView(rowLayout);
            }

            constraintLayout.addView(llMain);
            progressBarResult.setVisibility(View.GONE);
            progressBarStudentNo.setVisibility(View.GONE);
            progressBarPaperCode.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi ở displayImageViewAndSpinner : " + e, Toast.LENGTH_LONG).show();
            Log.e("Error", "Lỗi ở displayImageViewAndSpinner : " + e);
            progressBarResult.setVisibility(View.GONE);
            progressBarStudentNo.setVisibility(View.GONE);
            progressBarPaperCode.setVisibility(View.GONE);
            onBackPressed();
        }
    }


    private Bitmap decodeBase64ToImage(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void navigateToStudentList() {
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
        // Kiểm tra và loại bỏ dấu | cuối cùng
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

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận");
        builder.setMessage("Mã học sinh không khớp với học sinh đã chọn. Thầy/cô có muốn vẫn giữ học sinh đã chọn ?");
        builder.setCancelable(false);// Không cho phép click ra ngoài dialog để đóng dialog
        // Nếu người dùng chọn Yes
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Hiển thị mã học sinh từ studentList
                tvStudentNo.setText("Mã học sinh: " + studentNoFromSudentList);

                tvStudentNo.setTextColor(getResources().getColor(R.color.textColor));
                tvStudentNo.setTypeface(null, Typeface.BOLD);
                dialog.dismiss(); // Đóng dialog
            }
        });

        // Nếu người dùng chọn No

        builder.setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nếu người dùng chọn Nothi2i quay lại
                onBackPressed();
                dialog.dismiss(); // Đóng dialog
            }
        });
        // Tạo và hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        // Tùy chỉnh màu chữ cho nút Yes và No sau khi dialog được hiển thị
        Button buttonYes = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button buttonNo = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (buttonYes != null && buttonNo != null) {
            buttonYes.setTextColor(getResources().getColor(R.color.backgroundButton)); // Màu cho nút Yes
            buttonNo.setTextColor(getResources().getColor(R.color.red)); // Màu cho nút No
        }
    }

    private String generateResultString(ArrayList<Character> itemsAnswers) {
        StringBuilder resultStringBuilder = new StringBuilder();
        for (int i = 0; i < itemsAnswers.size(); i++) {
            if (itemsAnswers.get(i) != null && itemsAnswers.get(i) != ' ') {
                resultStringBuilder.append((i + 1)).append(":").append(itemsAnswers.get(i)).append("|");
            } else {
                resultStringBuilder.append((i + 1)).append(":").append("|");
            }
        }
        if (resultStringBuilder.length() > 0) {
            resultStringBuilder.deleteCharAt(resultStringBuilder.length() - 1);
        }
        return resultStringBuilder.toString();
    }
}
