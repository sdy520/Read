package com.example.read.Room.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;



import java.io.Serializable;

/**
 * 书
 */

@Entity
public class Book implements Serializable {


    private static final long serialVersionUID = -8692931255475403385L;

    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @ColumnInfo(name ="book_name")
    private String book_name;//书名
    @ColumnInfo(name ="source")
    private String source;
    @ColumnInfo(name ="chapterUrl")
    private String chapterUrl;//书目Url
    @ColumnInfo(name ="imgUrl")
    private String imgUrl;//封面图片url
    @ColumnInfo(name ="desc")
    private String desc;//简介
    @ColumnInfo(name ="author")
    private String author;//作者
    @ColumnInfo(name ="type")
    private String type;//类型
    @ColumnInfo(name ="updateDate")
    private String updateDate;//更新时间


   // @ColumnInfo(name ="newestChapterId")
    private String newestChapterId;//最新章节id
   // @ColumnInfo(name ="newestChapterTitle")
    private String newestChapterTitle;//最新章节标题
   // @ColumnInfo(name ="newestChapterUrl")
    private String newestChapterUrl;//最新章节url
   // @ColumnInfo(name ="historyChapterId")
    private String historyChapterId;//上次关闭时的章节ID
   // @ColumnInfo(name ="historyChapterNum")
    private int historyChapterNum;//上次关闭时的章节数
   // @ColumnInfo(name ="sortCode")
    private int sortCode;//排序编码
    private int chapterTotalNum;//总章节数
   // @ColumnInfo(name ="lastReadPosition")

    private int lastReadPosition;//上次阅读到的章节的位置
    private int noReadNum;//未读章数量

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", book_name='" + book_name + '\'' +
                ", source='" + source + '\'' +
                ", chapterUrl='" + chapterUrl + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", author='" + author + '\'' +
                ", type='" + type + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", newestChapterId='" + newestChapterId + '\'' +
                ", newestChapterTitle='" + newestChapterTitle + '\'' +
                ", newestChapterUrl='" + newestChapterUrl + '\'' +
                ", historyChapterId='" + historyChapterId + '\'' +
                ", historyChapterNum=" + historyChapterNum +
                ", sortCode=" + sortCode +
                ", chapterTotalNum=" + chapterTotalNum +
                ", lastReadPosition=" + lastReadPosition +
                ", noReadNum=" + noReadNum +
                '}';
    }

    public Book(Integer id, String book_name, String source, String chapterUrl, String imgUrl, String desc, String author, String type, String updateDate, String newestChapterId, String newestChapterTitle, String newestChapterUrl, String historyChapterId, int historyChapterNum, int sortCode, int chapterTotalNum, int lastReadPosition, int noReadNum) {
        this.id = id;
        this.book_name = book_name;
        this.source = source;
        this.chapterUrl = chapterUrl;
        this.imgUrl = imgUrl;
        this.desc = desc;
        this.author = author;
        this.type = type;
        this.updateDate = updateDate;
        this.newestChapterId = newestChapterId;
        this.newestChapterTitle = newestChapterTitle;
        this.newestChapterUrl = newestChapterUrl;
        this.historyChapterId = historyChapterId;
        this.historyChapterNum = historyChapterNum;
        this.sortCode = sortCode;
        this.chapterTotalNum = chapterTotalNum;
        this.lastReadPosition = lastReadPosition;
        this.noReadNum = noReadNum;
    }

    /*@Ignore
    public Book(Integer id,String book_name,String chapterUrl,String imgUrl,String desc,
                String author,String type) {
        this.id = id;
        this.book_name=book_name;
        this.chapterUrl=chapterUrl;
        //this.source=source;
        this.imgUrl=imgUrl;
        this.desc=desc;
        this.author=author;
        this.type=type;
    }*/



    public Book() {

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getChapterTotalNum() {
        return chapterTotalNum;
    }

    public void setChapterTotalNum(int chapterTotalNum) {
        this.chapterTotalNum = chapterTotalNum;
    }

    public String getNewestChapterId() {
        return newestChapterId;
    }

    public void setNewestChapterId(String newestChapterId) {
        this.newestChapterId = newestChapterId;
    }

    public String getNewestChapterTitle() {
        return newestChapterTitle;
    }

    public void setNewestChapterTitle(String newestChapterTitle) {
        this.newestChapterTitle = newestChapterTitle;
    }

    public int getSortCode() {
        return sortCode;
    }

    public void setSortCode(int sortCode) {
        this.sortCode = sortCode;
    }

    public int getLastReadPosition() {
        return lastReadPosition;
    }

    public void setLastReadPosition(int lastReadPosition) {
        this.lastReadPosition = lastReadPosition;
    }

    public int getHistoryChapterNum() {
        return historyChapterNum;
    }

    public void setHistoryChapterNum(int historyChapterNum) {
        this.historyChapterNum = historyChapterNum;
    }

    public String getNewestChapterUrl() {
        return newestChapterUrl;
    }

    public void setNewestChapterUrl(String newestChapterUrl) {
        this.newestChapterUrl = newestChapterUrl;
    }

    public String getHistoryChapterId() {
        return historyChapterId;
    }

    public void setHistoryChapterId(String historyChapterId) {
        this.historyChapterId = historyChapterId;
    }
}
