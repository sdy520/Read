package com.example.read.util;

import android.text.Html;
import android.util.Log;


import com.example.read.Entity.BookNameUrl;
import com.example.read.Room.Entity.Book;
import com.example.read.Room.Entity.Chapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 天籁小说网html解析工具
 */

public class BiQuGeReadUtil {


    /**
     * 获取书城小说分类列表
     * @param html
     * @return

    public static List<BookType> getBookTypeList(String html){
        List<BookType> bookTypes = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        Elements divs = doc.getElementsByClass("nav");
        if (divs.size() > 0){
            Elements uls = divs.get(0).getElementsByTag("ul");
            if (uls.size() > 0){
                for(Element li : uls.get(0).children()){
                    Element a = li.child(0);
                    BookType bookType = new BookType();
                    bookType.setTypeName(a.attr("title"));
                    bookType.setUrl(a.attr("href"));
                    if (!bookType.getTypeName().contains("小说") || bookType.getTypeName().contains("排行")) continue;
                    if (StringHelper.isNotEmpty(bookType.getTypeName())){
                        bookTypes.add(bookType);
                    }

                }
            }

        }
        return bookTypes;

    }
     */
    /**
     * 获取某一分类小说排行榜列表
     * @param html
     * @return

    public static List<Book> getBookRankList(String html){
        List<Book> books = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements divs = doc.getElementsByClass("r");
        if (divs.size() > 0){
            Elements uls = divs.get(0).getElementsByTag("ul");
            if (uls.size() > 0){
                for(Element li : uls.get(0).children()){
                    Book book = new Book();
                    Element scanS1 = li.getElementsByClass("s1").get(0);
                    Element scanS2 = li.getElementsByClass("s2").get(0);
                    Element scanS5 = li.getElementsByClass("s5").get(0);
                    book.setType(scanS1.html().replace("[","").replace("]",""));
                    Element a = scanS2.getElementsByTag("a").get(0);
                    book.setName(a.attr("title"));
                    book.setChapterUrl(a.attr("href"));
                    book.setAuthor(scanS5.html());
                    book.setSource(BookSource.biquge.toString());
                    books.add(book);

                }
            }
        }

        return books;

    }
     */
    /**
     * 获取小说详细信息
     * @param html
     * @return

    public static Book getBookInfo(String html, Book book)  {


        //小说源
        book.setSource(BookSource.biquge.toString());
        Document doc = Jsoup.parse(html);

        // <meta property="og:novel:read_url" content="https://www.52bqg.net/book_113099/">

        Element meta = doc.getElementsByAttributeValue("property","og:novel:read_url").get(0);

        book.setChapterUrl(meta.attr("content"));


        //图片url
        Element divImg = doc.getElementById("fmimg");
        Element img = divImg.getElementsByTag("img").get(0);
        book.setImgUrl(img.attr("src"));
        Element divInfo = doc.getElementById("info");

        //书名
        Element h1 = divInfo.getElementsByTag("h1").get(0);
        book.setName(h1.html());

        Elements ps = divInfo.getElementsByTag("p");

        //作者
        Element p0 = ps.get(0);
        Element a = p0.getElementsByTag("a").get(0);
        book.setAuthor(a.html());

        //更新时间
        Element p2 = ps.get(2);

        Pattern pattern = Pattern.compile("更新时间：(.*)&nbsp;");
        Matcher matcher = pattern.matcher(p2.html());
        if (matcher.find()){
            book.setUpdateDate(matcher.group(1));
        }

        //最新章节
        Element p3 = ps.get(3);
        a = p3.getElementsByTag("a").get(0);
        book.setNewestChapterTitle(a.attr("title"));
        book.setNewestChapterUrl(book.getChapterUrl() + a.attr("href"));

        //简介
        Element divIntro = doc.getElementById("intro");
        book.setDesc(Html.fromHtml(divIntro.html()).toString());




        return book;

    }
 */

    /**
     * 从搜索html中得到书名链接
     *
     * @param html
     * @return
     */
    public static ArrayList<BookNameUrl> getBooksUrlFromSearchHtml(String html) {
        ArrayList<BookNameUrl> bookNameUrls = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements divs = doc.getElementsByTag("table");
        Element div = divs.get(0);
        Elements elementsByTag = div.getElementsByTag("tr");
        for (int i = 1; i < elementsByTag.size(); i++) {
            Element element = elementsByTag.get(i);
            BookNameUrl bookNameUrl =new BookNameUrl();
            Elements info = element.getElementsByTag("td");
            bookNameUrl.setBooknameurl("https://www.wqge.cc/"+info.get(0).getElementsByTag("a").attr("href"));
            bookNameUrls.add(bookNameUrl);
        }
        return bookNameUrls;
    }
    /**
     * 从搜索html中得到书名排行
     *
     * @param html
     * @return
     */
    public static ArrayList<BookNameUrl> getBooksUrlFromSearchHtmlpaihang(String html,String a,int b) {
        ArrayList<BookNameUrl> bookNameUrls = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element type = doc.getElementsByClass("box b"+a).get(b);
        Elements elementsByTag = type.getElementsByTag("li");
        for (int i = 1; i < elementsByTag.size(); i++) {
            Element element = elementsByTag.get(i);
            BookNameUrl bookNameUrl =new BookNameUrl();
            bookNameUrl.setBooknameurl("https:"+element.getElementsByTag("a").attr("href"));
            bookNameUrls.add(bookNameUrl);
        }
        //Element type1 = doc.getElementsByClass("box b1").get(1);
        //Log.e("text", String.valueOf(type));
        //Log.e("text", String.valueOf(elementsByTag));
        //Log.e("text", bookNameUrls.toString());
        //Log.e("text", String.valueOf(type1));
        return bookNameUrls;
    }
    /**
     * 从搜索html中得到书名链接
     *
     * @param html
     * @return
     */

