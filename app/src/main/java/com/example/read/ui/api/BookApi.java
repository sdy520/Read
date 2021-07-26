package com.example.read.ui.api;

import com.example.read.Room.Entity.Book;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookApi {
    //@GET("search.php")
    //Call<ResponseBody> getJsonData(@Query("q") String book_name);
    ///modules/article/search.php
    @GET("modules/article/search.php")
    Call<String> getData(@Query("searchkey") String book_name);
}

