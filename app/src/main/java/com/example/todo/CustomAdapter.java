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

/**
 * CustomAdapter is a custom adapter for the list view.
 * It is used to be able to hold the checkbox, date, and type state in a single item.
 */
public class CustomAdapter extends ArrayAdapter<DataModel> {

    private final ArrayList<DataModel> dataSet;
    private OnCheckboxClickListener onCheckboxClickListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    // Interface for checkbox click listener
    public interface OnCheckboxClickListener {
        void onCheckboxClick(int position);
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Interface for item long click listener
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    /**
     * Constructor for the custom adapter.
     * @param dataSet
     * @param mContext
     */
    public CustomAdapter(ArrayList<DataModel> dataSet, Context mContext) {
        super(mContext, R.layout.raw_item, dataSet);
        this.dataSet = dataSet;
    }

    // Setter for the checkbox click listener
    public void setOnCheckboxClickListener(OnCheckboxClickListener listener) {
        this.onCheckboxClickListener = listener;
    }

    // Setter for the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Setter for the item long click listener
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    /**
     * ViewHolder class to hold the views in the list item.
     */
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

    /**
     * getView is called to populate the list view.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
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

        // Sets the data for the view
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

        // Add a click listener to the checkbox
        viewHolder.checkBox.setOnClickListener(v -> {
            if (onCheckboxClickListener != null) {
                onCheckboxClickListener.onCheckboxClick(position);
            }
        });

        // Add click listeners to the text views
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