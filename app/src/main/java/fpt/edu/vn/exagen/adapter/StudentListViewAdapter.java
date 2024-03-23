package fpt.edu.vn.exagen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

import fpt.edu.vn.exagen.R;
import fpt.edu.vn.exagen.Students.StudentInfo;

public class StudentListViewAdapter extends ArrayAdapter<StudentInfo>{
    private Context context;
    private List<StudentInfo> studentList;
    private boolean stateColor;

    public StudentListViewAdapter(@NonNull Context context, List<StudentInfo> studentList, boolean stateColor) {
        super(context, 0, studentList);
        this.context = context;
        this.studentList = studentList;
        this.stateColor = stateColor;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.relativeLayoutStudent = convertView.findViewById(R.id.relativeLayoutStudent);
            viewHolder.checkBox = convertView.findViewById(R.id.studentCheckBox);
            viewHolder.studentInfoName = convertView.findViewById(R.id.studentInfoName);
            viewHolder.studentInfoNo = convertView.findViewById(R.id.studentInfoNo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        StudentInfo studentInfo = studentList.get(position);

        if (stateColor) {
            viewHolder.relativeLayoutStudent.setBackgroundColor(ContextCompat.getColor(context, fpt.edu.vn.exagen.R.color.backgroundButton));
        }


        viewHolder.checkBox.setButtonDrawable(R.drawable.custom_checkbox);


        viewHolder.studentInfoName.setText(studentInfo.getStudentName());
        viewHolder.studentInfoNo.setText(studentInfo.getNo());

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout relativeLayoutStudent;
        CheckBox checkBox;
        TextView studentInfoName;
        TextView studentInfoNo;
    }
}