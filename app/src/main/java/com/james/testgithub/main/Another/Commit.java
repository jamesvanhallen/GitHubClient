package com.james.testgithub.main.Another;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by james on 30.01.15.
 */
public class Commit {
    public String sha;
    public CommitInner commit;

    public static class CommitInner {
        public String message;
        public Author author;
    }

    public static class Author {
        public String name;
        public Date date;
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        return dateFormat.format(commit.author.date);
    }

    public String toString() {

        return getName()+" "+commit.author.name+" "+getDate()+" => "+commit.message;

    }

    public String getName() {
        return sha.substring(0,7);
    }

}
