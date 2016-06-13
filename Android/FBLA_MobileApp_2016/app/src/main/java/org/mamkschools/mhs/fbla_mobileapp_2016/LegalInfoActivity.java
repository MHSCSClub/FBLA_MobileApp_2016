package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Util;

import java.util.Scanner;

/**
 * Shows legal information about app
 */

public class LegalInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_info);
        TextView LegalText = (TextView) findViewById(R.id.legaltext);
        Scanner s = new Scanner(this.getResources().openRawResource(R.raw.legal));
        String text = "";
        try{
            do{
                text += s.nextLine() + '\n';
            } while(s.hasNextLine());
        }catch(Exception e){
            Util.log(e.getMessage());
        }
        assert LegalText != null;
        LegalText.setText(text);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
