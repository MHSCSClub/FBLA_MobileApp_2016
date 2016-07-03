package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

/**
 * Class to contain picture info for array adapters
 * Created by jackphillips on 5/5/16.
 */
public class Picture {
    public int entryid;
    public double geolat;
    public double geolong;
    public double dist;
    public String created;
    public String title;
    public String username;
    public double priority;
    public double hours;

    public Picture(int entryid, double geolat,
                   double geolong, String created,
                   String title, String username,
                   double priority, double hours, double dist){
        this.entryid = entryid;
        this.geolat = geolat;
        this.geolong = geolong;
        this.dist = dist;
        this.created = created;
        this.title = title;
        this.username = username;
        this.priority = priority;
        this.hours = Math.max(0, hours);
        if(Constants.DEMO_MODE) {
            this.hours = Math.min(hours, 1);
            this.dist = 0;
        }
    }
}
