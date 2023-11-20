package com.example.timeriut.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.timeriut.R;
import com.example.timeriut.db.Seance;

import java.util.List;

public class LoadAdapter extends ArrayAdapter<Seance> {
    public LoadAdapter(Context mCtx, List<Seance> list){
        super(mCtx, R.layout.load_activity_template, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Seance seance = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.load_activity_template, parent, false);

        //Element
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView titleView = (TextView) rowView.findViewById(R.id.load_titre);
        titleView.setText(seance.getLibelle());


        return rowView;
    }

}
