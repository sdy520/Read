package com.example.read.util;

import android.text.Html;
import android.util.Log;

import com.example.read.Room.Entity.Book;
import com.example.read.Room.Entity.Chapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class DuoBenReadUtil {
    /**
     * 从搜索html中得到书列表
     *
     * @param html
     * @return
     */
    public static ArrayList<Book> getBooksFromSearchHtml(String html) {
        ArrayList<Book> books = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element div = doc.getElementsByClass("type_show").first();
        for (Element element : div.getElementsByClass("bookbox")) {
            Book book = new Book();
            Element img = element.getElementsByTag("img").first();
            book.setImgUrl("https://www.duoben.net" + img.attr("src"));
            Element info = element.getElementsByClass("bookinfo").first();
            for (Element el : info.children()) {
                if (el.tagName().equals("h4")){
                    Element a = el.getElementsByTag("a").first();
                    book.setChapterUrl("https://www.duoben.net"  + a.attr("href"));
                    book.setBook_name(a.text());
                }else if(el.className().equals("cat")){
                    book.setType(el.text().replace("分类：",""));
                }else if(el.className().equals("author")){
                    book.setAuthor(el.text().replace("作者：",""));
                }else if(el.className().equals("update")){
                    Element a1 = el.getElementsByTag("a").first();
                    book.setNewestChapterUrl("https://www.duoben.net"  + a1.attr("href"));
                    book.setNewestChapterTitle(a1.text());
                }else if(el.tagName().equals("p")){
                    book.setDesc(el.text());
                }
            }
            book.setSource(URLCONST.duoben);
            books.add(book);
        }
        return books;
    }
    /**
     * 从html中获取章节正文
     *
     * @param html
     * @return
     */
    public static String getContentFormHtml(String html) {

        Document doc = Jsoup.parse(html);
        Element divContent = doc.getElementById("content");
        //解决划动进度条跳转章节闪退问题
        if (divContent != null) {
            String content = Html.fromHtml(divContent.html()).toString();
            content =content.replace("\n\n","\n");
            return content;
        } else {
            return "";
        }
    }
}
