package com.example.timeriut.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.timeriut.GestionActivity;
import com.example.timeriut.LoadActivity;
import com.example.timeriut.MainActivity;
import com.example.timeriut.R;
import com.example.timeriut.db.DatabaseClient;
import com.example.timeriut.db.Seance;

import java.util.List;

public class GestionAdapter extends ArrayAdapter<Seance> {

    private DatabaseClient mDB;
    public GestionAdapter(Context mCtx, List<Seance> list){
        super(mCtx, R.layout.gestion_activity_template, list);
        mDB = DatabaseClient.getInstance(mCtx);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Seance seance = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.gestion_activity_template, parent, false);

        //Element
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView titleView = (TextView) rowView.findViewById(R.id.gestion_title);
        titleView.setText(seance.getLibelle());

        Button button = (Button) rowView.findViewById(R.id.gestion_supprimer);
        /**Supprime une row*/
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///Suppression visuel
                GestionAdapter.this.remove(seance);
                ///Suppression dans la base
                deleteSeance(seance);
                GestionAdapter.this.notifyDataSetChanged();

            }
        });

        /**Modification de la row : renvoie sur la MainActivity avec l'ID le row voulu*/
        Button buttonMod = (Button) rowView.findViewById(R.id.gestion_modifier);
        buttonMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GestionAdapter.this.getContext(), MainActivity.class);
                intent.putExtra(MainActivity.NAME_ROW, seance.getLibelle());
                //Enleve l'activité Load de la pile
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                GestionAdapter.this.getContext().startActivity(intent);
            }
        });
       ;
        return rowView;
    }

    private void deleteSeance(Seance s) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class DeleteSeance extends AsyncTask<Void, Void, Seance> {

            @Override
            protected Seance doInBackground(Void... voids) {

                // creating a task
                Seance seance = s;


                // adding to database
                mDB.getAppDatabase()
                        .seanceDao()
                        .delete(seance);

                // mettre à jour l'id de la tache
                // Nécessaire si on souhaite avoir accès à l'id plus tard dans l'activité
                return seance;
            }

            @Override
            protected void onPostExecute(Seance seance) {
                super.onPostExecute(seance);
            }
        }
        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        DeleteSeance st = new DeleteSeance();
        st.execute();
    }
}
