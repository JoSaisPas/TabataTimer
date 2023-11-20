package com.example.timeriut.db;


import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "seance")
public class Seance {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String libelle;
    private int preparation;
    private int sequence;
    private int cycle;
    private int travail;
    private int repos;
    private int repos_long;

    /*
     * Getters and Setters
     * */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getPreparation() {
        return preparation;
    }

    public void setPreparation(int preparation) {
        this.preparation = preparation;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getTravail() {
        return travail;
    }

    public void setTravail(int travail) {
        this.travail = travail;
    }

    public int getRepos() {
        return repos;
    }

    public void setRepos(int repos) {
        this.repos = repos;
    }

    public int getRepos_long() {
        return repos_long;
    }

    public void setRepos_long(int repos_long) {
        this.repos_long = repos_long;
    }

}