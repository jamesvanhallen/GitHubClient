package com.james.testgithub.main.Another;

import java.util.Date;


/**
 * Created by james on 30.01.15.
 */
public class Repository {
    public long id;
    public Owner owner;
    public String name;
    public String full_name;
    public int forks_count;
    public int watchers_count;
    public Date created_at;
    public Date pushed_at;

    public static class Owner {
        public String login;
        public long id;
        public String avatar_url;
    }

    @Override
    public String toString() {
        return "#"+id+" "+full_name+" ("+created_at.toString()+"/"+pushed_at+")";
    }

}

