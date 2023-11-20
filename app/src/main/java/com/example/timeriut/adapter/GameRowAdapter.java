package com.example.timeriut.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.timeriut.Activity;
import com.example.timeriut.R;

import java.util.List;

public class GameRowAdapter extends ArrayAdapter<Activity> {


    public GameRowAdapter(Context mCtx, List list){
        super(mCtx, R.layout.game_activity_template, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Activity  activity = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.game_activity_template, parent, false);

        //Element
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView indexView = (TextView) rowView.findViewById(R.id.game_index_template);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView titreView = (TextView) rowView.findViewById(R.id.game_titre_template);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView timeView = (TextView) rowView.findViewById(R.id.game_time_template);


        indexView.setText(checkNameActivity(activity.getName())  ? String.valueOf(position+1) : "");
        titreView.setText(checkNameActivity(activity.getName()) ? activity.getName() : "");
        timeView.setText(checkNameActivityForDuration(activity.getName()) ? activity.getStringDuration() : "");
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ConstraintLayout layout = rowView.findViewById(R.id.layout_game_template);
        layout.setBackgroundColor(getBackgroundColor(activity));
        return rowView;
    }

    /**Retourne une couleur basée sur le nom de l'activity*/
    private int getBackgroundColor(Activity activity){
        switch (activity.getName()){
            case "Preparation":
                return 0xff2986cc; //Bleu
            case "Travail":
                return 0xffe91b1b; //Rouge
            case "Repos":
                return 0xff71c94b; //Vert
            case "Terminer":
                return 0xffd4a411; //Or
            case "Repos long":
                return 0xffffd966; //Jaune
        }
        //Ligne sans nom
        return 0xffffffff; //Blanc
    }

    /**Retourn vrai si le nom de l'activity est différentes  ""
     * Permet de ne pas mettre de titre sur les lignes vides*/
    private boolean checkNameActivity(String name){
        return !name.equals("");
    }

    /**Retourn vrai si le nom de l'activity est différentes de 'Terminer ou de vide ''
     * Permet de ne pas afficher de timer sur la ligne Terminer et sur les lignes vide*/
    private boolean checkNameActivityForDuration(String name){
        if(name.equals("Terminer")){
            return false;
        }
        if(name.equals("")){
            return false;
        }
        return true;
    }
}

