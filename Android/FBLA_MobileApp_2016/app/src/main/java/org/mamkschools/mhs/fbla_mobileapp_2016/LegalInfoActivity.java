package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.mamkschools.mhs.fbla_mobileapp_2016.lib.*;

import java.util.Scanner;

public class LegalInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_info);
        TextView LegalText = (TextView) findViewById(R.id.legaltext);
        Scanner s = new Scanner(this.getResources().openRawResource(R.raw.filler));
        String text = "";
        try{
            do{
                text += s.nextLine();
            } while(s.hasNextLine());
        }catch(Exception e){
            Constants.log(e.getMessage());
        }
        LegalText.setText(text);
    }
}
