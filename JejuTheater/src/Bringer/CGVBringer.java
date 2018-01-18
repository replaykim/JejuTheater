package Bringer;

import Crawlers.JsoupCralwer;
import Interface.Bring;
import Interface.Crawl;
import Parsers.JsoupParser;
import TheaterData.Movies;
import TheaterData.Schedules;
import Utils.Crawler;
import Utils.Parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CGVBringer implements Bring {
    private Crawler crawler;
    private Parser parser;
    public final String JEJU = "0121";
    public final String JEJU_NOHYENG = "0259";

    public CGVBringer()
    {
        this.crawler = Crawler.getInstance();
        this.parser = Parser.getInstance(new JsoupParser());
    }

    @Override
    public ArrayList<ArrayList> bring() {
        ArrayList<ArrayList> lists = new ArrayList<>();

        // TODO: 반복문으로 구현
        lists.add(getMovies(JEJU));
        lists.add(getSchedules(JEJU));
//        lists.add(getSchedules(JEJU_NOHYENG));
//        lists.add(getMovies(JEJU_NOHYENG));

        return lists;
    }

    private Schedules getSchedules(String theater)
    {
        Schedules schedules = new Schedules();
        String date = "&date=" + getDate();
        String url = "http://www.cgv.co.kr/common/showtimes/iframeTheater.aspx?areacode=206,04,06&theatercode=" + JEJU + date;
        String html = crawler.crawl(new JsoupCralwer(), url);

        // TODO: 날짜 개수만큼 반복
        String date_set = parser.parse(html, ".day a[title=\"현재 선택\"]");
        String month = parser.parseToText(date_set, "span");
        String day = parser.parseToText(date_set, "strong");
        String day_of_week = parser.parseToText(date_set, "em");

        // 영화 개수만큼 반복
        ArrayList schedule_set = parser.parseToList(html, ".sect-showtimes ul li .col-times");
        for (int count_movie = 0; count_movie < schedule_set.size(); count_movie++) {
            String movie_titie = parser.parseToText(schedule_set.get(count_movie).toString(), ".info-movie a strong");

            // 상영관 수만큼 반복
            ArrayList screen_set = parser.parseToList(schedule_set.get(count_movie).toString(), ".col-times .type-hall");
            for (int count_screen = 0; count_screen < screen_set.size(); count_screen++) {
                String screen_number = parser.parseToText(screen_set.get(count_screen).toString(), ".info-hall ul li:nth-child(2)");
                ArrayList time_set = parser.parseToList(screen_set.get(count_screen).toString(), ".type-hall div:nth-child(2) ul li");

                // 상영시간 수만큼 반복
                for (int count_showtime = 0; count_showtime < time_set.size(); count_showtime++) {
                    String show_time = parser.parseToText(time_set.get(count_showtime).toString(), "em");
                    String seat_left = parser.parseToText(time_set.get(count_showtime).toString(), "span:not(.hidden)");

                    // TODO: ArrayList에 add
//                    System.out.print(month + "\t");
//                    System.out.print(day + "\t");
//                    System.out.print(day_of_week + "\t");
//                    System.out.print(movie_titie + "\t");
//                    System.out.print(screen_number + "\t");
//                    System.out.print(show_time + "\t");
//                    System.out.print(seat_left + "\n");
                }
//                System.out.println("--------------------------------------------------");
            }
        }
        return null;
    }

    private Movies getMovies(String theater)
    {
        Crawler crawler = Crawler.getInstance();

        String html = crawler.crawl(new JsoupCralwer(), "http://www.cgv.co.kr/movies/?lt=1&ft=1");
        String[] ids = getIds(html);

        // TODO: id 개수만큼 반복
        int i = 0;
        String url_pro = "http://www.cgv.co.kr/movies/detail-view/?midx=";
        String html_movie = crawler.crawl(new JsoupCralwer(), url_pro + ids[i]);

        String title = parseToText(html_movie,".title strong");
        String title_en = parseToText(html_movie, ".title p");
        String genre = parseToText(html_movie, ".spec dt:nth-of-type(3)").substring(5);
        String storyline = parseToText(html_movie, ".sect-story-movie");
        String release_date = parseToText(html_movie, ".spec .on:last-of-type");
        String info = parseToText(html_movie, ".spec .on:nth-last-of-type(2)");
        String age_limit = info.substring(0, 6);//
        String running_time = info.substring(8, 12);
        String score = parseToText(html_movie, ".score .percent span:not(.percent)");
        String ticket_sales = parseToText(html_movie, ".egg-gage:first-of-type .percent");

        System.out.println(title);
        System.out.println(title_en);
        System.out.println(genre);
        System.out.println(storyline);
        System.out.println(release_date);
        System.out.println(age_limit);
        System.out.println(running_time);
        System.out.println(score);
        System.out.println(ticket_sales);

        return null;
    }

    private String parseToText(String html, String tag)
    {
        return parser.parseToText(html, tag);
    }

    private String[] getIds(String html)
    {
        Parser parser = Parser.getInstance(new JsoupParser());
        ArrayList movielist = parser.parseToList(html, ".sect-movie-chart .box-contents a:not(.link-reservation)");
        String[] ids = new String[movielist.size()];

        for (int i = 0; i < movielist.size(); i++)
        {
            String str = movielist.get(i).toString();
            String href = parser.getAttr(str, "a", "href");
            ids[i] = href.substring(href.length()-5);
        }

        return ids;
    }

    private String getDate()
    {
        Calendar c1 = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 날짜 포맷
        String date = sdf.format(c1.getTime()); // String으로 저장

        return date;
    }

}
