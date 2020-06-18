package com.pratyush.firebaseml;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pratyush.firebaseml.activities.BarCodeScanner;
import com.pratyush.firebaseml.activities.ImageLabeling;
import com.pratyush.firebaseml.activities.LanguageIdentification;
import com.pratyush.firebaseml.activities.TextRecognition;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO Natural Language -> Identify the Language and Translate Text
        //Text Recognition ML Kit Offline data stays on Device
        findViewById(R.id.textRecognition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TextRecognition.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.barcodeScanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarCodeScanner.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.labelImages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImageLabeling.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.identifyLanguage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LanguageIdentification.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.smartReply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SmartReply.class);
                startActivity(intent);
            }
        });


    }

}