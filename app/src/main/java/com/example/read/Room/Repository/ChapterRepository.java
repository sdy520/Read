package com.example.read.Room.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.read.Room.Dao.BookDao;
import com.example.read.Room.Dao.ChapterDao;
import com.example.read.Room.Database.BookDatabase;
import com.example.read.Room.Database.ChapterDatabase;
import com.example.read.Room.Entity.Book;
import com.example.read.Room.Entity.Chapter;

import java.util.List;

public class ChapterRepository {
    private List<Chapter> allChapters;
    private Chapter chapter;
    private ChapterDao chapterDao;


    public ChapterRepository(Context context) {
        ChapterDatabase chapterDatabase=ChapterDatabase.getDatabase(context.getApplicationContext());
        chapterDao=chapterDatabase.getChapterDao();
        allChapters=chapterDao.findBookAllChapterByBookId();
    }
    public ChapterRepository(Context context,String bookid,String title) {
        ChapterDatabase chapterDatabase=ChapterDatabase.getDatabase(context.getApplicationContext());
        chapterDao=chapterDatabase.getChapterDao();
        allChapters=chapterDao.findBookAllChapterByBookId();
        chapter=chapterDao.findChapterByBookIdAndTitle(bookid,title);
    }
    public List<Chapter> findBookAllChapterByBookId(){
        return  allChapters;
    }
}
