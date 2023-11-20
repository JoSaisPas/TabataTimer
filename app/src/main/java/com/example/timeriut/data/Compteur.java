package com.example.timeriut.data;

import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ListView;
import android.widget.TextView;

import com.example.timeriut.Activity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by fbm on 24/10/2017.
 */
public class Compteur extends UpdateSource implements Parcelable {

    // CONSTANTE
    private final static long INITIAL_TIME = 5000;

    // DATA
    private long updatedTime = INITIAL_TIME;
    private CountDownTimer timer;   // https://developer.android.com/reference/android/os/CountDownTimer.html
    private LinkedList<Activity> list_activity;
    private Boolean isRunning = false;
    private ListView listView;
    private int positionScroll = 1;
    private TextView titre;
    private boolean isFinish = false;



    private boolean newTimer = true;

    public Compteur(long time, LinkedList<Activity> l, ListView lv, TextView t) {
        updatedTime = time*1000;
        this.list_activity = new LinkedList<>();
        list_activity = (LinkedList<Activity>) l.clone();
        this.listView = lv;
        this.titre = t;
    }

    public Compteur(LinkedList<Activity> l, ListView lv, TextView t) {
        updatedTime = l.get(0).getDuration()*1000;
        this.list_activity = new LinkedList<>();
        list_activity = (LinkedList<Activity>) l.clone();
        this.listView = lv;
        this.titre = t;
        titre.setText(l.getFirst().getName());
    }

    protected Compteur(Parcel in) {
        updatedTime = in.readLong();
        byte tmpIsRunning = in.readByte();
        isRunning = tmpIsRunning == 0 ? null : tmpIsRunning == 1;
        positionScroll = in.readInt();
    }

    public static final Creator<Compteur> CREATOR = new Creator<Compteur>() {
        @Override
        public Compteur createFromParcel(Parcel in) {
            return new Compteur(in);
        }

        @Override
        public Compteur[] newArray(int size) {
            return new Compteur[size];
        }
    };

    // Lancer le compteur
    public void start() {

        if (timer == null) {
            // Créer le CountDownTimer
            timer = new CountDownTimer(updatedTime, 10) {
                // Callback fired on regular interval

                public void onTick(long millisUntilFinished) {
                    updatedTime = millisUntilFinished;
                    isRunning = true;
                    // Mise à jour
                    update();
                }

                // Callback fired when the time is up
                public void onFinish() {
                    /** Ajout de 4 ligne supplémentaires dans la liste 'list_activity'
                     * Ces 4 lignes permettent de scroller vers le haut quelque soit le nombre d'Activity
                     * Si le nombre d'Activity > 4 : Il reste des Activity
                     * Si nombre d'Activity == 4 : le programme est fini, seul la ligne 'Terminer' doit être présente
                     * */
                    if(list_activity.size() > 5){
                        list_activity.removeFirst();
                        updatedTime = list_activity.getFirst().getDuration() * 1000;
                        timer = null;
                        listView.smoothScrollToPositionFromTop(positionScroll, 0);
                        positionScroll++;
                        titre.setText(list_activity.getFirst().getName());
                        newTimer = true;
                        Compteur.this.start();
                    }else if(list_activity.size() == 5){
                        list_activity.removeFirst();
                        updatedTime = list_activity.getFirst().getDuration() * 1000;
                        listView.smoothScrollToPositionFromTop(positionScroll, 0);
                        timer = null;

                        Compteur.this.start();
                    }else{
                        updatedTime = 0;
                        isFinish = true;
                    }
                    //Mise à jour
                    update();
                }

            }.start();   // Start the countdown
        }
    }

    // Mettre en pause le compteur
    public void pause() {

        if (timer != null) {
            // Arreter le timer
            stop();
            // Mise à jour
            update();
        }
    }


    // Remettre à le compteur à la valeur initiale
    public void reset() {

        if (timer != null) {
            // Arreter le timer
            stop();
        }

        // Réinitialiser
        updatedTime = INITIAL_TIME;
        isRunning = false;
        // Mise à jour
        update();
    }

    // Arrete l'objet CountDownTimer et l'efface
    private void stop() {
        timer.cancel();
        timer = null;
        isRunning = false;
    }

    public int getMinutes() {
        return (int) (updatedTime / 1000)/60;
    }

    public int getSecondes() {
        int secs = (int) (updatedTime / 1000);
        return secs % 60;
    }

    public int getMillisecondes() {
        return (int) (updatedTime % 1000);
    }

    public Boolean getIsRunning(){
        return isRunning;
    }

    public int getSizeList(){
        return list_activity.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(updatedTime);
        parcel.writeByte((byte) (isRunning == null ? 0 : isRunning ? 1 : 2));
        parcel.writeInt(positionScroll);
    }

    public String getNameFirstItem(){
        return list_activity.getFirst().getName();
    }

    public void setListView(ListView l){
        this.listView = l;
        listView.smoothScrollToPositionFromTop(positionScroll, 0);
    }

    public boolean isFinish(){
        return isFinish;
    }

    public boolean isNewTimer() {
        return newTimer;
    }

    public void setNewTimer(boolean newTimer) {
        this.newTimer = newTimer;
    }
}
