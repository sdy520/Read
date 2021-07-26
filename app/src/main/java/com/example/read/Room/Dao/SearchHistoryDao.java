package com.example.read.Room.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.read.Room.Entity.SearchHistory;

import java.util.List;


@Dao
public interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserthistory(SearchHistory searchHistory);

    @Query("SELECT * FROM SearchHistory ORDER BY ID DESC")
    List<SearchHistory> queryhistoty();
    @Delete
    void deleteallhistory(List<SearchHistory> searchHistories);
}
