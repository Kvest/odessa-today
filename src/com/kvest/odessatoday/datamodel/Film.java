package com.kvest.odessatoday.datamodel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 22:40
 * To change this template use File | Settings | File Templates.
 */
public class Film {
    public long id;
    public String filmname;
    public String country;
    public String year;
    public String director;
    public String actors;
    public String description;
    public String image;
    public String video;
    public String genre;
    public float rating;
    public List<TimetableItem> timetable;
}
