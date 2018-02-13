package com.example.dinith.baxtercontroller;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import static com.example.dinith.baxtercontroller.MainActivity.isConnected;

public class SettingsActivity extends AppCompatActivity {

    public static String host;
    private String inputHost;
    public static int port;
    private int inputPort;
    private EditText enterIpText;
    private EditText enterPortText;
    TextView settingsConnectionStatusText;
    public static Socket client;
    private static boolean btnIsConnect = true;
    private static boolean hasConnectedBefore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        settingsConnectionStatusText = findViewById(R.id.settingsConnectionStatusText);
        if ((isConnected) && (host != null) && (port != 0)) {
            settingsConnectionStatusText.setText("Connected to: " + host + ":" + port);
            settingsConnectionStatusText.setTextColor(Color.rgb(50, 200, 50));
        }
        else
            settingsConnectionStatusText.setText("Not connected to a server.");

        enterIpText = findViewById(R.id.enterIpText);
        enterPortText = findViewById(R.id.enterPortText);

        Button connectBtn = findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(v -> {
            if (btnIsConnect) {
                try {
                    inputHost = enterIpText.getText().toString();
                    if (!(enterPortText.getText().toString().equals("")))
                        inputPort = Integer.parseInt(enterPortText.getText().toString());
                    else
                        inputPort = 0;

                    testConnection();
                    Thread.sleep(500);      // Delay thread so isConnected can update on testConnection() thread
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                if (isConnected) {
                    hasConnectedBefore = true;
                    settingsConnectionStatusText.setText("Connected to: " + host + ":" + port);
                    settingsConnectionStatusText.setTextColor(Color.rgb(50, 200, 50));
                    // Refresh activity to update the screen
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
                else {
                    settingsConnectionStatusText.setTextColor(Color.rgb(200, 50, 50));
                    if ((inputHost.equals("")) && (inputPort != 0))
                        settingsConnectionStatusText.setText("Please enter a valid host IP");
                    else if (!(inputHost.equals("")) && (inputPort == 0))
                        settingsConnectionStatusText.setText("Please enter a valid port number");
                    else if ((inputHost.equals("")) && (inputPort == 0))
                        settingsConnectionStatusText.setText("Please enter a valid host IP and port number");
                    else
                        settingsConnectionStatusText.setText("Unable to connect to the server");
                }
            }
            else {
                try {
                    client.close();
                    isConnected = false;
                    hasConnectedBefore = false;
                    host = "";
                    port = 0;
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
                catch (Exception e) {
                     e.printStackTrace();
                }
            }
        });

        isConnectedTest();

        if (isConnected) {
            enterIpText.setText(host);
            enterPortText.setText(Integer.toString(port));
            enterIpText.setHint(host);
            enterPortText.setHint(Integer.toString(port));
            connectBtn.setText("Disconnect");
            btnIsConnect = false;
        }
        else if (hasConnectedBefore) {
            enterIpText.setText(host);
            enterPortText.setText(Integer.toString(port));
            btnIsConnect = true;
        }
        else {
            host = inputHost;
            port = inputPort;
            btnIsConnect = true;
        }

    }

    public void testConnection() {
        new Thread(() -> {
            try {
                host = inputHost;
                port = inputPort;
                client = new Socket(host, port);
                isConnected = true;
            }
            catch (Exception e) {
                e.printStackTrace();
                isConnected = false;
            }
        }).start();
    }

    public void isConnectedTest() {
        new Thread(() -> {
            while (true) {
                try {
                    client = new Socket(host, port);
                    isConnected = true;
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    out.write("do_nothing\n");
                    out.flush();
                    out.close();
                    client.close();
                    Thread.sleep(1250);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    isConnected = false;
                    break;
                }
            }
        }).start();
    }

}
