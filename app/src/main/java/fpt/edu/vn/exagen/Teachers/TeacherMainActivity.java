package fpt.edu.vn.exagen.Teachers;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import fpt.edu.vn.exagen.R;

public class TeacherMainActivity extends AppCompatActivity {

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        // Load default fragment (no notifications)
        currentFragment = new EmptyFragment();
        loadFragment(currentFragment);

        ImageButton btnNotifications = findViewById(R.id.btnNotifications);
        ImageButton btnScan = findViewById(R.id.btnScanExam);
        ImageButton btnAccount = findViewById(R.id.btnAccount);

        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new TeacherNotiFragment());
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new ScanExamFragment());
            }
        });


    }

    private void switchFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Remove the current fragment
        transaction.remove(currentFragment);

        // Add the new fragment to the container
        transaction.add(R.id.container, newFragment);

        // Commit the transaction
        transaction.addToBackStack(null);
        transaction.commit();

        // Update the currentFragment
        currentFragment = newFragment;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace the initial fragment in the container
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
