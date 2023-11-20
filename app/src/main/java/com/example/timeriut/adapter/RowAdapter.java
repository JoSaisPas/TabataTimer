package com.example.timeriut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.timeriut.Etape;
import com.example.timeriut.R;

import java.util.List;

public class RowAdapter  extends ArrayAdapter<Etape> {

    public RowAdapter(Context mCtx, List list){
        super(mCtx, R.layout.add_ctivity_template, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Etape etape = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.add_ctivity_template, parent, false);

        //Element
        TextView titleView = (TextView) rowView.findViewById(R.id.title_template);
        TextView durationView = (TextView) rowView.findViewById(R.id.duration_template);
        titleView.setText(etape.getName());
        durationView.setText(String.valueOf(etape.getDuration()));

        ImageButton sub_button = (ImageButton) rowView.findViewById(R.id.imageButton_sub);
        ImageButton add_button = (ImageButton)rowView.findViewById(R.id.imageButton2_add);

        sub_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etape.subOneDuration();
                RowAdapter.this.notifyDataSetChanged();
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etape.addOneDuration();
                RowAdapter.this.notifyDataSetChanged();
            }
        });


        return rowView;
    }

}
