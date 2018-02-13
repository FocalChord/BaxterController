package com.example.dinith.baxtercontroller;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import static com.example.dinith.baxtercontroller.MainActivity.isConnected;
import static com.example.dinith.baxtercontroller.SettingsActivity.client;
import static com.example.dinith.baxtercontroller.SettingsActivity.host;
import static com.example.dinith.baxtercontroller.SettingsActivity.port;

public class ScriptsActivity extends AppCompatActivity {
    TextView scriptsConnectionStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scripts);

        scriptsConnectionStatusText = findViewById(R.id.scriptsConnectionStatusText);
        scriptsStatusChange();

        Button headNod = findViewById(R.id.scriptHeadNodBtn);
        headNod.setOnClickListener(v -> {
            if (isConnected) {
                new Thread(() -> {
                    try {
                        client = new Socket(host, port);
                        BufferedWriter out4 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        out4.write("head_nod head\n");
                        out4.flush();
                        out4.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });

    }

    public void scriptsStatusChange() {
        new Thread(() -> {
            while (true) {
                try {
                    if (isConnected) {
                        scriptsConnectionStatusText.setText("Connected to: " + host + ":" + Integer.toString(port));
                        scriptsConnectionStatusText.setTextColor(Color.rgb(50, 200, 50));
                    } else {
                        scriptsConnectionStatusText.setText("Not connected!");
                        scriptsConnectionStatusText.setTextColor(Color.rgb(200, 50, 50));
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


