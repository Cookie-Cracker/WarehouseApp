package com.kingocean.warehouseapp.data.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kingocean.warehouseapp.data.model.RepackType;

import java.util.ArrayList;

public class RepackTypeAdapter extends ArrayAdapter<RepackType> {
    private LayoutInflater inflater;

    public RepackTypeAdapter(Context context, ArrayList<RepackType> repackTypes) {
        super(context, 0, repackTypes);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        RepackType repackType = getItem(position);
        textView.setText(repackType.getRepackType());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        RepackType repackType = getItem(position);
        textView.setText(repackType.getRepackType());

        return convertView;
    }
}
