//MAIN
package com.example.av2mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button linkMapa;
    Button linkTracker;
    Button linkShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        linkMapa = (Button) findViewById(R.id.link_mapa);
        linkTracker = (Button) findViewById(R.id.link_tracker);
        linkShow = (Button) findViewById(R.id.link_show);

        linkTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TrackerActivityPRCP
                        .class);
                startActivity(intent);
            }
        });

        linkMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivityGPA.class);
                startActivity(intent);
            }
        });


        linkShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShowActivityHTR.class);
                startActivity(intent);
            }
        });
    }
}