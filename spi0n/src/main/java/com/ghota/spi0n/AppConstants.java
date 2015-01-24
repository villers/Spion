package com.ghota.spi0n;

/**
 * Created by Ghota on 17/02/14.
 */
public class AppConstants {

    /**
     * URL of your server
     */
    public static final String RSS_SERVER_URL = "http://www.spi0n.com/";
    public static final String RSS_SERVER_TO_JSON = "?json=1&count="+ MainActivity.mPreference.getString("load_nb_post", "15");
    public static final String URL_ARTICLE_HTML = "http://spi0n.com/wp-content/themes/clockstone/spion.php?id=";

    public static final String EMAIL_SPION = "contactspi0n@gmail.com";

    public static final String MY_AD_UNIT_ID = "ca-app-pub-3273184930942509/8436544490";
}