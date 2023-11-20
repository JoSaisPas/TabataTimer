package com.example.timeriut.db;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SeanceDAO {


    @Query("SELECT * FROM seance WHERE id = :selected_id")
    Seance getRowForId(int selected_id);

    @Query("SELECT * FROM seance WHERE libelle = :name")
    Seance getRowForName(int name);

    @Query("SELECT * FROM seance")
    List<Seance> getAll();

    @Insert
    long insert(Seance seance);

    @Insert
    long[] insertAll(Seance... seance);

    @Delete
    void delete(Seance seance);

    @Update
    void update(Seance seance);

    @Query("SELECT id FROM seance WHERE libelle = :name")
    long getIdFromName(String name);
}