package fpt.edu.vn.exagen.Students;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import java.util.List;

import fpt.edu.vn.exagen.R;

public class StudentListAdapter extends ArrayAdapter<StudentInfo> {
    private Context context;
    private List<StudentInfo> studentList;

    public StudentListAdapter(Context context, List<StudentInfo> studentList) {
        super(context, 0, studentList);
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Kiểm tra nếu convertView không được sử dụng, inflate một mới từ layout
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        }

        // Lấy thông tin học sinh tại vị trí position
        final StudentInfo studentInfo = studentList.get(position);

        // Hiển thị Tên HS và Mã HS
        TextView studentInfoTextViewName = convertView.findViewById(R.id.studentInfoName);
        TextView studentInfoTextViewNo = convertView.findViewById(R.id.studentInfoNo);
        String studentName = studentInfo.getStudentName();
        String studentNo = String.valueOf(studentInfo.getNo());

        // Tạo một SpannableString để đặt màu sắc cho "Tên HS"
        SpannableString spannableStringName = new SpannableString(studentName);
        // Đặt màu xanh lá cho văn bản của "Tên HS"
        spannableStringName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.studentName)),
                0, studentName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Tạo một SpannableString để đặt màu sắc cho "Mã HS"
        SpannableString spannableStringNo = new SpannableString("Mã HS: " + studentNo);
        // Đặt màu xám cho văn bản của "Mã HS"
        spannableStringNo.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 6 + studentNo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Độ dài của "Mã HS: " là 6

        // Đặt văn bản đã tùy chỉnh cho TextView
        studentInfoTextViewName.setText(spannableStringName);
        studentInfoTextViewNo.setText(spannableStringNo);

        return convertView;
    }

    // ViewHolder để lưu trữ các thành phần giao diện để tránh việc tìm kiếm lại
    private static class ViewHolder {
        TextView noTextView;
        TextView studentNameTextView;
        CheckBox studentCheckBox;
    }
}
