package com.example.read.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.read.MyApplication;
import com.example.read.R;
import com.example.read.Room.Dao.BookDao;
import com.example.read.Room.Database.BookDatabase;
import com.example.read.Room.Entity.Book;
import com.example.read.creator.DialogCreator;
import com.example.read.custom.DragAdapter;
import com.example.read.ui.activity.BookInfoActivity;
import com.example.read.util.APPCONST;
import com.example.read.util.StringHelper;

import java.util.ArrayList;


/**
 * Created by zhao on 2017/5/19.
 */

public class BookcaseDragAdapter extends DragAdapter {
    private int mResourceId;
    private ArrayList<Book> list;
    private Context mContext;
    private boolean mEditState;
    private BookDao bookDao;


    public BookcaseDragAdapter(Context context, int textViewResourceId, ArrayList<Book> objects, boolean editState) {
        mContext = context;
        mResourceId = textViewResourceId;
        list = objects;
        mEditState = editState;
        bookDao= BookDatabase.getDatabase(context).getBookDao();
    }

    @Override
    public void onDataModelMove(int from, int to) {
        Book b = list.remove(from);
        list.add(to, b);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSortCode(i);
        }
        MyApplication.getApplication().newThread(new Runnable() {
            @Override
            public void run() {
                bookDao.updateBooks(list);
            }
        });
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Book getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getSortCode();
    }

    public void remove(Book item) {
        list.remove(item);
        notifyDataSetChanged();
        MyApplication.getApplication().newThread(new Runnable() {
            @Override
            public void run() {
                bookDao.deleteBook(item);
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, null);
            viewHolder.ivBookImg =  convertView.findViewById(R.id.iv_book_img);
            viewHolder.tvBookName = convertView.findViewById(R.id.tv_book_name);
            viewHolder.tvNoReadNum =  convertView.findViewById(R.id.tv_no_read_num);
            viewHolder.ivDelete = convertView.findViewById(R.id.iv_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initView(position, viewHolder);
        return convertView;
    }

    private void initView(int position, ViewHolder viewHolder) {
        final Book book = getItem(position);
        if (StringHelper.isEmpty(book.getImgUrl())) {
            book.setImgUrl("");
        }
        Glide.with(mContext)
                .load(book.getImgUrl())
                .error(R.mipmap.no_image)
                .placeholder(R.mipmap.no_image)
                .into(viewHolder.ivBookImg);

        viewHolder.tvBookName.setText(book.getBook_name());
        Log.e("sdffadapt",book.getBook_name());
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCreator.createCommonDialog(mContext, "删除书籍", "确定删除《" + book.getBook_name() + "》及其所有缓存吗？",
                        true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                remove(book);
                                dialogInterface.dismiss();

                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });


        if (mEditState) {
            viewHolder.tvNoReadNum.setVisibility(View.GONE);
            viewHolder.ivDelete.setVisibility(View.VISIBLE);
            viewHolder.ivBookImg.setOnClickListener(null);
        } else {
            viewHolder.ivDelete.setVisibility(View.GONE);
            if (book.getNoReadNum() != 0) {
                viewHolder.tvNoReadNum.setVisibility(View.VISIBLE);
                if (book.getNoReadNum() > 99) {
                    viewHolder.tvNoReadNum.setText("...");
                } else {
                    viewHolder.tvNoReadNum.setText(String.valueOf(book.getNoReadNum()));
                }
            } else {
                viewHolder.tvNoReadNum.setVisibility(View.GONE);
            }
            viewHolder.ivBookImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( mContext, BookInfoActivity.class);
                    intent.putExtra(APPCONST.BOOK, book);
                    book.setNoReadNum(0);
                    //bookDao.updateBook(book);
                    //mBookService.updateEntity(book);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    public void setmEditState(boolean mEditState) {
        this.mEditState = mEditState;
        notifyDataSetChanged();
    }

    public boolean ismEditState() {
        return mEditState;
    }

    class ViewHolder {
        ImageView ivBookImg;
        TextView tvBookName;
        TextView tvNoReadNum;
        ImageView ivDelete;
    }

}
