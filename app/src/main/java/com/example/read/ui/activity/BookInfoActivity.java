package com.example.read.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.read.Entity.Setting;
import com.example.read.MyApplication;
import com.example.read.R;
import com.example.read.Room.Dao.BookDao;
import com.example.read.Room.Dao.ChapterDao;
import com.example.read.Room.Database.BookDatabase;
import com.example.read.Room.Database.ChapterDatabase;
import com.example.read.Room.Entity.Book;
import com.example.read.Room.Entity.Chapter;
import com.example.read.creator.DialogCreator;
import com.example.read.databinding.ActivityBookInfoBinding;
import com.example.read.ui.adapter.ChapterTitleAdapter;
import com.example.read.ui.adapter.ReadContentAdapter;
import com.example.read.util.BiQuGeReadUtil;
import com.example.read.util.BrightUtil;
import com.example.read.util.DateHelper;
import com.example.read.util.OkHttpUtil;
import com.example.read.util.ReadStyle;
import com.example.read.util.StringHelper;
import com.example.read.util.SysManager;
import com.example.read.util.TianLaiReadUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookInfoActivity extends BaseActivity {
    private static String TAG = "BookInfoActivity";
    private ActivityBookInfoBinding bookInfoBinding;


    private Book mBook;
    private ArrayList<Chapter> mChapters = new ArrayList<>();
    private ArrayList<Chapter> mInvertedOrderChapters = new ArrayList<>();
    private ChapterDao chapterDao;
    private BookDao bookDao;
    //private ChapterService mChapterService;
    //private BookService mBookService;
    //    private ChapterContentAdapter mChapterContentAdapter;
    private ReadContentAdapter mReadContentAdapter;
    private ChapterTitleAdapter mChapterTitleAdapter;
    private Setting mSetting;
    private LinearLayoutManager mLinearLayoutManager;

    private boolean isFirstInit = true;

    private boolean settingChange;//是否是设置改变
    private boolean autoScrollOpening = false;//是否开启自动滑动

    private float pointX;
    private float pointY;
    private float scrolledX;
    private float scrolledY;

    private long lastOnClickTime;//上次点击时间
    private long doubleOnClickConfirmTime = 200;//双击确认时间

    private float settingOnClickValidFrom;
    private float settingOnClickValidTo;


    private Dialog mSettingDialog;//设置视图
    private Dialog mSettingDetailDialog;//详细设置视图

    private int curSortflag = 0; //0正序  1倒序

    private int curCacheChapterNum = 0;//缓存章节数

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    init();
                    break;
                case 3:
                    int position = msg.arg1;
                    bookInfoBinding.rvContent.scrollToPosition(position);
                    if (position >= mChapters.size() - 1) {
                        delayTurnToChapter(position);
                    }
                    bookInfoBinding.pbLoading.setVisibility(View.GONE);
                    break;
                case 4:
                    position = msg.arg1;
                    bookInfoBinding.rvContent.scrollToPosition(position);
                    if (mBook.getHistoryChapterNum() < position) {
                        delayTurnToChapter(position);
                    }
                    bookInfoBinding.pbLoading.setVisibility(View.GONE);
                    break;
                case 6:
                    bookInfoBinding.rvContent.scrollBy(0,mBook.getLastReadPosition());
                    mBook.setLastReadPosition(0);
                    //if (!StringHelper.isEmpty(mBook.getId().toString())) {
                        //mBookService.updateEntity(mBook);
                    //}
                    break;
                case 7:
                    if (mLinearLayoutManager != null) {
                        bookInfoBinding.rvContent.scrollBy(0,2);
                    }
                    break;
                case 8:
                    showSettingView();
                    break;
            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏应用程序的标题栏，即当前activity的label
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏android系统的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bookInfoBinding=ActivityBookInfoBinding.inflate(getLayoutInflater());
        setContentView(bookInfoBinding.getRoot());

        mBook = (Book) getIntent().getSerializableExtra("book");

        chapterDao =ChapterDatabase.getDatabase(this).getChapterDao();
        bookDao= BookDatabase.getDatabase(this).getBookDao();
        mSetting = SysManager.getSetting();

        if (mSetting.isDayStyle()) {
            bookInfoBinding.dlReadActivity.setBackgroundResource(mSetting.getReadBgColor());
        } else {
            bookInfoBinding.dlReadActivity.setBackgroundResource(R.color.sys_night_bg);
        }
        if (!mSetting.isBrightFollowSystem()) {
            BrightUtil.setBrightness(this, mSetting.getBrightProgress());
        }
        settingOnClickValidFrom = BaseActivity.height / 4;
        settingOnClickValidTo = BaseActivity.height / 4 * 3;
        //关闭上拉加载
        bookInfoBinding.srlContent.setEnableLoadMore(false);
        //关闭下拉刷新
        bookInfoBinding.srlContent.setEnableRefresh(false);
        bookInfoBinding.srlContent.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                settingChange = true;
                getData();
            }
        });

        bookInfoBinding.pbLoading.setVisibility(View.VISIBLE);
        bookInfoBinding.lvChapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //关闭侧滑菜单
                bookInfoBinding.dlReadActivity.closeDrawer(GravityCompat.START);
                final int positionnow;
                if (curSortflag == 0) {
                    positionnow = position;
                } else {
                    positionnow = mChapters.size() - 1 - position;
                }
                if (StringHelper.isEmpty(mChapters.get(positionnow).getContent())) {
                    bookInfoBinding.pbLoading.setVisibility(View.VISIBLE);

                    String url1 = mChapters.get(positionnow).getUrl();
                    OkHttpUtil.getInstance().Get(url1, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("TAG", "get回调失败：");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                           // String body = Objects.requireNonNull(response.body()).string();
                            String body = new String(Objects.requireNonNull(response.body()).bytes(), "gbk");
                            if (!StringHelper.isEmpty(body))
                                mChapters.get(positionnow).setContent(TianLaiReadUtil.getContentFormHtml(body));
                            //mChapterService.saveOrUpdateChapter(mChapters.get(chapterNum));
                            mHandler.sendMessage(mHandler.obtainMessage(4, positionnow, 0));
                            Log.e("body", "333");
                        }
                    });
                   /* OkHttpClient okHttpClient1 = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(url1)
                            .get()//默认就是GET请求，可以不写
                            .build();
                    okhttp3.Call call1 = okHttpClient1.newCall(request);
                    call1.enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                            Log.e("TAG", "get回调失败：");
                        }

                        @Override
                        public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                            String body = response.body().string();
                            if (!StringHelper.isEmpty(body))
                                mChapters.get(positionnow).setContent(BiQuGeReadUtil.getContentFormHtml(body));
                            //mChapterService.saveOrUpdateChapter(mChapters.get(chapterNum));
                            mHandler.sendMessage(mHandler.obtainMessage(4, positionnow, 0));
                            Log.e("body", "333");
                        }
                    });*/
                }else {
//                    mReadActivity.getLvContent().setSelection(position);
                    bookInfoBinding.rvContent.scrollToPosition(positionnow);
                    if (position > mBook.getHistoryChapterNum()) {
                        delayTurnToChapter(position);
                    }
                }
            }

        });

        bookInfoBinding.rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //页面初始化的时候不要执行
                if (!isFirstInit) {

                    MyApplication.getApplication().newThread(new Runnable() {
                        @Override
                        public void run() {
                            saveLastChapterReadPosition(dy);
                        }
                    });
                } else {
                    isFirstInit = false;
                }
            }
        });
        bookInfoBinding.tvChapterSort.setOnClickListener(v -> {
            if (curSortflag == 0) {//当前正序
                bookInfoBinding.tvChapterSort.setText(R.string.positive_sort);
                curSortflag = 1;
            } else {//当前倒序
                bookInfoBinding.tvChapterSort.setText(R.string.inverted_sort);
                curSortflag = 0;
            }
            changeChapterSort();
        });
        //关闭手势滑动
        bookInfoBinding.dlReadActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        bookInfoBinding.dlReadActivity.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull @NotNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull @NotNull View drawerView) {
                //打开手势滑动
                bookInfoBinding.dlReadActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(@NonNull @NotNull View drawerView) {
                //关闭手势滑动
                bookInfoBinding.dlReadActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        getData();

    }

    /**
     * 章节数据网络同步
     */
    private void getData(){
        String url = mBook.getChapterUrl();
        Log.e("ddddddd",url);
        OkHttpUtil.getInstance().Get(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "get回调失败：");
                mHandler.sendMessage(mHandler.obtainMessage(1));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //String body = Objects.requireNonNull(response.body()).string();
                String body = new String(Objects.requireNonNull(response.body()).bytes(), "gbk");
                Log.e(TAG,body);
                try {
                    mChapters = TianLaiReadUtil.getChaptersFromHtml(body,mBook);
                    Log.e(TAG,mChapters.toString());
                    //updateAllOldChapterData(mChapters);
                    mInvertedOrderChapters.clear();
                    mInvertedOrderChapters.addAll(mChapters);
                    Collections.reverse(mInvertedOrderChapters);

                    mBook.setChapterTotalNum(mChapters.size());
                    chapterDao.insertAllChapters(mChapters);
                    if (mBook.getHistoryChapterNum()< 0)
                        mBook.setHistoryChapterNum(0);
                    else if (mBook.getHistoryChapterNum() >= mChapters.size())
                        mBook.setHistoryChapterNum(mChapters.size() - 1);
                    getContent(mChapters.get(mBook.getHistoryChapterNum()));
                }catch (Exception e){
                    Log.e(TAG,"getdata error");
                }

            }
        });
        /*OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        okhttp3.Call call =okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.e("TAG", "get回调失败：");
                mHandler.sendMessage(mHandler.obtainMessage(1));
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                String body = response.body().string();
                //Log.e("body",body);
                mChapters = BiQuGeReadUtil.getChaptersFromHtml(body,mBook);
                //updateAllOldChapterData(mChapters);
                mInvertedOrderChapters.clear();
                mInvertedOrderChapters.addAll(mChapters);
                Collections.reverse(mInvertedOrderChapters);

                mBook.setChapterTotalNum(mChapters.size());
                chapterDao.insertAllChapters(mChapters);
                if (mBook.getHistoryChapterNum()< 0)
                    mBook.setHistoryChapterNum(0);
                else if (mBook.getHistoryChapterNum() >= mChapters.size())
                    mBook.setHistoryChapterNum(mChapters.size() - 1);
                getContent(mChapters.get(mBook.getHistoryChapterNum()));

            }
        });*/
    }
    /**
     * 初始化
     * @param chapter
     */
    private void getContent(Chapter chapter){
        String url1 = chapter.getUrl();
        Log.e(TAG,url1);
        OkHttpUtil.getInstance().Get(url1, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", "get回调失败：");
                mHandler.sendMessage(mHandler.obtainMessage(1));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //String body = Objects.requireNonNull(response.body()).string();
                String body = new String(Objects.requireNonNull(response.body()).bytes(), "gbk");
                chapter.setContent(TianLaiReadUtil.getContentFormHtml(body));
                mHandler.sendMessage(mHandler.obtainMessage(1));
                Log.e("body", "2222");
            }
        });
        /*OkHttpClient okHttpClient1 = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url1)
                .get()//默认就是GET请求，可以不写
                .build();
        okhttp3.Call call1 =okHttpClient1.newCall(request);
        call1.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.e("TAG", "get回调失败：");
                mHandler.sendMessage(mHandler.obtainMessage(1));
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                String body = response.body().string();

                chapter.setContent(BiQuGeReadUtil.getContentFormHtml(body));

                mHandler.sendMessage(mHandler.obtainMessage(1));
                Log.e("body", "2222");
            }
        });*/
    }
    private void init() {
        initContent();
        initChapterTitleList();
    }

    /**
     * 初始化主内容视图
     */
    private void initContent() {

        if (mSetting.isDayStyle()) {
            bookInfoBinding.dlReadActivity.setBackgroundResource(mSetting.getReadBgColor());
        } else {
            bookInfoBinding.dlReadActivity.setBackgroundResource(R.color.sys_night_bg);
        }
        if (mReadContentAdapter == null) {
//            mChapterContentAdapter = new ChapterContentAdapter(mReadActivity, R.layout.listview_chapter_content_item, mChapters, mBook);
//            mReadActivity.getLvContent().setAdapter(mChapterContentAdapter);
            //设置布局管理器
            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            bookInfoBinding.rvContent.setLayoutManager(mLinearLayoutManager);
            mReadContentAdapter = new ReadContentAdapter(this, mChapters);
            initReadViewOnClick();
            bookInfoBinding.rvContent.setAdapter(mReadContentAdapter);
        } else {
            mReadContentAdapter.notifyDataSetChangedBySetting();
        }
        if (!settingChange) {
//            mReadActivity.getLvContent().setSelection(mBook.getHisttoryChapterNum());
            bookInfoBinding.rvContent.scrollToPosition(mBook.getHistoryChapterNum());
            delayTurnToLastChapterReadPosion();
        } else {
            settingChange = false;
        }
        bookInfoBinding.pbLoading.setVisibility(View.GONE);
        bookInfoBinding.srlContent.finishLoadMore();

    }
    /**
     * 初始化阅读界面点击事件
     */
    private void initReadViewOnClick() {
        mReadContentAdapter.setmOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pointY = event.getRawY();
                Log.e("body", "touch");
                return false;
            }
        });
        mReadContentAdapter.setOnItemClickListener(new ReadContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("body", settingOnClickValidFrom+"");
                Log.e("body", settingOnClickValidTo+"");
                Log.e("body", pointY+"");
                if (pointY > settingOnClickValidFrom && pointY < settingOnClickValidTo) {
                    Log.e("body", "onclick");
                    autoScrollOpening = false;
//                    int progress = mReadActivity.getLvContent().getLastVisiblePosition() * 100 / (mChapters.size() - 1);
                    long curOnClickTime = DateHelper.getLongDate();
                    if (curOnClickTime - lastOnClickTime < doubleOnClickConfirmTime) {
                        autoScroll();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(doubleOnClickConfirmTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!autoScrollOpening) {
                                    Log.e("body", "click");
                                    mHandler.sendMessage(mHandler.obtainMessage(8));
                                }

                            }
                        }).start();
                    }
                    lastOnClickTime = curOnClickTime;
                } else if (pointY > settingOnClickValidTo) {
                    bookInfoBinding.rvContent.scrollBy(0,BaseActivity.height);
                } else if (pointY < settingOnClickValidFrom) {
                    bookInfoBinding.rvContent.scrollBy(0,-BaseActivity.height);
                }
            }
        });

    }
    /**
     * 自动滚动
     */
    private void autoScroll() {
        autoScrollOpening = true;
        new Thread(() -> {
            while (autoScrollOpening) {
                try {
                    Thread.sleep(mSetting.getAutoScrollSpeed() + 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage(7));

            }
        }).start();
    }
    /**
     * 延迟跳转章节(防止跳到章节尾部)
     */
    private void delayTurnToChapter(final int positionnow) {
        new Thread(() -> {
            try {
                Thread.sleep(50);
                mHandler.sendMessage(mHandler.obtainMessage(4, positionnow, 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
    /**
     * 延迟跳转章节位置
     */
    private void delayTurnToLastChapterReadPosion() {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                mHandler.sendMessage(mHandler.obtainMessage(6));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case APPCONST.REQUEST_FONT:
                if (resultCode == RESULT_OK) {
                    mSetting.setFont((Font) data.getSerializableExtra(APPCONST.FONT));
                    settingChange = true;
                    initContent();
                }
                break;
        }
    }*/
    /**
     * 保存最后阅读章节的进度
     *
     * @param dy
     */
    private void saveLastChapterReadPosition(int dy) {
        if (mLinearLayoutManager == null) return;

        if (mLinearLayoutManager.findFirstVisibleItemPosition() != mLinearLayoutManager.findLastVisibleItemPosition()
                || dy == 0) {
            mBook.setLastReadPosition(0);
        } else {
            mBook.setLastReadPosition(mBook.getLastReadPosition() + dy);
        }
        mBook.setHistoryChapterNum(mLinearLayoutManager.findLastVisibleItemPosition());
        MyApplication.getApplication().newThread(new Runnable() {
            @Override
            public void run() {
                boolean s =bookDao.isInBookList(mBook.getBook_name());
                Log.e("boolean1", String.valueOf(s));
                if(s)
                {
                    bookDao.updateBook(mBook);
                }
            }
        });
        //if (!StringHelper.isEmpty(mBook.getId())) {
        //    mBookService.updateEntity(mBook);
        //}
    }
    /**
     * 显示设置视图
     */
    private void showSettingView() {
        autoScrollOpening = false;
        if (mSettingDialog != null) {
            mSettingDialog.show();
        } else {
            int progress = 100;
            if(mChapters.size() != 1){
                progress = mLinearLayoutManager.findLastVisibleItemPosition() * 100 / (mChapters.size() - 1);
            }

            //缓存整本
            mSettingDialog = DialogCreator.createReadSetting(this, mSetting.isDayStyle(), progress, view -> {//返回
                        this.finish();
                    },v -> {
                        MyApplication.getApplication().newThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean s =bookDao.isInBookList(mBook.getBook_name());
                                Log.e("boolean", String.valueOf(s));
                                if(s)
                                {
                                    bookDao.updateBook(mBook);
                                    //主线程中创建handler后会创建一个looper对象，而子线程却不会，所以我在我使用Toast前先执行 Looper.prepare();使用Toast后执行 Looper.loop();
                                    //这样不会闪退
                                    Looper.prepare();
                                    Toast.makeText(getApplicationContext(),"已经加入书架",Toast.LENGTH_SHORT).show();
                                }else {
                                    bookDao.insertBook(mBook);
                                    Looper.prepare();
                                    Toast.makeText(getApplicationContext(), "加入书架成功", Toast.LENGTH_SHORT).show();
                                }
                                Looper.loop();

                            }
                        });
                    }
                    ,view -> {//上一章
//                            int curPosition = mReadActivity.getLvContent().getLastVisiblePosition();

                        int curPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                        if (curPosition > 0) {
                            bookInfoBinding.rvContent.scrollToPosition(curPosition - 1);
                        }
                    }, view -> {//下一章

//                            int curPosition = mReadActivity.getLvContent().getLastVisiblePosition();
                        int curPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                        if (curPosition < mChapters.size() - 1) {
                            bookInfoBinding.rvContent.scrollToPosition(curPosition + 1);
                            delayTurnToChapter(curPosition + 1);
                        }
                    }, view -> {//目录
                        initChapterTitleList();
                        bookInfoBinding.dlReadActivity.openDrawer(GravityCompat.START);
                        mSettingDialog.dismiss();

                    }, (dialog, view, isDayStyle) -> {//日夜切换

                        changeNightAndDaySetting(isDayStyle);
                    }, view -> {//设置
                        showSettingDetailView();
                    }, new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            bookInfoBinding.pbLoading.setVisibility(View.VISIBLE);
                            final int chapterNum = (mChapters.size() - 1) * progress / 100;
                            String url1 = mChapters.get(chapterNum).getUrl();
                            OkHttpUtil.getInstance().Get(url1, new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.e("TAG", "get回调失败：");
                                    mHandler.sendMessage(mHandler.obtainMessage(1));
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    //String body = Objects.requireNonNull(response.body()).string();
                                    String body = new String(Objects.requireNonNull(response.body()).bytes(), "gbk");
                                    if(!StringHelper.isEmpty(body))
                                        mChapters.get(chapterNum).setContent(TianLaiReadUtil.getContentFormHtml(body));
                                    //mChapterService.saveOrUpdateChapter(mChapters.get(chapterNum));
                                    mHandler.sendMessage(mHandler.obtainMessage(4, chapterNum, 0));
                                    Log.e("body", "333");
                                }
                            });
                            /*OkHttpClient okHttpClient1 = new OkHttpClient();
                            final Request request = new Request.Builder()
                                    .url(url1)
                                    .get()//默认就是GET请求，可以不写
                                    .build();
                            okhttp3.Call call1 =okHttpClient1.newCall(request);
                            call1.enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                                    Log.e("TAG", "get回调失败：");
                                    mHandler.sendMessage(mHandler.obtainMessage(1));
                                }

                                @Override
                                public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                                    String body = response.body().string();
                                    if(!StringHelper.isEmpty(body))
                                        mChapters.get(chapterNum).setContent(BiQuGeReadUtil.getContentFormHtml(body));
                                    //mChapterService.saveOrUpdateChapter(mChapters.get(chapterNum));
                                    mHandler.sendMessage(mHandler.obtainMessage(4, chapterNum, 0));
                                    Log.e("body", "333");
                                }
                            });*/
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }//阅读进度
                    }
                    , (dialog, view, tvDownloadProgress) -> {
                Toast.makeText(getApplicationContext(),"暂无资源可以下载",Toast.LENGTH_SHORT).show();
                        /*if (StringHelper.isEmpty(mBook.getId().toString())){
                            addBookToCaseAndDownload(tvDownloadProgress);
                        }else {
                            getAllChapterData(tvDownloadProgress);
                        }
                    */
                    });
        }

    }
    /**
     * 更新所有章节
     *
     * @param newChapters

    private void updateAllOldChapterData(ArrayList<Chapter> newChapters) {
        int i;
        for (i = 0; i < mChapters.size() && i < newChapters.size(); i++) {
            Chapter oldChapter = mChapters.get(i);
            Chapter newChapter = newChapters.get(i);
            if (!oldChapter.getTitle().equals(newChapter.getTitle())) {
                oldChapter.setTitle(newChapter.getTitle());
                oldChapter.setUrl(newChapter.getUrl());
                oldChapter.setContent(null);
                mChapterService.updateEntity(oldChapter);
            }
        }
        if (mChapters.size() < newChapters.size()) {
            int start = mChapters.size();
            for (int j = mChapters.size(); j < newChapters.size(); j++) {
                newChapters.get(j).setId(StringHelper.getStringRandom(25));
                newChapters.get(j).setBookId(mBook.getId());
                mChapters.add(newChapters.get(j));
//                mChapterService.addChapter(newChapters.get(j));
            }
            mChapterService.addChapters(mChapters.subList(start,mChapters.size()));
        } else if (mChapters.size() > newChapters.size()) {
            for (int j = newChapters.size(); j < mChapters.size(); j++) {
                mChapterService.deleteEntity(mChapters.get(j));
            }
            mChapters.subList(0, newChapters.size());
        }
    } */
    /**
     * 显示详细设置视图
     */
    private void showSettingDetailView() {
        mSettingDialog.dismiss();
        if (mSettingDetailDialog != null) {
            mSettingDetailDialog.show();
        } else {
            mSettingDetailDialog = DialogCreator.createReadDetailSetting(this, mSetting,
                    readStyle -> changeStyle(readStyle), v -> reduceTextSize(), v -> increaseTextSize(), v -> {
                        autoScroll();
                        mSettingDetailDialog.dismiss();
                    });
        }
    }
    /**
     * 改变阅读风格
     *
     * @param readStyle
     */
    private void changeStyle(ReadStyle readStyle) {
        settingChange = true;
        if (!mSetting.isDayStyle()) mSetting.setDayStyle(true);
        mSetting.setReadStyle(readStyle);
        switch (readStyle) {
            case common:
                mSetting.setReadBgColor(R.color.sys_common_bg);
                mSetting.setReadWordColor(R.color.sys_common_word);
                break;
            case leather:
                mSetting.setReadBgColor(R.mipmap.theme_leather_bg);
                mSetting.setReadWordColor(R.color.sys_leather_word);
                break;
            case protectedEye:
                mSetting.setReadBgColor(R.color.sys_protect_eye_bg);
                mSetting.setReadWordColor(R.color.sys_protect_eye_word);
                break;
            case breen:
                mSetting.setReadBgColor(R.color.sys_breen_bg);
                mSetting.setReadWordColor(R.color.sys_breen_word);
                break;
            case blueDeep:
                mSetting.setReadBgColor(R.color.sys_blue_deep_bg);
                mSetting.setReadWordColor(R.color.sys_blue_deep_word);
                break;
        }
        SysManager.saveSetting(mSetting);
        init();
    }
    /**
     * 缩小字体
     */
    private void reduceTextSize() {
        if (mSetting.getReadWordSize() > 1) {
            mSetting.setReadWordSize(mSetting.getReadWordSize() - 1);
            SysManager.saveSetting(mSetting);
            settingChange = true;
            initContent();
        }
    }

    /**
     * 增大字体
     */
    private void increaseTextSize() {
        if (mSetting.getReadWordSize() < 40) {
            mSetting.setReadWordSize(mSetting.getReadWordSize() + 1);
            SysManager.saveSetting(mSetting);
            settingChange = true;
            initContent();
        }
    }
    /**
     * 白天夜间改变
     *
     * @param isCurDayStyle
     */
    private void changeNightAndDaySetting(boolean isCurDayStyle) {
        mSetting.setDayStyle(!isCurDayStyle);
        SysManager.saveSetting(mSetting);
        settingChange = true;
        init();
    }
    /**
     * 初始化章节目录视图
    */
    private void initChapterTitleList() {
        if (mSetting.isDayStyle()) {
            bookInfoBinding.tvBookList.setTextColor(this.getResources().getColor(mSetting.getReadWordColor()));
            bookInfoBinding.tvChapterSort.setTextColor(this.getResources().getColor(mSetting.getReadWordColor()));
        } else {
            bookInfoBinding.tvBookList.setTextColor(this.getResources().getColor(R.color.sys_night_word));
            bookInfoBinding.tvChapterSort.setTextColor(this.getResources().getColor(R.color.sys_night_word));
        }
        if (mSetting.isDayStyle()) {
            bookInfoBinding.llChapterListView.setBackgroundResource(mSetting.getReadBgColor());
        } else {
            bookInfoBinding.llChapterListView.setBackgroundResource(R.color.sys_night_bg);
        }
        int selectedPostion, curChapterPosition;

        //设置布局管理器
        if (curSortflag == 0) {
            mChapterTitleAdapter = new ChapterTitleAdapter(this, R.layout.listview_chapter_title_item, mChapters);
//            curChapterPosition = mReadActivity.getRvContent().getLastVisiblePosition();
            curChapterPosition = mLinearLayoutManager.findLastVisibleItemPosition();
            selectedPostion = curChapterPosition - 5;
            if (selectedPostion < 0) selectedPostion = 0;
            if (mChapters.size() - 1 - curChapterPosition < 5) selectedPostion = mChapters.size();
            mChapterTitleAdapter.setCurChapterPosition(curChapterPosition);
        } else {
            mChapterTitleAdapter = new ChapterTitleAdapter(this, R.layout.listview_chapter_title_item, mInvertedOrderChapters);
//            curChapterPosition = mChapters.size() - 1 - mReadActivity.getLvContent().getLastVisiblePosition();
            curChapterPosition = mChapters.size() - 1 - mLinearLayoutManager.findLastVisibleItemPosition();
            selectedPostion = curChapterPosition - 5;
            if (selectedPostion < 0) selectedPostion = 0;
            if (mChapters.size() - 1 - curChapterPosition < 5) selectedPostion = mChapters.size();
            mChapterTitleAdapter.setCurChapterPosition(curChapterPosition);
        }
        bookInfoBinding.lvChapterList.setAdapter(mChapterTitleAdapter);
        bookInfoBinding.lvChapterList.setSelection(selectedPostion);

    }
    /**
     * 改变章节列表排序（正倒序）
     */
    private void changeChapterSort() {
        //设置布局管理器
        if (curSortflag == 0) {
            mChapterTitleAdapter = new ChapterTitleAdapter(this, R.layout.listview_chapter_title_item, mChapters);
        } else {
            mChapterTitleAdapter = new ChapterTitleAdapter(this, R.layout.listview_chapter_title_item, mInvertedOrderChapters);
        }
        bookInfoBinding.lvChapterList.setAdapter(mChapterTitleAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            bookInfoBinding.rvContent.scrollBy(0,BaseActivity.height);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            bookInfoBinding.rvContent.scrollBy(0,-BaseActivity.height);
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSettingDialog!=null)
            mSettingDialog.dismiss();
        MyApplication.getApplication().shutdownThreadPool();
    }
}
