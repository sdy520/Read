package com.example.read.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.read.Entity.Setting;
import com.example.read.R;
import com.example.read.Room.Entity.Chapter;

import com.example.read.util.SysManager;

import java.util.List;

public class ChapterTitleAdapter extends ArrayAdapter<Chapter> {

    private int mResource;
    private Setting setting;
    private int curChapterPosition = -1;
    public ChapterTitleAdapter(@NonNull Context context, int resource,  @NonNull List<Chapter> objects) {
        super(context, resource, objects);
        mResource = resource;
        setting = SysManager.getSetting();
    }

    @Override
    public void notifyDataSetChanged() {
        setting = SysManager.getSetting();
        super.notifyDataSetChanged();
    }
    class ViewHolder{

        TextView tvTitle;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(mResource,null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_chapter_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initView(position,viewHolder);
        return convertView;

    }
    private void initView(int postion,final ViewHolder viewHolder){
        final  Chapter chapter = getItem(postion);
        viewHolder.tvTitle.setText("【" + chapter.getTitle() + "】");
        if (setting.isDayStyle()) {
            viewHolder.tvTitle.setTextColor(getContext().getResources().getColor(setting.getReadWordColor()));
        }else {
            viewHolder.tvTitle.setTextColor(getContext().getResources().getColor(R.color.sys_night_word));
        }

        if (postion == curChapterPosition){
            viewHolder.tvTitle.setTextColor(getContext().getResources().getColor(R.color.sys_dialog_setting_word_red));
        }

    }

    public void setCurChapterPosition(int curChapterPosition) {
        this.curChapterPosition = curChapterPosition;
    }
}
