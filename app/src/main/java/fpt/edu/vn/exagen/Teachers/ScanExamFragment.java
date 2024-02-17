// ScanExamActivity.java
package fpt.edu.vn.exagen.Teachers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import fpt.edu.vn.exagen.R;

public class ScanExamFragment extends Fragment {

    private PopupWindow popupWindow;

    public ScanExamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.exam_scanner, container, false);

        // More button
        ImageButton btnMore = view.findViewById(R.id.btnCreateFolder);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị pop-up tạo thư mục
                showCreateFolderPopup(v);
            }
        });

        // Plus button
        ImageButton btnAddExam = view.findViewById(R.id.btnAddExam);
        btnAddExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển Intent tới màn hình mới
                Intent intent = new Intent(getActivity(), CreateExamActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void showCreateFolderPopup(View view) {
        // Inflate the popup_layout.xml
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_layout, null);

        // Tạo PopupWindow
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Hiển thị PopupWindow tại vị trí của nút "More"
        popupWindow.showAsDropDown(view);
    }
}
