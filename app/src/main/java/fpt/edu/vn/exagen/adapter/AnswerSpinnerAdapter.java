package fpt.edu.vn.exagen.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import fpt.edu.vn.exagen.R;


public class AnswerSpinnerAdapter extends ArrayAdapter<Character> {

    private char selectedAnswer; // Đáp án đã chọn
    public AnswerSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Character> objects, char selectedAnswer) {
        super(context, resource, objects);
        this.selectedAnswer = selectedAnswer;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aswers_selected, parent, false);
        TextView tvSelected = convertView.findViewById(R.id.tvSelected);
        Character answer = getItem(position);
        if (answer != null) {
            tvSelected.setText(answer.toString());
            tvSelected.setTextColor(parent.getResources().getColor(com.google.android.material.R.color.primary_material_light));
        }


        return convertView;
    }


    // Hiển thị danh sách đáp án (sổ ra đáp án) trong Spinner
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answers, parent, false);
        TextView tvCategory = convertView.findViewById(R.id.tvCategory);

        Character answer = getItem(position);
        if (answer != null) {
            tvCategory.setText(answer.toString());
            tvCategory.setTypeface(null, Typeface.BOLD);

        }

        if (answer != null && answer != selectedAnswer) {
            tvCategory.setTextColor(parent.getResources().getColor(com.google.android.material.R.color.design_default_color_error));
            tvCategory.setTypeface(null, Typeface.BOLD);
        }
        return convertView;
    }


    @Override
    public int getCount() {
        return 4; // Chỉ hiển thị 4 đáp án A, B, C, D
    }

    @Override
    public Character getItem(int position) {
        switch (position) {
            case 0:
                return 'A';
            case 1:
                return 'B';
            case 2:
                return 'C';
            case 3:
                return 'D';
            default:
                return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(@Nullable Character item) {
        if (item != null) {
            switch (item) {
                case 'A':
                    return 0;
                case 'B':
                    return 1;
                case 'C':
                    return 2;
                case 'D':
                    return 3;
            }
        }
        return -1;
    }
}

