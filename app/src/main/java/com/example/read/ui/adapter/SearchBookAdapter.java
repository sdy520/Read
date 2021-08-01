package com.example.read.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.read.R;
import com.example.read.Room.Entity.Book;

import java.util.List;

public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.ViewHolder>{
    private Context mcontext;
    private List<Book> mbookList;


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;
        TextView textView6;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.tv_book_img);
            textView1=itemView.findViewById(R.id.tv_book_name);
            textView2=itemView.findViewById(R.id.tv_book_source);
            textView3=itemView.findViewById(R.id.tv_book_desc);
            textView4=itemView.findViewById(R.id.tv_book_author);
            textView5=itemView.findViewById(R.id.tv_book_type);
            textView6=itemView.findViewById(R.id.tv_book_newchapter);
        }
    }

    public SearchBookAdapter(Context context,List<Book> bookList) {
        mcontext=context;
        mbookList = bookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_search_book_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String name = mbookList.get(position).getBook_name();
                Log.e("dd",name);
            }
        });*/
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = mbookList.get(position);
        Glide.with(mcontext)
                .load(book.getImgUrl())
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.mipmap.no_image)
                .into(holder.imageView);
        holder.textView1.setText(book.getBook_name());
        holder.textView2.setText(book.getSource());
        holder.textView3.setText(book.getDesc());
        holder.textView4.setText(book.getAuthor());
        holder.textView5.setText(book.getType());
        holder.textView6.setText(book.getNewestChapterTitle());

           holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null)
                    mOnItemClickListener.onItemClick(holder.itemView,holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mbookList.size();
    }

    //1.定义变量接收接口
    private OnItemClickListener mOnItemClickListener;
    //2.定义接口：点击事件
    public interface OnItemClickListener {
        void onItemClick(View view, int position);//单击
    }
    //3.设置接口接收的方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
