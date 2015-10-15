package org.mamkschools.mhs.mamk2016fblaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.logonButton)).setOnClickListener(this);
    }

    public void onClick(View v){
        int id = v.getId();
        switch(id){
            case R.id.logonButton:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this,
                        "Invalid selection, have you coded in the other activity?",
                        Toast.LENGTH_LONG).show();
                break;
        }
    }
}
