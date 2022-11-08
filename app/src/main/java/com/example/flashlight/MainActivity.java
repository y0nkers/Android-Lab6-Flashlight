package com.example.flashlight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Camera camera = null;
    private Camera.Parameters parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton flashlight_switch = findViewById(R.id.flashlight_switch);
        RelativeLayout layout = findViewById(R.id.flashlight_layout);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            flashlight_switch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked) {
                    if (camera == null) {
                        camera = Camera.open();
                        parameters = camera.getParameters();
                        List<String> flashModesList = parameters.getSupportedFlashModes();
                        if (flashModesList == null || !flashModesList.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                            showDialog(MainActivity.this);
                            return;
                        }

                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                        SurfaceTexture dummy = new SurfaceTexture(1);
                        try {
                            camera.setPreviewTexture(dummy);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        camera.startPreview();
                        layout.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.on));
                    }
                } else {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                    layout.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.off));
                    camera.release();
                    camera = null;
                }
            });
        } else showDialog(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
        }
    }

    public void showDialog(Context context) {
        AlertDialog alertDialog;
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context);
        builder.setMessage("Ваше устройство не поддерживает вспышку")
                .setCancelable(false)
                .setNeutralButton("Close", (dialog, which) -> finish());
        alertDialog = builder.create();
        alertDialog.show();
    }
}