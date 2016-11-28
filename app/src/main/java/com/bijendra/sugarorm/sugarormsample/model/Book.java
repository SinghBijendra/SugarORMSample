package com.bijendra.sugarorm.sugarormsample.model;

import com.orm.SugarRecord;

/**
 * Created by Newdream on 28-Nov-16.
 */

public class Book extends SugarRecord {
    public String title;
    public String edition;
    public String writer;

    public Book(){
    }

    public Book(String title, String edition,String writer){
        this.title = title;
        this.edition = edition;
        this.writer=writer;
    }
}
