package com.example.read.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.read.ui.api.BookApi;
//import com.example.read.BookViewModel;
import com.example.read.Entity.BookNameUrl;
import com.example.read.MyApplication;
import com.example.read.R;
import com.example.read.Room.Dao.SearchHistoryDao;
import com.example.read.Room.Database.SearchHistoryDatabase;
import com.example.read.Room.Entity.Book;
import com.example.read.Room.Entity.SearchHistory;
import com.example.read.ui.adapter.SearchHistoryAdapter;
import com.example.read.util.APPCONST;
import com.example.read.util.BiQuGeReadUtil;
import com.example.read.databinding.ActivitySearchBookBinding;
import com.example.read.ui.adapter.SearchBookAdapter;
import com.example.read.util.DuoBenReadUtil;
import com.example.read.util.OkHttpUtil;
import com.example.read.util.TianLaiReadUtil;
import com.example.read.util.URLCONST;
import com.example.read.util.YingSxReadUtil;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SearchBookActivity extends Activity {
    private static String TAG = "SearchBookActivity";
    SearchBookAdapter searchBookAdapter;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private SearchHistoryDao historyDao;
    private List<SearchHistory> mSearchHistories = new ArrayList<>();
    //BookViewModel bookViewModel;
    ActivitySearchBookBinding binding;
    private ArrayList<Book> mbookList = new ArrayList<>();
    private ArrayList<BookNameUrl> bookNameUrls1 = new ArrayList<>();
    private ArrayList<BookNameUrl> bookNameUrls2 = new ArrayList<>();
    private static final String[] suggestion2 = {"????????????4????????????","????????????","????????????","??????","????????????","????????????","????????????"};
    private static final String[] suggestion1 = {"????????????","????????????","????????????","??????","???????????????","??????????????????","????????????"};
    private int suggestionflag;
    private Retrofit mRetrofit;
    private String searchKey;//???????????????
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        historyDao= SearchHistoryDatabase.getDatabase(this).getSearchHistoryDao();
        //?????????????????????
        binding.tgSuggestBook.setTags(suggestion1);
        //????????????????????????????????????
        binding.tgSuggestBook.setOnTagClickListener(tag -> {
            binding.etSearchKey.setText(tag);
            //???????????????????????????????????????????????????
            binding.etSearchKey.setSelection(binding.etSearchKey.getText().length());
            searchbook();
        });
        binding.tvSearchConform.setOnClickListener(v -> {
           // String string=binding.etSearchKey.getText().toString();
            searchbook();
        });
        binding.etSearchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                searchKey = s.toString();
                if(searchKey.equals(""))
                    searchbook();
            }
        });
        binding.etSearchKey.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //??????????????????
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //????????????
                    ((InputMethodManager) SearchBookActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchBookActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //??????
                    searchbook();
                }
                return false;
            }
        });
        binding.lvHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                binding.etSearchKey.setText(mSearchHistories.get(position).getContent());
                //???????????????????????????????????????????????????
                binding.etSearchKey.setSelection(binding.etSearchKey.getText().length());
                searchbook();
            }
        });
        binding.llClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getApplication().newThread(new Runnable() {
                    @Override
                    public void run() {
                        historyDao.deleteallhistory(mSearchHistories);
                    }
                });
                initHistoryList();
            }
        });
        /*bookViewModel=new ViewModelProvider(this).get(BookViewModel.class);
        bookViewModel.getAllBookLive().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {

            }
        });*/

        //??????Retrofit??????
        mRetrofit = new Retrofit.Builder()
                //??????????????????BaseUrl??????
                //.baseUrl("https://www.23txt.com/")
                .baseUrl("https://www.wqge.cc/")
                //?????????????????????
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        LinearLayoutManager layoutManager =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.lvSearchBooksList.setLayoutManager(layoutManager);
        initHistoryList();

    }

    private void searchbook() {
        binding.pbLoading.setVisibility(View.VISIBLE);
        if(searchKey.equals("")){
            binding.pbLoading.setVisibility(View.GONE);
            binding.lvSearchBooksList.setVisibility(View.GONE);
            binding.llSuggestBooksView.setVisibility(View.VISIBLE);
            initHistoryList();
            binding.lvSearchBooksList.setAdapter(null);
        }else {
            binding.lvSearchBooksList.setVisibility(View.VISIBLE);
            binding.llSuggestBooksView.setVisibility(View.GONE);
            binding.llHistoryView.setVisibility(View.GONE);
            getData();
            MyApplication.getApplication().newThread(new Runnable() {
                @Override
                public void run() {
                    historyDao.inserthistory(new SearchHistory(searchKey));
                }
            });

        }
    }
    private void getData(){
        //???????????????????????????????????????
        mbookList.clear();
        String url1 = "https://www.tianlaixsw.com/search.html?searchkey="+searchKey+"&searchtype=all";
        String url2 = URLCONST.method_db_search+searchKey;
        OkHttpUtil.getInstance().Get(url1, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "????????????get???????????????????????????"+e.toString());
                mHandler.sendMessage(mHandler.obtainMessage(3));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    //String body = new String(Objects.requireNonNull(response.body()).bytes(), "gbk");
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.e(TAG, body);
                    mbookList.addAll(TianLaiReadUtil.getBooksFromSearchHtml(body));
                    mHandler.sendMessage(mHandler.obtainMessage(2));
                }catch (Exception e){
                    Log.e(TAG, "???????????????????????????"+e.toString());
                }

            }
        });
        OkHttpUtil.getInstance().Get(url2, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "????????????get???????????????????????????"+e.toString());
                mHandler.sendMessage(mHandler.obtainMessage(3));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    String body = new String(Objects.requireNonNull(response.body()).bytes(), "gbk");
                    mbookList.addAll(DuoBenReadUtil.getBooksFromSearchHtml(body));
                    mHandler.sendMessage(mHandler.obtainMessage(2));
                }catch (Exception e){
                    Log.e(TAG, "???????????????????????????"+e.toString());
                }
            }
        });
    }
    /*private void getData(){
        //???????????????????????????????????????
        mbookList.clear();

        // ??????5:????????????????????????????????????
        /*BookApi api = mRetrofit.create(BookApi.class);
        //??????6???????????????????????????????????????????????????
        //Call<ResponseBody> jsonDataCall = api.getData(searchKey);
                //api.getJsonData(searchKey);
        Call<String> DataCall = api.getData("searchKey");
        //??????7:??????????????????(??????)
        Log.e("TAG", "get == url???" + DataCall.request().url());
        DataCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                String body = response.body();
                //Log.e("TAG",body);
                bookNameUrls= BiQuGeReadUtil.getBooksUrlFromSearchHtml(body);
                //getDatainfo();
                //mHandler.sendMessage(mHandler.obtainMessage(2));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("TAG", "get????????????2???" + t.getMessage() + "," + t.toString());
                mHandler.sendMessage(mHandler.obtainMessage(3));
            }
        });
    }*/
    /*private void getDatainfo(){
        Log.e("ss",bookNameUrls2.size()+"");
        //??????????????????
        if(bookNameUrls2.size()==0){
            mHandler.sendMessage(mHandler.obtainMessage(4));
        }
        else {
            for (int i = 0; i < bookNameUrls2.size(); i++) {
         /*   int max;
            if(bookNameUrls.size()>10) {
                max = 10;
            }
            else {
                max = bookNameUrls.size();
            }
            for (int i = 0; i < max; i++) {*/
              /*  String url = bookNameUrls2.get(i).getBooknameurl();
                OkHttpUtil.getInstance().Get(url, new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                        Log.e("TAG", "get????????????1???");
                        mHandler.sendMessage(mHandler.obtainMessage(3));
                    }

                    @Override
                    public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                        String body = Objects.requireNonNull(response.body()).string();
                        try {
                            mbookList.addAll(YingSxReadUtil.getBookInfo(body));
                        } catch (IndexOutOfBoundsException e) {
                             mHandler.sendMessage(mHandler.obtainMessage(3));
                        }
                         mHandler.sendMessage(mHandler.obtainMessage(2));
                    }
                });
            }
        }
    }*/

    /**
     * ?????????????????????
     */
    private void initHistoryList() {
        MyApplication.getApplication().newThread(new Runnable() {
            @Override
            public void run() {
                mSearchHistories = historyDao.queryhistoty();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mSearchHistories == null || mSearchHistories.size() == 0) {
                            binding.llHistoryView.setVisibility(View.GONE);
                        } else {
                            mSearchHistoryAdapter = new SearchHistoryAdapter(getApplicationContext(), R.layout.listview_search_history_item, mSearchHistories);
                            binding.lvHistoryList.setAdapter(mSearchHistoryAdapter);
                            binding.llHistoryView.setVisibility(View.VISIBLE);
                        }
                    }
                });
               }
        });

    }

    /**
     * ?????????????????????
     */
    private void initSearchList() {
        searchBookAdapter =new SearchBookAdapter(getApplicationContext(),mbookList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchBookAdapter.notifyDataSetChanged();
                binding.lvSearchBooksList.setAdapter(searchBookAdapter);
            }
        });
        searchBookAdapter.setOnItemClickListener(new SearchBookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(SearchBookActivity.this, BookInfoActivity.class);
                intent.putExtra(APPCONST.BOOK, mbookList.get(position));
                startActivity(intent);
            }
        });
        binding.lvSearchBooksList.setVisibility(View.VISIBLE);
        binding.llSuggestBooksView.setVisibility(View.GONE);
        binding.llHistoryView.setVisibility(View.GONE);
        binding.pbLoading.setVisibility(View.GONE);

    }
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    searchbook();
                    break;
                case 2:
                    initSearchList();
                    break;
                case 3:
//                    mSearchBookActivity.getLvSearchBooksList().setAdapter(null);
                    binding.pbLoading.setVisibility(View.GONE);
                    break;
                case 4:
                    binding.etSearchKey.setText("");
                    binding.pbLoading.setVisibility(View.GONE);
                    binding.lvSearchBooksList.setVisibility(View.GONE);
                    binding.llSuggestBooksView.setVisibility(View.VISIBLE);
                    binding.llHistoryView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"?????????????????????",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    /**
     * ????????????????????????????????????????????????
     */
    public void imagechange(View view) {
        if(suggestionflag==1){
            binding.tgSuggestBook.setTags(suggestion2);
            suggestionflag=2;
        }
        else{
            binding.tgSuggestBook.setTags(suggestion1);
            suggestionflag=1;
        }
    }

    public void onback(View view) {
        super.onBackPressed();
    }
}