package com.hack.android.medassist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hack.android.medassist.models.PrescriptionModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrescriptionActivity  extends AppCompatActivity {

//    private PrescriptionModel prescriptionModel;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String prescriptionJson = getIntent().getStringExtra("prescription");
        Gson gson = new Gson();
        PrescriptionModel prescriptionModel = gson.fromJson(prescriptionJson, PrescriptionModel.class);
        setContentView(R.layout.activity_prescription_details);
        setActionItems(prescriptionModel);
    }

    private void setActionItems(PrescriptionModel prescriptionModel) {
        EditText medicineText = (EditText)findViewById(R.id.medicineText);
        EditText dosageText = (EditText)findViewById(R.id.dosageText);
        EditText frequencyText = (EditText)findViewById(R.id.frequencyText);
        EditText durationText = (EditText)findViewById(R.id.durationText);
        mImageView = (ImageView) findViewById(R.id.photoImageView);

        medicineText.setText(prescriptionModel.getPrescription()[0].getFields().getMedicine());
        dosageText.setText(prescriptionModel.getPrescription()[0].getFields().getDosage());
        frequencyText.setText(prescriptionModel.getPrescription()[0].getFields().getFrequency());
        durationText.setText(prescriptionModel.getPrescription()[0].getFields().getDuration());
    }

    public void onImageCaptureClicked(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("TAG", "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                mImageView.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}