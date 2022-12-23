package com.fourshape.a4mcqplus.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class OpenExternalUrl {

    public static void open (Context context, String url) {

        try {
            if (context != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(browserIntent);
            }
        } catch (Exception e){

            MakeLog.exception(e);

            if (context != null) {
                Toast.makeText(context, "Can't open.", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
