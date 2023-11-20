package com.example.timeriut;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.timeriut.adapter.GestionAdapter;
import com.example.timeriut.adapter.LoadAdapter;
import com.example.timeriut.db.DatabaseClient;
import com.example.timeriut.db.Seance;

import java.util.ArrayList;
import java.util.List;

public class GestionActivity extends AppCompatActivity {
    //DataBase
    private DatabaseClient mDB;

    private ListView listView;
    private GestionAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion);

        //Recuparation du DatabaseClient
        mDB = DatabaseClient.getInstance(getApplicationContext());
        adapter = new GestionAdapter(this, new ArrayList<Seance>());

        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    /**
     *
     *
     */
    private void getSeances() {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class GetSeances extends AsyncTask<Void, Void, List<Seance>> {

            @Override
            protected List<Seance> doInBackground(Void... voids) {
                List<Seance> seanceList = mDB.getAppDatabase()
                        .seanceDao()
                        .getAll();
                return seanceList;
            }

            @Override
            protected void onPostExecute(List<Seance> seances) {
                super.onPostExecute(seances);

                // Mettre à jour l'adapter avec la liste de taches
                adapter.clear();
                adapter.addAll(seances);

                // Now, notify the adapter of the change in source
                adapter.notifyDataSetChanged();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        GetSeances gt = new GetSeances();
        gt.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Mise à jour des seances
        getSeances();
    }
}