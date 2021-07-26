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
import com.example.read.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SearchBookActivity extends Activity {
    SearchBookAdapter searchBookAdapter;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private SearchHistoryDao historyDao;
    private List<SearchHistory> mSearchHistories = new ArrayList<>();
    //BookViewModel bookViewModel;
    ActivitySearchBookBinding binding;
    private ArrayList<Book> mbookList = new ArrayList<>();
    private ArrayList<BookNameUrl> bookNameUrls = new ArrayList<>();
    private static final String[] suggestion2 = {"斗罗大陆4终极斗罗","左道倾天","诡秘之主","元尊","天下第九","三寸人间","万族之劫"};
    private static final String[] suggestion1 = {"魔法始记","猎手准则","万相之王","半仙","海兰萨领主","放开那只妖宠","武练巅峰"};
    private int suggestionflag;
    private Retrofit mRetrofit;
    private String searchKey;//搜索关键字
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        historyDao= SearchHistoryDatabase.getDatabase(this).getSearchHistoryDao();
        //初始化建议书目
        binding.tgSuggestBook.setTags(suggestion1);
        //点击建议书目设置到输入框
        binding.tgSuggestBook.setOnTagClickListener(tag -> {
            binding.etSearchKey.setText(tag);
            //将输入框光标自动移到输入文字最后面
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
                //是否是回车键
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) SearchBookActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchBookActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //搜索
                    searchbook();
                }
                return false;
            }
        });
        binding.lvHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                binding.etSearchKey.setText(mSearchHistories.get(position).getContent());
                //将输入框光标自动移到输入文字最后面
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

        //构建Retrofit实例
        mRetrofit = new Retrofit.Builder()
                //设置网络请求BaseUrl地址
                //.baseUrl("https://www.23txt.com/")
                .baseUrl("https://www.wqge.cc/")
                //设置数据解析器
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
    Call<String> DataCall;
    private void getData(){
        //清除之前输入的图书搜索记录
        mbookList.clear();
        /*String urlbase = "https://www.wqge.cc/modules/article/search.php?searchkey="+searchKey;
        Log.e("TAG", "get == url：" + urlbase);
        OkHttpUtil.getInstance().Getwithparms(urlbase, "search1", new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.e("TAG", "get回调失败2：" + e.getMessage() + "," + e.toString());
                mHandler.sendMessage(mHandler.obtainMessage(3));
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                String body = Objects.requireNonNull(response.body()).toString();
                Log.e("TAG", "get回调失败2："+body);
                bookNameUrls= BiQuGeReadUtil.getBooksUrlFromSearchHtml(body);
                getDatainfo();
            }
        });*/
        // 步骤5:创建网络请求接口对象实例
        BookApi api = mRetrofit.create(BookApi.class);
        //步骤6：对发送请求进行封装，传入接口参数
        //Call<ResponseBody> jsonDataCall = api.getJsonData(searchKey);
        DataCall = api.getData(searchKey);
        //步骤7:发送网络请求(异步)
        Log.e("TAG", "get == url：" + DataCall.request().url());
        DataCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                String body = response.body();
                bookNameUrls= BiQuGeReadUtil.getBooksUrlFromSearchHtml(body);
                getDatainfo();
                //mHandler.sendMessage(mHandler.obtainMessage(2));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("TAG", "get回调失败2：" + t.getMessage() + "," + t.toString());
                mHandler.sendMessage(mHandler.obtainMessage(3));
            }
        });

    }
    private void getDatainfo(){
        Log.e("ss",bookNameUrls.size()+"");
        //没有搜索到书
        if(bookNameUrls.size()==0){
            mHandler.sendMessage(mHandler.obtainMessage(4));
        }
        else {
            for (int i = 0; i < bookNameUrls.size(); i++) {
                String url = bookNameUrls.get(i).getBooknameurl();
 	            OkHttpUtil.getInstance().Getwithparms(url,"search", new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                        Log.e("TAG", "get回调失败1：");
                        mHandler.sendMessage(mHandler.obtainMessage(3));
                    }

                    @Override
                    public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                        String body = Objects.requireNonNull(response.body()).string();
                        try {
                            BiQuGeReadUtil.getBookInfo(body, mbookList);
                        } catch (IndexOutOfBoundsException e) {
                            mHandler.sendMessage(mHandler.obtainMessage(3));
                        }

                        mHandler.sendMessage(mHandler.obtainMessage(2));
                    }
                });
/*
                OkHttpClient okHttpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(url)
                        .get()//默认就是GET请求，可以不写
                        .build();
                okhttp3.Call call = okHttpClient.newCall(request);
                call.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                        Log.e("TAG", "get回调失败：");
                        mHandler.sendMessage(mHandler.obtainMessage(3));
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
                });*/
            }
        }
    }

    /**
     * 初始化历史列表
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
     * 初始化搜索列表
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
                Log.e("TAG", "lailefffdf：");
                //DataCall.cancel();
                OkHttpUtil.getInstance().Canale("search1");
                OkHttpUtil.getInstance().Canale("search");
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
                    Toast.makeText(getApplicationContext(),"书库中没有此书",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    /**
     * 点击换一批图片和按钮切换推荐书目
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