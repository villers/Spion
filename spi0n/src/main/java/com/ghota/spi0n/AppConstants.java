package com.ghota.spi0n;

/**
 * Created by Ghota on 17/02/14.
 */
public class AppConstants {

    /**
     * URL of your server
     */
    public static final String RSS_SERVER_URL = "http://www.spi0n.com/";
    public static final String RSS_SERVER_TO_JSON = "?json=1&count="+ MainActivity.mPreference.getString("load_nb_post", "20");

    public static final String URL_ARTICLE_HTML = "http://spi0n.com/wp-content/themes/clockstone/spion.php?id=";
}