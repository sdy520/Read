package com.example.read.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.read.Entity.Setting;
import com.example.read.MyApplication;
import com.example.read.R;
import com.example.read.Room.Dao.ChapterDao;
import com.example.read.Room.Database.ChapterDatabase;
import com.example.read.Room.Entity.Book;
import com.example.read.Room.Entity.Chapter;
import com.example.read.custom.MyTextView;
import com.example.read.util.BiQuGeReadUtil;
import com.example.read.util.DuoBenReadUtil;
import com.example.read.util.OkHttpUtil;
import com.example.read.util.StringHelper;
import com.example.read.util.SysManager;
import com.example.read.util.TianLaiReadUtil;
import com.example.read.util.URLCONST;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReadContentAdapter extends RecyclerView.Adapter<ReadContentAdapter.ViewHold> {
    private RecyclerView rvContent;
    private ArrayList<Chapter> mDatas;
    private Book mbook;
    private Context mContext;
    private Setting mSetting;
    private ChapterDao chapterDao;
    private TextView curTextView;
    private float add=0;
    private float mult=1.5f;

    public class ViewHold extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;
       /* MyTextView tvTitle;
        MyTextView tvContent;*/
        TextView tvErrorTips;
        public ViewHold(@NonNull @NotNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvErrorTips = itemView.findViewById(R.id.tv_loading_error_tips);
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ViewHold viewHolder = (ViewHold) msg.obj;
                    viewHolder.tvErrorTips.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    public ReadContentAdapter(Context context, ArrayList<Chapter> datas,Book book) {
        mDatas = datas;
        mContext = context;
        chapterDao = ChapterDatabase.getDatabase(context).getChapterDao();
        mSetting = SysManager.getSetting();
        mbook = book;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHold onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (rvContent == null) rvContent = (RecyclerView) parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_chapter_content_item,parent,false);
        ViewHold holder = new ViewHold(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHold holder, int position) {
        initView(position,holder);
        if(touchListener!= null)
            holder.itemView.setOnTouchListener(touchListener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mmmOnItemClickListener!=null)
                    mmmOnItemClickListener.onItemClick(holder.itemView,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    public Chapter getItem(int position) {
        return mDatas.get(position);
    }
    public void notifyDataSetChangedBySetting() {
        mSetting = SysManager.getSetting();
        super.notifyDataSetChanged();
    }



    private void initView(final int postion, final ViewHold viewHolder) {
        final Chapter chapter = getItem(postion);
        viewHolder.tvErrorTips.setVisibility(View.GONE);
        viewHolder.tvTitle.setText("???" + chapter.getTitle() + "???");
        if (mSetting.isDayStyle()) {
            viewHolder.tvTitle.setTextColor(mContext.getResources().getColor(mSetting.getReadWordColor()));
            viewHolder.tvContent.setTextColor(mContext.getResources().getColor(mSetting.getReadWordColor()));
        } else {
            viewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.sys_night_word));
            viewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.sys_night_word));
        }
        viewHolder.tvTitle.setTextSize(mSetting.getReadWordSize() + 2);
        viewHolder.tvContent.setTextSize(mSetting.getReadWordSize());
        viewHolder.tvErrorTips.setOnClickListener(view -> getChapterContent(chapter, viewHolder));
        if (StringHelper.isEmpty(chapter.getContent())) {
            getChapterContent(chapter, viewHolder);
        } else {
            viewHolder.tvContent.setText(chapter.getContent());
            viewHolder.tvContent.setLineSpacing(add,mult);
        }


        curTextView = viewHolder.tvContent;

        preLoading(postion);

        lastLoading(postion);

    }
    public TextView getCurTextView() {
        return curTextView;
    }
    /**
     * ??????????????????
     *
     * @param
     */
    private void getChapterContent(final Chapter chapter, final ViewHold viewHolder) {

        MyApplication.getApplication().newThread(new Runnable() {
            @Override
            public void run() {
                if (viewHolder != null) {
                    viewHolder.tvErrorTips.setVisibility(View.GONE);
                }
                Chapter cacheChapter = chapterDao.findChapterByBookIdAndTitle(chapter.getBookId(), chapter.getTitle());


                if (cacheChapter != null && !StringHelper.isEmpty(cacheChapter.getContent())) {
                    Log.e("readadapter", cacheChapter.getTitle());
                    chapter.setContent(cacheChapter.getContent());
                    chapter.setId(cacheChapter.getId());
                    if (viewHolder != null) {
                        viewHolder.tvContent.setText(chapter.getContent());
                        viewHolder.tvContent.setLineSpacing(add,mult);
                        viewHolder.tvErrorTips.setVisibility(View.GONE);
                    }
                } else {
                    String url1 = chapter.getUrl();
                    OkHttpUtil.getInstance().Get(url1, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("TAG", "get???????????????");
                            if (viewHolder != null) {
                                mHandler.sendMessage(mHandler.obtainMessage(1, viewHolder));
                            }
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            //String body = Objects.requireNonNull(response.body()).string();
                            //String body = new String(Objects.requireNonNull(response.body()).bytes(), "gbk");
                            String body;
                            String content = null;
                            if(mbook.getSource().equals(URLCONST.tianlai)) {
                                body = Objects.requireNonNull(response.body()).string();
                                content = TianLaiReadUtil.getContentFormHtml(body);
                            }
                            else if(mbook.getSource().equals(URLCONST.duoben)) {
                                body = new String(Objects.requireNonNull(response.body()).bytes(), "gbk");
                                content = DuoBenReadUtil.getContentFormHtml(body);
                            }
                            chapter.setContent(content);
                            if(viewHolder!=null){
                                String finalContent = content;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewHolder.tvContent.setText(finalContent);
                                        viewHolder.tvContent.setLineSpacing(add,mult);
                                        viewHolder.tvErrorTips.setVisibility(View.GONE);
                                        Log.e("readadapter", "111");
                                    }
                                });}
                        }
                    });
                }
            }
        });




    }

    /**
     * ??????????????????
     */
    private void preLoading(int position) {
        if (position + 1 < getItemCount()) {
            Chapter chapter = getItem(position + 1);
            if (StringHelper.isEmpty(chapter.getContent())) {
                getChapterContent(chapter, null);
                Log.e("readadapter", chapter.getTitle());
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param position
     */
    private void lastLoading(int position) {
        if (position > 0) {
            Chapter chapter = getItem(position - 1);
            if (StringHelper.isEmpty(chapter.getContent())) {
                getChapterContent(chapter, null);
            }
        }
    }



    //1.????????????????????????
    private OnItemClickListener mmmOnItemClickListener;
    private View.OnTouchListener touchListener;
    //2.???????????????????????????
    public interface OnItemClickListener {
        void onItemClick(View view, int position);//??????
    }
    //3.???????????????????????????
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mmmOnItemClickListener = onItemClickListener;
    }
    public void setmOnTouchListener(View.OnTouchListener touchListener){
        this.touchListener=touchListener;
    }
}
