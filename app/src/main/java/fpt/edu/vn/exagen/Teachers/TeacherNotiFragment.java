package fpt.edu.vn.exagen.Teachers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import fpt.edu.vn.exagen.R;

public class TeacherNotiFragment extends Fragment {

    public TeacherNotiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.teacher_noti_fragment, container, false);
    }
}