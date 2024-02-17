package fpt.edu.vn.exagen.Teachers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fpt.edu.vn.exagen.R;

public class ResultActivity extends AppCompatActivity {
    private ImageView resultImageView;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultImageView = findViewById(R.id.resultImageView);
        resultTextView = findViewById(R.id.resultTextView);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            String base64Image = intent.getStringExtra("BASE64_IMAGE");
            String resultString = intent.getStringExtra("RESULT_STRING");

            // Hiển thị hình ảnh và kết quả
            resultImageView.setImageBitmap(decodeBase64Image(base64Image));
            resultTextView.setText("API Result: " + resultString);
        }
    }


    private Bitmap decodeBase64Image(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
