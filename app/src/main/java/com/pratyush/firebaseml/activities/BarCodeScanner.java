package com.pratyush.firebaseml.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
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
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.mindorks.paracamera.Camera;
import com.pratyush.firebaseml.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BarCodeScanner extends AppCompatActivity {

    private static final int REQ = 22;
    private int INT_CONST = 49;
    private ImageView imageView;
    private Uri uri;
    private Bitmap bitmap;
    private List<String> list;
    private TextView textView;
    private Camera camera;
    private static final String permissionCamera = Manifest.permission.CAMERA;
    private static final String permissionStorageRead = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String permissionStorageWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private String[] permissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_scanner);

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

                if(ContextCompat.checkSelfPermission(BarCodeScanner.this,permissionCamera)== PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(BarCodeScanner.this,permissionStorageRead)== PackageManager.PERMISSION_GRANTED){
                        if(ContextCompat.checkSelfPermission(BarCodeScanner.this,permissionStorageRead)== PackageManager.PERMISSION_GRANTED){
                            camera = new Camera.Builder()
                                    .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                                    .setTakePhotoRequestCode(1)
                                    .setDirectory("pics")
                                    .setName("ali_" + System.currentTimeMillis())
                                    .setImageFormat(Camera.IMAGE_JPEG)
                                    .setCompression(75)
                                    .setImageHeight(500)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                                    .build(BarCodeScanner.this);

                            try {
                                camera.takePicture();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else {
                    ActivityCompat.requestPermissions(BarCodeScanner.this,permissions,REQ);
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
            barCodeRecognition(bitmap);

        }
        else if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            Bitmap bitmap = camera.getCameraBitmap();
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                barCodeRecognition(bitmap);
            } else {
                Toast.makeText(this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void barCodeRecognition(Bitmap imageView) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageView);

        FirebaseVisionBarcodeDetector barCodeRecognizer = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();
        barCodeRecognizer.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        processBarcodeRecognitionResult(firebaseVisionBarcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }



    @SuppressLint("SetTextI18n")
    private void processBarcodeRecognitionResult(List<FirebaseVisionBarcode> barcodes){
        for (FirebaseVisionBarcode barcode: barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

            int valueType = barcode.getValueType();

            Toast.makeText(this, "barcode: "+rawValue, Toast.LENGTH_SHORT).show();

            textView.setText("Barcode: "+rawValue);

            // Log.d(TAG, "processBarcodeRecognitionResult: "+rawValue);
            // Log.d(TAG, "processBarcodeRecognitionResult: "+valueType);
            // See API reference for complete list of supported types
            switch (valueType) {
                case FirebaseVisionBarcode.TYPE_WIFI:
                    String ssid = barcode.getWifi().getSsid();
                    String password = barcode.getWifi().getPassword();
                    int type = barcode.getWifi().getEncryptionType();
                    textView.setText(ssid+ ": "+password);
                    break;
                case FirebaseVisionBarcode.TYPE_URL:
                    String title = barcode.getUrl().getTitle();
                    String url = barcode.getUrl().getUrl();
                    textView.setText(title+ ": "+url);
                    break;
            }
        }
    }

}