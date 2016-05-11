package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

/**
 * PictureItem Object to hold picture info for recyclerviews
 * Created by Andrew A. Katz on 4/7/2016.
 */
public class PictureItem {
    private String title;
    private long time;
    private int rate;
    private int views;
    private int pid;
    private int up;



    public PictureItem(String title, long time, int up, int down, int views, int pid) {
        this.title = title;
        this.time = time;
        this.rate = up - down;
        this.views = views;
        this.pid = pid;
        this.up = up;
    }

    public int getUp () { return up;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public int getPid() {
        return pid;
    }

    public int getRate() {
        return rate;
    }

    @SuppressWarnings("unused")
    public int getViews() {
        return views;
    }
}
