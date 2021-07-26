package com.example.read.Room.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.read.Room.Entity.Chapter;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllChapters(ArrayList<Chapter> chapters);
    @Query("SELECT * FROM Chapter WHERE bookId LIKE :bookId AND " +
            "title LIKE :title LIMIT 1")
    Chapter findChapterByBookIdAndTitle(String bookId, String title);
    @Query("SELECT * FROM Chapter ORDER BY ID DESC")
    List<Chapter> findBookAllChapterByBookId();
}
