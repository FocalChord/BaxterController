package com.example.dinith.baxtercontroller;
// Author: Dinith Wannigama

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Button jointsActivityBtn = findViewById(R.id.jointsActivityBtn);
        jointsActivityBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, JointsActivity.class)));


        Button xyzActivityBtn = findViewById(R.id.xyzActivityBtn);
        xyzActivityBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, XyzActivity.class)));


        Button scriptsActivityBtn = findViewById(R.id.scriptsActivityBtn);
        scriptsActivityBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ScriptsActivity.class)));


        Button cameraActivityBtn = findViewById(R.id.cameraActivityBtn);
        cameraActivityBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CameraActivity.class)));


        Button settingsActivityBtn = findViewById(R.id.settingsActivityBtn);
        settingsActivityBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

    }

}
