package fpt.edu.vn.exagen.Students;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import fpt.edu.vn.exagen.R;

public class ImageDisplayActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_PATH = "extra_image_path";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        imageView = findViewById(R.id.imageView);

        // Nhận đường dẫn của hình ảnh từ Intent
        String imagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);

        // Hiển thị hình ảnh trên ImageView
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }
}
