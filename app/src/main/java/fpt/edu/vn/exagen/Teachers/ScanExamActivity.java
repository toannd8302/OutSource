package fpt.edu.vn.exagen.Teachers;

// ScanExamActivity.java


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fpt.edu.vn.exagen.R;

public class ScanExamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_exam);

        // Add ScanExamFragment to the activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ScanExamFragment())
                    .commit();
        }
    }
}
