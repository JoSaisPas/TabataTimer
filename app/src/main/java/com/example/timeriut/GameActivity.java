package com.example.timeriut;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.timeriut.adapter.GameRowAdapter;
import com.example.timeriut.data.Compteur;
import com.example.timeriut.data.OnUpdateListener;

import java.util.ArrayList;
import java.util.LinkedList;


public class GameActivity extends AppCompatActivity implements OnUpdateListener, View.OnClickListener {
    public static final String GAME_KEY = "Activities";
    public static final String NAME = "NAME";
    public static final String STATE_COMPTEUR = "STATE_COMPTEUR";
    public static final String STATE_LIST = "STATE_LIST";
    public static final String POSITION = "POSITION";
    private LinkedList<Activity> list;
    private ListView listView;
    private GameRowAdapter adapter;
    private TextView titre;
    private Compteur compteur;
    private TextView timerView;
    private ImageButton game_button;

    ///Limite le nombre de popup de fin à 1
    private boolean one_popup_end =false;

    private String name;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        titre = findViewById(R.id.game_title);
        game_button = findViewById(R.id.game_button);
        game_button.setOnClickListener(this);
        listView = findViewById(R.id.listViewGame);

        /**Si rotation du device */
        if(savedInstanceState != null){
            compteur = savedInstanceState.getParcelable(STATE_COMPTEUR);
            list = new LinkedList<>(savedInstanceState.getParcelableArrayList(STATE_LIST));

            if(!compteur.getIsRunning()){
                game_button.setBackgroundResource(R.drawable.playpng);
            }else{
                game_button.setBackgroundResource(R.drawable.pause);
            }

            titre.setText(compteur.getNameFirstItem());
            adapter = new GameRowAdapter(this, list);
            listView.setAdapter(adapter);
            compteur.setListView(listView);
        }else{
            list = new LinkedList<Activity>(getIntent().getParcelableArrayListExtra(GAME_KEY));
            adapter = new GameRowAdapter(this, list);
            listView.setAdapter(adapter);
            compteur = new Compteur(list, listView, titre);
        }


        name =  getIntent().getStringExtra(NAME);
        //titre dans la top bar
        setTitle(name);


        timerView = findViewById(R.id.game_time);

        compteur.addOnUpdateListener(this);
        miseAJour();



        /**Ajout de l'adapter dans le listView*/

        //position = getIntent().getIntExtra(POSITION, 0);
    }

    public void onStart(View view){
        compteur.start();
    }

    public void onPause(View view){
        compteur.pause();
    }

    public void onReset(View view){
        compteur.reset();
    }

    private void miseAJour(){
        timerView.setText("" + String.format("%1d", compteur.getMinutes()) + ':' + String.format("%02d", compteur.getSecondes()) + ':' + String.format("%03d", compteur.getMillisecondes()));
    }

    @Override
    public void onUpdate(){
        miseAJour();
        MediaPlayer mp;
        int count = 0;
        if(compteur.getSecondes() <= 3 && compteur.isNewTimer()){

                compteur.setNewTimer(false);

            mp = MediaPlayer.create(this, R.raw.ding);
            mp.setLooping(true);
            if(compteur.getSecondes() > 3){
                mp.setLooping(false);
            }

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                    mp=null;
                }
            });
            mp.start();
        }


        if(compteur.isFinish() && !one_popup_end){
            mp = MediaPlayer.create(this, R.raw.end);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                    mp=null;
                }
            });
            mp.start();
            one_popup_end = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    onGameFinish();

                }
            }, 1000);
        }
    }


    /**Au premier click lance le timer
     * Au second met en pause le timer
     * etc ...*/
    @Override
    public void onClick(View view) {
        if(view == game_button){
            if(compteur.getIsRunning()){
                game_button.setBackgroundResource(R.drawable.playpng);
                onPause(view);
            }else{
                game_button.setBackgroundResource(R.drawable.pause);
                onStart(view);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        saveInstanceState.putParcelable(STATE_COMPTEUR, compteur);
        saveInstanceState.putParcelableArrayList(STATE_LIST, new ArrayList<>(list));

        super.onSaveInstanceState(saveInstanceState);
    }


    /**Popup de fin de séance*/
    public void onGameFinish() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.popup_fin_seance, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(dialogLayout);

        alert.setTitle("Felicitation Seance Terminer");
        TextView tempsTT = dialogLayout.findViewById(R.id.timeTT);//findViewById(R.id.timeTT);
        tempsTT.setText("Temps total : " + getTempstotal());

        TextView tempsW = dialogLayout.findViewById(R.id.timeWork);
        tempsW.setText("Temps travail : " + getTempsWork());


        alert.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                //startActivity(intent);
                intent.putExtra(MainActivity.NAME_ROW, name);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        alert.setNegativeButton("Annuler",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

    /**Retourne le temps de travail de la seance*/
    private int getTempsWork(){
        int temps = 0;
        for(Activity act : list){
            if(act.getName().equals("Travail")){
                temps += act.getDuration();
            }
        }
        return temps;
    }


    /**Retourne le temps total de la seance*/
    private int getTempstotal(){
        int temps = 0;
        for(Activity act : list){
            temps += act.getDuration();
        }
        return temps;
    }
}