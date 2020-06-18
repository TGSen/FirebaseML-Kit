package com.pratyush.firebaseml.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.mindorks.paracamera.Camera;
import com.pratyush.firebaseml.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageLabeling extends AppCompatActivity {
    private int INT_CONST = 49;
    private int REQ = 983;
    private ImageView imageView;
    private Uri uri;
    private Bitmap bitmap;
    private List<String> list;
    private StringBuilder stringBuilder;
    private TextView textView;
    private static final String permissionCamera = Manifest.permission.CAMERA;
    private static final String permissionStorageRead = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String permissionStorageWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private String[] permissions;

    private Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_labeling);
        permissions = new String[]{permissionCamera, permissionStorageRead, permissionStorageWrite};
        imageView = findViewById(R.id.imageView);
        list = new ArrayList<>();
        textView = findViewById(R.id.text);
        findViewById(R.id.choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,INT_CONST);
            }
        });

        findViewById(R.id.takePic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ImageLabeling.this,permissionCamera)== PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(ImageLabeling.this,permissionStorageRead)== PackageManager.PERMISSION_GRANTED){
                        if(ContextCompat.checkSelfPermission(ImageLabeling.this,permissionStorageRead)== PackageManager.PERMISSION_GRANTED){
                            camera = new Camera.Builder()
                                    .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                                    .setTakePhotoRequestCode(1)
                                    .setDirectory("pics")
                                    .setName("ali_" + System.currentTimeMillis())
                                    .setImageFormat(Camera.IMAGE_JPEG)
                                    .setCompression(75)
                                    .setImageHeight(500)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                                    .build(ImageLabeling.this);

                            try {
                                camera.takePicture();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else {
                    ActivityCompat.requestPermissions(ImageLabeling.this,permissions,REQ);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==INT_CONST && resultCode==RESULT_OK && data.getData()!=null){
            uri=data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
            imageLabelling(bitmap);

        }
        else if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            Bitmap bitmap = camera.getCameraBitmap();
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                imageLabelling(bitmap);
            } else {
                Toast.makeText(this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void imageLabelling(Bitmap bitmap){
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionImageLabeler firebaseVisionImageLabel = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        firebaseVisionImageLabel.processImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                        imageLabellingResults(firebaseVisionImageLabels);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void imageLabellingResults(List<FirebaseVisionImageLabel> firebaseVisionImageLabels){
        textView.setText("");
        for(FirebaseVisionImageLabel imageLabel:firebaseVisionImageLabels){
            textView.append(imageLabel.getText()+" "+imageLabel.getConfidence()+"\n");
        }
    }
}