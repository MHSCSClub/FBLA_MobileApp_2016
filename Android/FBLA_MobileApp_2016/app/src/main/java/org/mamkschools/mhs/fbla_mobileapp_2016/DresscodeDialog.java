package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Util;

import java.util.Scanner;

/**
 * Displays a larger version of the picture
 * Created by Andrew A. Katz on 4/17/2016.
 */
public class DresscodeDialog {
    public DresscodeDialog(final Context context) {
        //Create an alert dialog builder for a new alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        Scanner s = new Scanner(context.getResources().openRawResource(R.raw.dresscode));
        String text = "";
        try {
            do {
                text += s.nextLine() + '\n';
            } while (s.hasNextLine());
        } catch (Exception e) {
            Util.log(e.getMessage());
        }

        //Set the title and message
        builder.setIcon(R.mipmap.ic_launcher)
                .setMessage(Html.fromHtml(text))
                .setTitle("FBLA Official Dress Code");

        //Add an OK button
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setNeutralButton("View Example", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new PictureDialog(context,
                        R.mipmap.dress_code,
                        "Dress Code").show();

            }
        });

        //Create the actual dialog from the builder, then show it.
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
