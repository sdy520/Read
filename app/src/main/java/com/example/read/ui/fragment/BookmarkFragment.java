package com.example.read.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.read.R;
import com.example.read.Room.Dao.BookDao;
import com.example.read.Room.Database.BookDatabase;
import com.example.read.Room.Entity.Book;
import com.example.read.Room.Entity.Chapter;
import com.example.read.custom.DragSortGridView;
import com.example.read.databinding.FragmentBookmarkBinding;
import com.example.read.ui.activity.MainActivity;
import com.example.read.ui.activity.SearchBookActivity;
import com.example.read.ui.adapter.BookcaseDragAdapter;
import com.example.read.util.BiQuGeReadUtil;
import com.example.read.util.OkHttpUtil;
import com.example.read.util.TianLaiReadUtil;
import com.example.read.util.VibratorUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarkFragment extends Fragment {
    private FragmentBookmarkBinding bookmarkBinding;
    private Activity mactivity;
    private ArrayList<Book> mBooks = new ArrayList<>();//书目数组
    private BookDao bookDao;
    MainActivity mMainActivity;
    private BookcaseDragAdapter mBookcaseAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookmarkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookmarkFragment newInstance(String param1, String param2) {
        BookmarkFragment fragment = new BookmarkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mBookcaseAdapter.notifyDataSetChanged();
                    bookmarkBinding.srlContent.finishRefresh();
                    break;
                case 2:
                    bookmarkBinding.srlContent.finishRefresh();
                    break;

            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mMainActivity = ((MainActivity)getContext());
        bookDao= BookDatabase.getDatabase(mactivity).getBookDao();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mactivity=(Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bookmarkBinding = FragmentBookmarkBinding.inflate(inflater,container,false);

        bookmarkBinding.imageadd.setOnClickListener(v -> {
            //Intent intent = new Intent(getActivity(),SearchBookActivity.class);
            Intent intent = new Intent(mactivity, SearchBookActivity.class);
            startActivity(intent);
        });
        bookmarkBinding.srlContent.setEnableRefresh(false);
        bookmarkBinding.srlContent.setEnableHeaderTranslationContent(false);
        bookmarkBinding.srlContent.setEnableLoadMore(false);
        bookmarkBinding.srlContent.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                initNoReadNum();
            }
        });
        bookmarkBinding.gvBook.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mBookcaseAdapter.ismEditState()) {
                    bookmarkBinding.srlContent.setEnableRefresh(false);
                    mBookcaseAdapter.setmEditState(true);
                    bookmarkBinding.gvBook.setDragModel(DragSortGridView.DRAG_BY_LONG_CLICK);
                    mBookcaseAdapter.notifyDataSetChanged();

                    mMainActivity.getRlCommonTitle().setVisibility(View.GONE);
                    mMainActivity.getRlEditTitile().setVisibility(View.VISIBLE);
                    VibratorUtil.Vibrate(mactivity,200);
//                    mBookcaseFragment.getGvBook().setOnItemClickListener(null);
                }
                return true;
            }
        });
        mMainActivity.getTvEditFinish().setOnClickListener(v -> {
            mMainActivity.getRlCommonTitle().setVisibility(View.VISIBLE);
            mMainActivity.getRlEditTitile().setVisibility(View.GONE);
//                mBookcaseFragment.getSrlContent().setEnableRefresh(true);
            bookmarkBinding.gvBook.setDragModel(-1);
            mBookcaseAdapter.setmEditState(false);
            mBookcaseAdapter.notifyDataSetChanged();
        });
       /* try {
            getData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return bookmarkBinding.getRoot();
    }
    private void init() throws InterruptedException {
        initBook();

        if (mBooks == null || mBooks.size() == 0) {
            Log.e("sdff","mBooks.get(i).getBook_name()");
            bookmarkBinding.gvBook.setVisibility(View.GONE);
            bookmarkBinding.llNoDataTips.setVisibility(View.VISIBLE);
        } else {
            if(mBookcaseAdapter == null) {
                mBookcaseAdapter = new BookcaseDragAdapter(getContext(), R.layout.gridview_book_item, mBooks, false);
                bookmarkBinding.gvBook.setDragModel(-1);
                bookmarkBinding.gvBook.setTouchClashparent(((MainActivity)(getContext())).getVpContent());
       /*     mBookcaseFragment.getGvBook().setDragModel(DragSortGridView.DRAG_BY_LONG_CLICK);
            ((MainActivity) (mBookcaseFragment.getActivity())).setViewPagerScroll(false);*/
                bookmarkBinding.gvBook.setAdapter(mBookcaseAdapter);
            }else {
                mBookcaseAdapter.notifyDataSetChanged();
            }
            bookmarkBinding.llNoDataTips.setVisibility(View.GONE);
            bookmarkBinding.gvBook.setVisibility(View.VISIBLE);
        }

    }
    public void getData() throws InterruptedException {
        init();
        initNoReadNum();
    }

    private void initBook() throws InterruptedException {
        mBooks.clear();
        WorkThread workThread1 = new WorkThread();
        workThread1.start();
        workThread1.join();

    }
    private void initNoReadNum() {
        for (final Book book : mBooks) {
            String url = book.getChapterUrl();
            Log.e("ddddddd",url);
            OkHttpUtil.getInstance().Get(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("TAG", "get回调失败：");
                    mHandler.sendMessage(mHandler.obtainMessage(1));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String body = Objects.requireNonNull(response.body()).string();
                    //Log.e("body",body);
                    final ArrayList<Chapter> chapters = TianLaiReadUtil.getChaptersFromHtml(body,book);
                    //updateAllOldChapterData(mChapters);
                    int noReadNum = chapters.size() - book.getChapterTotalNum();
                    if (noReadNum > 0) {
                        book.setNoReadNum(noReadNum);
                        mHandler.sendMessage(mHandler.obtainMessage(1));
                    } else {
                        book.setNoReadNum(0);
                        mHandler.sendMessage(mHandler.obtainMessage(2));
                    }
                    bookDao.updateBook(book);
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
                    final ArrayList<Chapter> chapters = BiQuGeReadUtil.getChaptersFromHtml(body,book);
                    //updateAllOldChapterData(mChapters);
                    int noReadNum = chapters.size() - book.getChapterTotalNum();
                    if (noReadNum > 0) {
                        book.setNoReadNum(noReadNum);
                        mHandler.sendMessage(mHandler.obtainMessage(1));
                    } else {
                        book.setNoReadNum(0);
                        mHandler.sendMessage(mHandler.obtainMessage(2));
                    }
                    bookDao.updateBook(book);

                }
            });*/

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            getData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public class WorkThread extends Thread {
        @Override
        public void run() {
            mBooks.addAll(bookDao.getAllBooks());

            for (int i = 0; i < mBooks.size(); i++) {
                if (mBooks.get(i).getSortCode() != i + 1) {
                    mBooks.get(i).setSortCode(i + 1);

                    bookDao.updateBook(mBooks.get(i));
                    Log.e("sdff",mBooks.get(i).getBook_name());
                }
            }
        }
    }
}