    /**
     * 获取书籍详细信息
     *
     */
    /*public static ArrayList<Book> getBookInfo(String html,ArrayList<Book> books) {
            Book book =new Book();
            Document doc = Jsoup.parse(html);
            Element meta = doc.getElementsByAttributeValue("property","og:novel:read_url").get(0);
            book.setChapterUrl(meta.attr("content"));
            Element name = doc.getElementById("info");
            book.setBook_name(name.getElementsByTag("h1").get(0).text());
            Element author = doc.getElementById("info");
            book.setAuthor(author.getElementsByTag("p").get(0).text());
            Element img = doc.getElementById("fmimg");
            book.setImgUrl(img.getElementsByTag("img").get(0).attr("src"));
            Element desc = doc.getElementById("intro");
            book.setDesc(desc.getElementsByTag("p").get(0).text());
            Element type = doc.getElementsByClass("con_top").get(0);
            book.setType(type.getElementsByTag("a").get(2).text());
            books.add(book);
            Log.e("bookinfo",book.toString());
        return books;
    }*/
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
        book.setImgUrl(img.getElementsByTag("img").get(0).attr("src"));
        Element desc = doc.getElementById("intro");
        book.setDesc(desc.getElementsByTag("p").get(0).text());
        Element type = doc.getElementsByClass("con_top").get(0);
        book.setType(type.getElementsByTag("a").get(2).text());
        books.add(book);
        Log.e("bookinfo",book.toString());
        return books;
    }
    /**
     * 从html中获取章节列表
     *
     * @param html
     * @return
     */
    public static ArrayList<Chapter> getChaptersFromHtml(String html, Book book) {
        ArrayList<Chapter> chapters = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element divList = doc.getElementById("list");
        Element dl = divList.getElementsByTag("dl").get(0);

        String lastTile = null;
        int i = 0;

        for(Element dd : dl.getElementsByTag("dd")){
            Elements as = dd.getElementsByTag("a");
            if (as.size() > 0) {
                Element a = as.get(0);
                String title = a.html();
                if (!StringHelper.isEmpty(lastTile) && title.equals(lastTile)) {
                    continue;
                }
                Chapter chapter = new Chapter();
                chapter.setNumber(i++);
                chapter.setTitle(title);
                String url = a.attr("href");
                url = book.getChapterUrl() + url;
                chapter.setUrl(url);
                chapters.add(chapter);
                lastTile = title;
            }

        }

        return chapters;
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
            char c = 160;
            String spaec = "" + c;
            content = content.replace(spaec, "  ");
            return content;
        } else {
            return "";
        }
    }
    /**
     * 从搜索html中得到书列表
     *
     * @param html
     * @return
     */
    public static ArrayList<Book> getBooksFromSearchHtml(String html) {
        ArrayList<Book> books = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements divs = doc.getElementsByClass("novelslistss");
        if (divs.size() != 0){
            Element div = divs.get(0);
            Elements lis = div.getElementsByTag("li");
            for (Element li : lis) {
                Book book = new Book();
                Element s2a  = li.getElementsByClass("s2").get(0).getElementsByTag("a").get(0);
                //book.setChapterUrl(s2a.attr("href"));
                book.setBook_name(s2a.text());
                Element s4 = li.getElementsByClass("s4").get(0);
                book.setAuthor(s4.text());
                Element s1 = li.getElementsByClass("s1").get(0);
                book.setType(s1.text());
                Element s5 = li.getElementsByClass("s5").get(0);
                //book.setUpdateDate(s5.text());
                Element s3a = li.getElementsByClass("s3").get(0).getElementsByTag("a").get(0);
                //book.setNewestChapterUrl(s3a.attr("href"));
                //book.setNewestChapterTitle(s3a.text());
                //book.setSource(BookSource.biquge.toString());
                books.add(book);
            }

        }else{
            Book book = new Book();
            //getBookInfo(html,book);
            books.add(book);
        }
        return books;
    }






}
