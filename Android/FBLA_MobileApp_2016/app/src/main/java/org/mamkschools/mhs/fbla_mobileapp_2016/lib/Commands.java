package org.mamkschools.mhs.fbla_mobileapp_2016.lib;



/**
 * Contains allowed commands that can be run against server.
 * Created by Andrew Katz on 10/23/2015.
 */
public final class Commands {



    private Commands(){
        //Exists to defeat instantiation
    }
    public final class Get{
        public static final String TEST = "test/get";

        private Get(){
            //Exists to defeat instantiation
        }
    }
    public final class Post{
        public static final String LOGIN = "user/login";
        public static final String REGISTER = "user/register";
        private Post() {
            //Exists to defeat instantiation
        }
    }
}
