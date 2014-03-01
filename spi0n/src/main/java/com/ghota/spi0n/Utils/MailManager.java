package com.ghota.spi0n.Utils;

import android.content.Context;
import android.content.Intent;

import com.ghota.spi0n.R;

/**
 * Created by Ghota on 24/02/14.
 */
public class MailManager {

    public static void openMailIntent(Context c, String subject, String email) {

        String[] TO = { email };

        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        mailIntent.setType("plain/text");

        c.startActivity(Intent.createChooser(mailIntent, c.getString(R.string.choose_mail_client)));
    }
}