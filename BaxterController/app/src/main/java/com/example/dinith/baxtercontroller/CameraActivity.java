package com.example.dinith.baxtercontroller;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import static com.example.dinith.baxtercontroller.MainActivity.isConnected;
import static com.example.dinith.baxtercontroller.SettingsActivity.host;
import static com.example.dinith.baxtercontroller.SettingsActivity.port;

public class CameraActivity extends AppCompatActivity {
    TextView cameraConnectionStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        cameraStatusChange();

        cameraConnectionStatusText = findViewById(R.id.cameraConnectionStatusText);
        if (isConnected) {
            cameraConnectionStatusText.setText("Connected to: " + host + ":" + Integer.toString(port));
            cameraConnectionStatusText.setTextColor(Color.rgb(50, 200, 50));
        }
        else {
            cameraConnectionStatusText.setText("Not connected!");
            cameraConnectionStatusText.setTextColor(Color.rgb(200, 50, 50));
        }

    }

    public void cameraStatusChange() {
        new Thread(() -> {
            while (true) {
                try {
                    if (isConnected) {
                        cameraConnectionStatusText.setText("Connected to: " + host + ":" + Integer.toString(port));
                        cameraConnectionStatusText.setTextColor(Color.rgb(50, 200, 50));
                    } else {
                        cameraConnectionStatusText.setText("Not connected!");
                        cameraConnectionStatusText.setTextColor(Color.rgb(200, 50, 50));
                        break;
                    }
                    Thread.sleep(1000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
