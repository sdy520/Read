package com.example.read.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.read.Room.Entity.Book;

import java.util.List;

@Dao
public interface BookDao {
    @Insert
    void insertBook(Book book);
    @Query("SELECT * FROM Book where book_name=:book_name")
    boolean isInBookList(String book_name);
    @Query("SELECT * FROM Book ORDER BY ID DESC")
    List<Book> getAllBooks();
    @Delete
    void deleteBook(Book book);
    @Update
    void updateBooks(List<Book> books);
    @Update
    void updateBook(Book books);
}
