package com.example.gouravmahajan.womensecurity;

import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity
{
    Button bt;
    Button btimg;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt=(Button)findViewById(R.id.bt_add);
        btimg=(Button)findViewById(R.id.bt_img);
        bt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startService(new Intent(getBaseContext(),LocationService.class));


            }
        });
        btimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Global.flag=false;
                Log.d("MYVICTIM","ONCLICK");
                startService(new Intent(getBaseContext(),LocationVictimService.class));
            }
        });

    }
}
