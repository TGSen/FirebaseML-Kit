package com.pratyush.firebaseml.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.pratyush.firebaseml.R;

public class LanguageIdentification extends AppCompatActivity {
    private TextView result;
    private EditText enteredText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_identification);
        result = findViewById(R.id.textView);
        enteredText = findViewById(R.id.language);
        findViewById(R.id.identify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enteredText.getText().toString().equals("")){
                    enteredText.setError("Please Enter Text");
                    enteredText.requestFocus();
                }
                else{
                    String lang = enteredText.getText().toString();
                    identifyLanguage(lang);
                }
            }
        });
    }

    private void identifyLanguage(String lang) {
        LanguageIdentifier languageIdentifier = com.google.mlkit.nl.languageid.LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(lang)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode.equals("und")) {
                                    Toast.makeText(LanguageIdentification.this,"Can't identify language.",Toast.LENGTH_LONG).show();
                                } else {
                                   // Log.i(TAG, "Language: " + languageCode);
                                    result.setText("Language: "+languageCode);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
    }
}