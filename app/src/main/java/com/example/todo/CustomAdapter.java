package com.example.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> {

    private final ArrayList<DataModel> dataSet;
    private OnCheckboxClickListener onCheckboxClickListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    // Interface for checkbox click listener
    public interface OnCheckboxClickListener {
        void onCheckboxClick(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public CustomAdapter(ArrayList<DataModel> dataSet, Context mContext) {
        super(mContext, R.layout.raw_item, dataSet);
        this.dataSet = dataSet;
    }

    // Setter for the listener
    public void setOnCheckboxClickListener(OnCheckboxClickListener listener) {
        this.onCheckboxClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    private static class ViewHolder {
        TextView txtName;
        TextView selectionType;
        TextView dueDate;
        CheckBox checkBox;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public DataModel getItem(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View result;

        // If the view is not recycled, inflate the layout
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_item, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.txtName);
            viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
            viewHolder.selectionType = convertView.findViewById(R.id.selection_type);
            viewHolder.dueDate = convertView.findViewById(R.id.due_date);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        DataModel dataModel = getItem(position);
        assert dataModel != null;
        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.checkBox.setChecked(dataModel.isChecked());
        viewHolder.selectionType.setText(dataModel.getType());

        // If the date is in the future, show the date, otherwise show "OVERDUE"
        if (dataModel.getDate().isAfter(LocalDateTime.now())) {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = dateFormat.format(dataModel.getDate());
            viewHolder.dueDate.setText(formattedDate);
        } else {
            viewHolder.dueDate.setText("OVERDUE");
        }

        viewHolder.checkBox.setOnClickListener(v -> {
            if (onCheckboxClickListener != null) {
                onCheckboxClickListener.onCheckboxClick(position);
            }
        });

        setClickListeners(viewHolder.txtName, position);
        setClickListeners(viewHolder.selectionType, position);
        setClickListeners(viewHolder.dueDate, position);

        return result;
    }

    public void setClickListeners(View view, int position) {
        view.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
        view.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(position);
            }
            return true;
        });
    }
}