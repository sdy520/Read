package com.example.read.util;

import android.util.Log;

import com.example.read.Entity.BookNameUrl;
import com.example.read.Room.Entity.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class YingSxReadUtil {
    public static ArrayList<BookNameUrl> getBooksUrlFromSearchHtml(String html) {
        ArrayList<BookNameUrl> bookNameUrls = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element type = doc.getElementsByClass("novelslist2").get(0);
        Elements uls = type.getElementsByTag("ul");
        for (Element li : uls.get(0).children()) {
            if (li.getElementsByClass("s1").get(0).text().equals("作品分类"))
                continue;
            BookNameUrl bookNameUrl =new BookNameUrl();
            Element scanS2 = li.getElementsByClass("s2").get(0);
            bookNameUrl.setBooknameurl("https://www.yingsx.com"+scanS2.getElementsByTag("a").attr("href"));
            bookNameUrls.add(bookNameUrl);
        }
        return bookNameUrls;
    }
    /**
     * 获取书籍详细信息
     *
     */
    public static ArrayList<Book> getBookInfo(String html) {
        ArrayList<Book> books= new ArrayList<>();
        Book book =new Book();
        Document doc = Jsoup.parse(html);
        Element meta = doc.getElementsByAttributeValue("property","og:novel:read_url").get(0);
        book.setChapterUrl(meta.attr("content"));
        Element name = doc.getElementById("info");
        book.setBook_name(name.getElementsByTag("h1").get(0).text());
        Element author = doc.getElementById("info");
        book.setAuthor(author.getElementsByTag("p").get(0).text());
        Element img = doc.getElementById("fmimg");
        book.setImgUrl("https://www.yingsx.com"+img.getElementsByTag("img").get(0).attr("src"));
        Element desc = doc.getElementById("intro");
        book.setDesc(desc.getElementsByTag("p").get(0).text());
        Element type = doc.getElementsByAttributeValue("property","og:novel:category").get(0);
        book.setType(type.attr("content"));
        books.add(book);
        Log.e("YingSxReadUtil",book.toString());
        return books;
    }
}
