package com.example.read.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.read.Entity.BookNameUrl;
import com.example.read.R;
import com.example.read.Room.Entity.Book;
import com.example.read.databinding.ActivityBookRankingBinding;
import com.example.read.ui.adapter.SearchBookAdapter;
import com.example.read.util.BiQuGeReadUtil;
import com.example.read.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.read.util.BiQuGeReadUtil.getBooksUrlFromSearchHtmlpaihang;

public class RankingActivity extends Activity {
    SearchBookAdapter adapter;
    ActivityBookRankingBinding rankingBinding;
    private ArrayList<Book> mbookList = new ArrayList<>();
    private ArrayList<BookNameUrl> bookNameUrls = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ranking);
        rankingBinding = ActivityBookRankingBinding.inflate(getLayoutInflater());
        setContentView(rankingBinding.getRoot());

        LinearLayoutManager layoutManager =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rankingBinding.lvRankingBooksList.setLayoutManager(layoutManager);
        getdata();
    }


    private void getdata() {
        Intent intent = getIntent();
        String hangnum = intent.getStringExtra("hang");
        int lienum = intent.getIntExtra("lie",0);
        rankingBinding.pbLoading.setVisibility(View.VISIBLE);
        mbookList.clear();
        String url1="https://www.wqge.cc/paihangbang/";
        OkHttpUtil.getInstance().Get(url1, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", "get回调失败：");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = Objects.requireNonNull(response.body()).string();
                bookNameUrls=getBooksUrlFromSearchHtmlpaihang(body,hangnum,lienum);
                getDatainfo();
            }
        });
        /*OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url1)
                .get()//默认就是GET请求，可以不写
                .build();
        okhttp3.Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.e("TAG", "get回调失败：");
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                String body = response.body().string();
                bookNameUrls=getBooksUrlFromSearchHtmlpaihang(body,hangnum,lienum);
                getDatainfo();
            }
        });*/
    }

    private void getDatainfo(){
        Log.e("ss",bookNameUrls.size()+"");

        for (int i = 0; i < 20; i++) {
            String url = bookNameUrls.get(i).getBooknameurl();
            Log.e("ss",url);
            /*OkHttpUtil.getInstance().Get(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("TAG", "get回调失败：");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String body = Objects.requireNonNull(response.body()).string();
                    try {
                        BiQuGeReadUtil.getBookInfo(body, mbookList);
                    } catch (IndexOutOfBoundsException e) {
                        mHandler.sendMessage(mHandler.obtainMessage(3));
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(2));
                }
            });*/
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            okhttp3.Call call = okHttpClient.newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                    Log.e("TAG", "get回调失败：");
                }

                @Override
                public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                    String body = response.body().string();
                    try {
                        BiQuGeReadUtil.getBookInfo(body, mbookList);
                    } catch (IndexOutOfBoundsException e) {
                        mHandler.sendMessage(mHandler.obtainMessage(3));
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(2));
                }
            });
        }
    }
    private void initSearchList() {
        rankingBinding.lvRankingBooksList.setVisibility(View.VISIBLE);
        adapter = new SearchBookAdapter(getApplicationContext(),mbookList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                rankingBinding.lvRankingBooksList.setAdapter(adapter);
            }
        });
        adapter.setOnItemClickListener(new SearchBookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(RankingActivity.this, BookInfoActivity.class);
                intent.putExtra("book", mbookList.get(position));
                startActivity(intent);
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //searchbook();
                    break;
                case 2:
                    initSearchList();
                    break;
                case 3:
//                    mSearchBookActivity.getLvSearchBooksList().setAdapter(null);
                    rankingBinding.pbLoading.setVisibility(View.GONE);
                    break;
            }
        }
    };

}
