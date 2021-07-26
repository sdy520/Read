package com.example.read.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.read.Entity.BookClassify;
import com.example.read.R;


import java.util.List;

public class ClassifyBookAdapter extends RecyclerView.Adapter<ClassifyBookAdapter.ViewHolder>{
    private Context context;
    List<BookClassify> mbookClassifyList;
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.bookFind_image);
            textView=itemView.findViewById(R.id.bookFind_text);
        }
    }

    public ClassifyBookAdapter(Context context, List<BookClassify> bookClassifyList) {
        this.context=context;
        mbookClassifyList = bookClassifyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_classify_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookClassify bookClassify =mbookClassifyList.get(position);
        Glide.with(context)
                .load(bookClassify.getClassifyimg())
                .into(holder.imageView);
        holder.textView.setText(bookClassify.getClassifytext());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mmOnItemClickListener!=null)
                    mmOnItemClickListener.onItemClick(holder.itemView,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mbookClassifyList.size();
    }

    //1.定义变量接收接口
    private OnItemClickListener mmOnItemClickListener;
    //2.定义接口：点击事件
    public interface OnItemClickListener {
        void onItemClick(View view, int position);//单击
    }
    //3.设置接口接收的方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mmOnItemClickListener = onItemClickListener;
    }
}
