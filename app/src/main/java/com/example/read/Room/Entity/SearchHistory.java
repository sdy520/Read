package com.example.read.Room.Entity;



import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * 搜索记录
 * Created by zhao on 2017/8/3.
 */

@Entity(tableName = "searchhistory",indices = {@Index(value = {"content"}, unique = true)})
public class SearchHistory implements Serializable {
    private static final long serialVersionUID = -3397626384854138171L;

    @PrimaryKey(autoGenerate = true)
    private int id;


    @ColumnInfo(name ="content")
    private String content;//内容




    public SearchHistory(int id, @NotNull String content
                         ) {
        this.id = id;
        this.content = content;

    }
    /*
     *   由于Room 只能识别和使用一个构造器，如果希望定义多个构造器
     *   可以使用Ignore标签，让Room忽略这个构造器
     *   不仅如此，@Ignore标签还可以用于字段
     *   Room不会持久化被@Ignore标签标记过的字段的数据
     * */
    @Ignore
    public SearchHistory(@NotNull String content) {
        this.content = content;

    }
    @Ignore
    public SearchHistory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
