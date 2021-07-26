/*package com.example.read;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.read.Room.Repository.BookRepository;
import com.example.read.Room.Entity.Book;

import java.util.List;

public class BookViewModel extends AndroidViewModel {
  private BookRepository bookRepository;
   public BookViewModel(@NonNull Application application) {
       super(application);
       bookRepository=new BookRepository(application);
   }
   public LiveData<List<Book>> getAllBookLive(){
       return bookRepository.getAllBookLive();
   }
}*/
