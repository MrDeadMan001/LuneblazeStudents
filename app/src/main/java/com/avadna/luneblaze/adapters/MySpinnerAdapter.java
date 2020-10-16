package com.avadna.luneblaze.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.avadna.luneblaze.R;

import java.util.List;

/**
 * Created by Sunny on 10-02-2018.
 */

public class MySpinnerAdapter extends ArrayAdapter<String> {

    public MySpinnerAdapter(Context context, List<String> items) {
        super(context, R.layout.custom_spinner_dropdown_item, items);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(true);
        }
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(false);
        }
        return getCustomView(position, convertView, parent);
    }


    @Override
    public int getCount() {
        return super.getCount() + 1; // Adjust for initial selection item
    }

    private View initialSelection(boolean dropdown) {
        // Just an example using a simple TextView. Create whatever default view
        // to suit your needs, inflating a separate layout if it's cleaner.
        TextView view = new TextView(getContext());
        view.setText("First item");

        view.setPadding(0, 12, 0, 12);

        if (dropdown) { // Hidden when the dropdown is opened
            view.setHeight(0);
        }

        return view;
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        // Distinguish "real" spinner items (that can be reused) from initial selection item
        View row = convertView != null && !(convertView instanceof TextView)
                ? convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_dropdown_item, parent, false);

        position = position - 1; // Adjust for initial selection item
        String item = getItem(position);

        // ... Resolve views & populate with data ...
        CheckedTextView ctv_row_item=(CheckedTextView)row.findViewById(R.id.ctv_row_item);
        ctv_row_item.setText(item);

        return row;
    }

}