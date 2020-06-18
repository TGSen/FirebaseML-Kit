package com.pratyush.firebaseml.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.smartreply.SmartReplyGenerator;
import com.google.mlkit.nl.smartreply.SmartReplySuggestion;
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult;
import com.google.mlkit.nl.smartreply.TextMessage;
import com.pratyush.firebaseml.R;

import java.util.ArrayList;
import java.util.List;

public class SmartReply extends AppCompatActivity {
    private TextView finalResult;
    private EditText enteredText;
    private List<TextMessage> conversation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_reply);

        finalResult = findViewById(R.id.textView);
        enteredText = findViewById(R.id.language);
        conversation = new ArrayList<>();
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enteredText.getText().toString().equals("")){
                    enteredText.setError("Please Enter you message");
                    enteredText.requestFocus();
                }
                else{
                    String lang = enteredText.getText().toString();
                    finalResult.append(lang+"\n");
                    conversation.add(TextMessage.createForLocalUser(lang,System.currentTimeMillis()));
                    smartReply(conversation);
                }
            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalResult.setText("");
            }
        });
    }

    public void smartReply(List<TextMessage> convo){
        SmartReplyGenerator smartReply = com.google.mlkit.nl.smartreply.SmartReply.getClient();
        smartReply.suggestReplies(convo)
                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                    @Override
                    public void onSuccess(SmartReplySuggestionResult result) {
                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                            Toast.makeText(SmartReply.this,"No Suggestions!",Toast.LENGTH_LONG).show();
                            // The conversation's language isn't supported, so
                            // the result doesn't contain any suggestions.
                        } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                            finalResult.append("Suggestions: \n");
                            for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                finalResult.append(suggestion.getText());
                                finalResult.append("\n");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}