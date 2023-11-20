package com.example.timeriut;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.timeriut.adapter.RowAdapter;
import com.example.timeriut.db.DatabaseClient;
import com.example.timeriut.db.Seance;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //DataBase
    private DatabaseClient mDB;

    private RowAdapter adapter;
    private ListView listRow;
    private ArrayList<Etape> list_etape;
    private ArrayList<Activity> list_activity;
    private Seance currenteSeance;


    private Button button_start;
    private Button button_load;
    private Button button_save;

    public final static String NAME_ROW = "NAME_ROW";

    //Au lancement de l'app, va chercher la row 1
    private int id = 1;

    public final static String STATE_ETAPE = "STATE";
    private String name;

    public final static int HELLO_REQUEST = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Rotation ecran : sauvegarde des données*/
        if(savedInstanceState != null){
            list_etape = (ArrayList) savedInstanceState.getParcelableArrayList(STATE_ETAPE).clone();
            adapter = new RowAdapter(this, list_etape);
        }else{
            list_etape = new ArrayList<>();
            adapter = new RowAdapter(this, new ArrayList<Seance>());
        }
        setContentView(R.layout.activity_main);

        //Recuparation du DatabaseClient
        mDB = DatabaseClient.getInstance(getApplicationContext());

        button_start = findViewById(R.id.button_start);
        button_start.setOnClickListener(this);

        listRow = findViewById(R.id.listview);
        listRow.setAdapter(adapter);

        button_load = findViewById(R.id.button_load);
        button_load.setOnClickListener(this);

        button_save = findViewById(R.id.button_save);
        button_save.setOnClickListener(this);

        /**Le nom de la séance à aller chercher dans la base*/
        name = getIntent().getStringExtra(NAME_ROW);
    }

    public void onGameStart(View view){
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        createListActivity();
        intent.putParcelableArrayListExtra(GameActivity.GAME_KEY, list_activity);
        intent.putExtra(GameActivity.NAME, getTitle());

        intent.putExtra(GameActivity.POSITION, id);
        startActivity(intent);
    }

    public void onLoadStart(View view){
        Intent intent = new Intent(MainActivity.this, LoadActivity.class);
        intent.putParcelableArrayListExtra(GameActivity.GAME_KEY, list_activity);
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        if(view == button_start){
            onGameStart(view);
        }

        if(view == button_load){
            onLoadStart(view);
        }

        if(view == button_save){
            onSaveSeance();
        }
    }

    void createListActivity(){
        list_activity = new ArrayList<Activity>();
        list_activity.add(new Activity("Preparation", list_etape.get(0).getDuration()));

        /**Nombre de séquences*/
        for(int i = 0 ; i < list_etape.get(1).getDuration(); i++){
            /** Nombre de cycles*/
            for(int j = 0; j < list_etape.get(2).getDuration(); j++){
                list_activity.add(new Activity("Travail", list_etape.get(3).getDuration()));
                /**  Tant que ce n'est pas le dernier cycle : ajouter des repos*/
                if(j != list_etape.get(2).getDuration() - 1){
                    list_activity.add(new Activity("Repos", list_etape.get(4).getDuration()));
                }
            }
            /**  Ajout d'un repos long*/
                list_activity.add(new Activity("Repos long", list_etape.get(5).getDuration()));
        }

        /**Fin de la liste
         * Permet le scrolling du premier élément en dehors du cadre*/
        list_activity.add(new Activity("Terminer", 0));
        list_activity.add(new Activity("", 0));
        list_activity.add(new Activity("", 0));
        list_activity.add(new Activity("", 0));
    }


    /**Popup pour sauvegarder/modifier une séance*/
    public void onSaveSeance() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Voulez vous enregistrer ce programme?");
        EditText text = new EditText(getApplicationContext());
        text.setText(currenteSeance.getLibelle());
        alert.setView(text);

        alert.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(currenteSeance.getLibelle().equals(text.getText().toString())){
                    updateSeance();

                }else{
                    setTitle(text.getText().toString());
                    saveSeance(text.getText().toString());
                }
            }
        });

        alert.setNegativeButton("Annuler",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }


    private void getSeances() {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class GetSeance extends AsyncTask<Void, Void, Seance> {

            @Override
            protected Seance doInBackground(Void... voids) {
                Seance seanceList;
                if(name != null){
                    seanceList= mDB.getAppDatabase()
                            .seanceDao()
                            .getRowForId(Math.toIntExact(mDB.getAppDatabase().seanceDao().getIdFromName(name)));
                }else{
                    seanceList= mDB.getAppDatabase()
                            .seanceDao()
                            .getRowForId(id);
                }

                return seanceList;
            }

            @Override
            protected void onPostExecute(Seance seances) {
                super.onPostExecute(seances);
                if(seances == null){
                    seances = new Seance();
                    seances.setLibelle("Nice try");
                    seances.setPreparation(3);
                    seances.setSequence(15);
                    seances.setCycle(15);
                    seances.setTravail(99);
                    seances.setRepos(3);
                    seances.setRepos_long(3);
                }
                currenteSeance = seances;

                // Mettre à jour l'adapter avec la liste de taches
                adapter.clear();

               // List<Etape> listE = new ArrayList<>();
                list_etape.add(new Etape("Preparation", seances.getPreparation()));
                list_etape.add(new Etape("Sequences", seances.getSequence()));
                list_etape.add(new Etape("Cycles", seances.getCycle()));
                list_etape.add(new Etape("Travail", seances.getTravail()));
                list_etape.add(new Etape("Repos", seances.getRepos()));
                list_etape.add(new Etape("Repos long", seances.getRepos_long()));
                adapter.addAll(list_etape);

                // Now, notify the adapter of the change in source
                adapter.notifyDataSetChanged();
                setTitle("Programme : " + seances.getLibelle());
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        GetSeance gt = new GetSeance();
        gt.execute();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Mise à jour des taches
        if(list_etape!= null &&  list_etape.isEmpty()){
            getSeances();
        }
    }


    private void saveSeance(String nomS) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class SaveSeance extends AsyncTask<Void, Void, Seance> {

            @Override
            protected Seance doInBackground(Void... voids) {

                // creating a task
                Seance seance = new Seance();
                seance.setLibelle(nomS);
                seance.setPreparation(list_etape.get(0).getDuration());
                seance.setSequence(list_etape.get(1).getDuration());
                seance.setCycle(list_etape.get(2).getDuration());
                seance.setTravail(list_etape.get(3).getDuration());
                seance.setRepos(list_etape.get(4).getDuration());
                seance.setRepos_long(list_etape.get(5).getDuration());

                // adding to database
                long id = mDB.getAppDatabase()
                        .seanceDao()
                        .insert(seance);

                // mettre à jour l'id de la tache
                // Nécessaire si on souhaite avoir accès à l'id plus tard dans l'activité
                seance.setId(id);
                return seance;
            }

            @Override
            protected void onPostExecute(Seance seance) {
                super.onPostExecute(seance);

            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        SaveSeance st = new SaveSeance();
        st.execute();
    }

    private void updateSeance() {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class UpdateSeance extends AsyncTask<Void, Void, Seance> {

            @Override
            protected Seance doInBackground(Void... voids) {

                currenteSeance.setPreparation(list_etape.get(0).getDuration());
                currenteSeance.setSequence(list_etape.get(1).getDuration());
                currenteSeance.setCycle(list_etape.get(2).getDuration());
                currenteSeance.setTravail(list_etape.get(3).getDuration());
                currenteSeance.setRepos(list_etape.get(4).getDuration());
                currenteSeance.setRepos_long(list_etape.get(5).getDuration());

                // adding to database
                 mDB.getAppDatabase()
                        .seanceDao()
                        .update(currenteSeance);

                // mettre à jour l'id de la tache
                // Nécessaire si on souhaite avoir accès à l'id plus tard dans l'activité
                return currenteSeance;
            }

            @Override
            protected void onPostExecute(Seance seance) {
                super.onPostExecute(seance);
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        UpdateSeance st = new UpdateSeance();
        st.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Ajout un bouton dans la top barre*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            Intent intent = new Intent(MainActivity.this, GestionActivity.class);
            startActivityForResult(intent, HELLO_REQUEST);
        }
        return super.onOptionsItemSelected(item);
    }

    /**Permet de sauvegarder l'état de la liste list_etape lorsqu'il y a rotation de l'écran*/
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        saveInstanceState.putParcelableArrayList(STATE_ETAPE, list_etape);
        super.onSaveInstanceState(saveInstanceState);
    }

    /**Affiche un toast quand l'activité GestionActivité se termine par un 'Retoure/back'*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HELLO_REQUEST) {
            if (requestCode == RESULT_CANCELED) {
                Toast.makeText(this, "Retour cancel", Toast.LENGTH_SHORT).show();
                name = null;
            }
        }
    }
}