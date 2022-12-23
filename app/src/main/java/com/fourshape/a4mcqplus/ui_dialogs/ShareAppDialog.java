package com.fourshape.a4mcqplus.ui_dialogs;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class ShareAppDialog {

    private MaterialAlertDialogBuilder dialogBuilder;
    private View dialogView;

    public ShareAppDialog (Context context) {
        dialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        dialogView = LayoutInflater.from(context).inflate(R.layout.mtrl_dialog_share_app, null);
        dialogBuilder.setTitle("Share This App");
        dialogBuilder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        MaterialCardView waShareCardView, optionShareCardView;

        waShareCardView = dialogView.findViewById(R.id.whatsapp_share_card_view);
        optionShareCardView = dialogView.findViewById(R.id.options_share_card_view);

        waShareCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    shareInfo(view.getContext(), "com.whatsapp", getMessage());
                } catch (Exception e) {
                    MakeLog.exception(e);
                }

            }
        });

        optionShareCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra(Intent.EXTRA_SUBJECT, "Share 4VideoSongs App");
                    intent.putExtra(Intent.EXTRA_TEXT, getMessage());

                    Intent chooser = Intent.createChooser(intent, "Share 4VideoSongs App");

                    view.getContext().startActivity(chooser);

                } catch (ActivityNotFoundException e) {
                    MakeLog.exception(e);
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.create();

    }

    public void show () {
        dialogBuilder.show();
    }

    private String getMessage () {
        if (dialogBuilder != null) {
            return dialogBuilder.getContext().getString(R.string.app_share_message);
        } else {
            return "GSSSB અને GPSSSB વર્ગ-3 ની પરીક્ષાઓમાં અત્યાર સુધી પૂછવામાં આવેલા 50,000+ MCQ ની લાઈવ-ટેસ્ટ મફતમાં આપો, માત્ર 4MCQ Plus પર. ગૂગલ પ્લે સ્ટોર પરથી એપ મેળવો - https://play.google.com/store/apps/details?id=com.fourshape.a4mcqplus.";
        }
    }

    private void shareInfo(Context context, String packageName, String message) {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, message);
        tweetIntent.setType("text/plain");

        PackageManager packManager = context.getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith(packageName)) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            context.startActivity(tweetIntent);
        } else {
            Intent i = new Intent();
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Intent.EXTRA_TEXT, message);
            i.setAction(Intent.ACTION_VIEW);

            if (packageName.equals("com.twitter.android"))
                i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + message));
            else if (packageName.equals("com.facebook.katana"))
                i.setData(Uri.parse("https://www.facebook.com/sharer/sharer.php?u=" + message));

            context.startActivity(i);

            if (packageName.equals("com.twitter.android"))
                Toast.makeText(context, "Twitter app isn't found", Toast.LENGTH_LONG).show();
            else if (packageName.equals("com.facebook.katana"))
                Toast.makeText(context, "Facebook app isn't found", Toast.LENGTH_LONG).show();
            else if (packageName.equals("com.whatsapp"))
                Toast.makeText(context, "WhatsApp app isn't found", Toast.LENGTH_LONG).show();
        }
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            MakeLog.exception(e);
            return "";
        }
    }


}
