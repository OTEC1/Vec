package qwikpay.com.ng.vectis_orbit.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import qwikpay.com.ng.vectis_orbit.R;
import qwikpay.com.ng.vectis_orbit.UTILS.CameraXS;


public class Screen extends AppCompatActivity {


    private CameraXS preview;
    private String TAG = "Screen";
    private int o = 0;
    private long file_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
    }

    public void Close() {
        preview.camera.cancelAutoFocus();
        preview.camera.release();
        preview.camera.stopPreview();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            file_name = System.currentTimeMillis();
            preview = new CameraXS(this, file_name);
            ((FrameLayout) findViewById(R.id.preview_layout)).addView(preview);
            TimerStart();
        }
    }


    Camera.ShutterCallback shutterCallback = () -> Log.d(TAG, "onShutter");


    Camera.PictureCallback rawCallback = (data, camera) -> Log.d(TAG, "onPictureTaken");


    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream;
            try {
                outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", file_name));
                outStream.write(data);
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Close();
            }
        }
    };


    private void TimerStart() {
        TimerTask task = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (o == 2)
                        preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                    o++;
                });
            }
        };
        Timer time = new Timer();
        time.schedule(task, 0, 1000);
    }


}