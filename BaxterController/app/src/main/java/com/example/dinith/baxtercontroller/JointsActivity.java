package com.example.dinith.baxtercontroller;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import static java.lang.Math.PI;
import static com.example.dinith.baxtercontroller.MainActivity.isConnected;
import static com.example.dinith.baxtercontroller.SettingsActivity.client;
import static com.example.dinith.baxtercontroller.SettingsActivity.host;
import static com.example.dinith.baxtercontroller.SettingsActivity.port;


public class JointsActivity extends AppCompatActivity implements JoystickView.JoystickListener {

    private static String currentLimb = "left_limb";
    private static int currentLimbID = 0;
    private static boolean armLeftIsReady = true;
    private static boolean armRightIsReady = true;
    private static boolean gripperLeftOpen = true;
    private static boolean gripperRightOpen = true;
    private static double headTheta[] = new double[1];
    private static boolean headBtnPressed = false;
    private static boolean headIsReady = true;
    private static String inString;
    TextView jointsConnectionStatusText;
    TextView xPercentJoints;
    TextView yPercentJoints;
    private static double wristValue = 0.025;
    private static int wristTime = 80;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_joints);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        jointsConnectionStatusText = findViewById(R.id.jointsConnectionStatusText);
        jointsStatusChange();

        currentLimbID = 0;
        currentLimb = "left_limb";
        getHeadPos();



        Button headLeftBtn = findViewById(R.id.headLeftBtnJoints);
        headLeftBtn.setOnTouchListener((v, event) -> {
            if (isConnected && headIsReady) {
                headTheta[0] += 0.025;
                new Thread(() -> {
                    try {
                        if (headTheta[0] >= PI / 2)
                            headTheta[0] = PI / 2;
                        client = new Socket(host, port);
                        BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        out1.write("head_setpan head " + Double.toString(headTheta[0]) + " 10.0\n");
                        headIsReady = false;
                        out1.flush();
                        headBtnPressed = true;
                        out1.close();
                        client.close();
                        Thread.sleep(75);
                        headIsReady = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        headTheta[0] -= 0.2;
                        headIsReady = true;
                    }
                }).start();
            }
            return true;
        });



        Button headRightBtn = findViewById(R.id.headRightBtnJoints);
        headRightBtn.setOnTouchListener((v, event) -> {
            if (isConnected && headIsReady) {
                headTheta[0] -= 0.025;
                new Thread(() -> {
                    try {
                        if (headTheta[0] <= -PI / 2)
                            headTheta[0] = -PI / 2;
                        client = new Socket(host, port);
                        BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        out1.write("head_setpan head " + Double.toString(headTheta[0]) + " 10.0\n");
                        headIsReady = false;
                        out1.flush();
                        headBtnPressed = true;
                        out1.close();
                        client.close();
                        Thread.sleep(75);
                        headIsReady = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        headTheta[0] -= 0.025;
                        headIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Switch armSwitch = findViewById(R.id.armSwitchJoints);
        armSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentLimb = "right_limb";
                currentLimbID = 1;
            }
            else {
                currentLimb = "left_limb";
                currentLimbID = 0;
            }
        });



        Button gripperLeftBtn = findViewById(R.id.gripperLeftBtnJoints);
        gripperLeftBtn.setOnClickListener(v -> {
            if (isConnected) {
                new Thread(() -> {
                    try {
                        client = new Socket(host, port);
                        BufferedWriter out3 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        if (gripperLeftOpen)
                            out3.write("limb_gripper left_limb close\n");
                        else
                            out3.write("limb_gripper left_limb open\n");
                        gripperLeftOpen = !gripperLeftOpen;
                        out3.flush();
                        out3.close();
                        client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        });



        Button gripperRightBtn = findViewById(R.id.gripperRightBtnJoints);
        gripperRightBtn.setOnClickListener(v -> {
            if (isConnected) {
                new Thread(() -> {
                    try {
                        client = new Socket(host, port);
                        BufferedWriter out3 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        if (gripperRightOpen)
                            out3.write("limb_gripper right_limb close\n");
                        else
                            out3.write("limb_gripper right_limb open\n");
                        gripperRightOpen = !gripperRightOpen;
                        out3.flush();
                        out3.close();
                        client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        });



        Button w0Btn = findViewById(R.id.w0Btn);
        w0Btn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "4" + " " + -wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;
                        out2.flush();
                        client5.close();

                        Thread.sleep(wristTime);

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Button w0Btn2 = findViewById(R.id.w0Btn2);
        w0Btn2.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "4" + " " + wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;
                        out2.flush();
                        client5.close();

                        Thread.sleep(wristTime);

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Button w1Btn = findViewById(R.id.w1Btn);
        w1Btn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "5" + " " + -wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;

                        out2.flush();
                        client5.close();

                        Thread.sleep(wristTime);

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Button w1Btn2 = findViewById(R.id.w1Btn2);
        w1Btn2.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "5" + " " + wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;

                        out2.flush();
                        client5.close();

                        Thread.sleep(wristTime);

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Button w2Btn = findViewById(R.id.w2Btn);
        w2Btn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "6" + " " + -wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;

                        out2.flush();
                        client5.close();

                        Thread.sleep(50);

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Button w2Btn2 = findViewById(R.id.w2Btn2);
        w2Btn2.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "6" + " " + wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;
                        out2.flush();
                        client5.close();

                        Thread.sleep(50);

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Button e0Btn = findViewById(R.id.e0Btn);
        e0Btn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "2" + " " + wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;

                        out2.flush();
                        out2.close();
                        client5.close();

                        Thread.sleep(wristTime);


                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Button e0Btn2 = findViewById(R.id.e0Btn2);
        e0Btn2.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "2" + " " + -wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;

                        out2.flush();
                        out2.close();
                        client5.close();

                        Thread.sleep(wristTime);


                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Button e1Btn = findViewById(R.id.e1Btn);
        e1Btn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "3" + " " + -wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;
                        out2.flush();
                        client5.close();

                        Thread.sleep(wristTime);

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });



        Button e1Btn2 = findViewById(R.id.e1Btn2);
        e1Btn2.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("move_single_joint " + currentLimb + " " + "3" + " " + wristValue + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;

                        out2.flush();
                        out2.close();
                        client5.close();

                        Thread.sleep(wristTime);

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(wristTime);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                }).start();
            }
            return false;
        });

    }

    @Override
    // Joystick used to control joints s0 (x direction on joystick) and s1 (y direction on joystick)
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        xPercentJoints = findViewById(R.id.xPercentJoints);
        yPercentJoints = findViewById(R.id.yPercentJoints);
        switch (id) {
            case R.id.joystickJoints:
                xPercentJoints.setText(Float.toString(xPercent));
                yPercentJoints.setText(Float.toString(yPercent));

                if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady)) && ((xPercent < -0.6) || (xPercent > 0.6) || (yPercent < -0.6) || (yPercent > 0.6))) {
                    new Thread(() -> {
                        try {
                            Socket client4 = new Socket(host, port);
                            BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client4.getOutputStream()));
                            String wrist_joint;
                            String value;

                            if ((xPercent > 0.6) && (currentLimbID == 0)) {
                                value = "-0.025";
                                wrist_joint = "0";
                            }
                            else if ((xPercent < -0.6) && (currentLimbID == 0)) {
                                value = "0.025";
                                wrist_joint = "0";
                            }
                            else if ((yPercent > 0.6) && (currentLimbID == 0)) {
                                value = "0.025";
                                wrist_joint = "1";
                            }
                            else if ((yPercent < -0.6) && (currentLimbID == 0)) {
                                value = "-0.025";
                                wrist_joint = "1";
                            }
                            else if ((xPercent > 0.6) && (currentLimbID == 1)) {
                                value = "-0.025";
                                wrist_joint = "0";
                            }
                            else if ((xPercent < -0.6) && (currentLimbID == 1)) {
                                value = "0.025";
                                wrist_joint = "0";
                            }
                            else if ((yPercent > 0.6) && (currentLimbID == 1)) {
                                value = "0.025";
                                wrist_joint = "1";
                            }
                            else /*if ((yPercent < -0.6) && (currentLimbID == 1))*/ {
                                value = "-0.025";
                                wrist_joint = "1";
                            }
                            out2.write("move_single_joint " + currentLimb + " " + wrist_joint + " " + value + "\n");

                            if (currentLimbID == 0)
                                armLeftIsReady = false;
                            else
                                armRightIsReady = false;
                            out2.flush();
                            client4.close();

                            Thread.sleep(wristTime);

                            if (!armLeftIsReady)
                                armLeftIsReady = true;
                            else if (!armRightIsReady)
                                armRightIsReady = true;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            try {
                                Thread.sleep(wristTime);
                            }
                            catch(Exception ex) {
                                ex.printStackTrace();
                            }
                            if (!armLeftIsReady)
                                armLeftIsReady = true;
                            else if (!armRightIsReady)
                                armRightIsReady = true;
                        }
                    }).start();
                }
                break;
        }
    }

    public void jointsStatusChange() {
        new Thread(() -> {
            while (true) {
                try {
                    if (isConnected) {
                        jointsConnectionStatusText.setText("Connected to: " + host + ":" + Integer.toString(port));
                        jointsConnectionStatusText.setTextColor(Color.rgb(50, 200, 50));
                    } else {
                        jointsConnectionStatusText.setText("Not connected!");
                        jointsConnectionStatusText.setTextColor(Color.rgb(200, 50, 50));
                    }
                    Thread.sleep(500);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getHeadPos() {
        // Determine current value of theta 10(ish) seconds after any head button has NOT been pressed
        new Thread(() -> {
            while (true) {
                if (isConnected && !headBtnPressed) {
                    for (int i = 0; i < 2; i++) {
                        try {
                            Socket client = new Socket(host, port);
                            BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                            out1.write("head_getpan head\n");
                            out1.flush();
                            BufferedReader in1 = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            inString = in1.readLine() + "\n";
                            in1.close();
                            headTheta[0] = Double.parseDouble(inString);
                            Thread.sleep(10000);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (isConnected && headBtnPressed) {
                    try {
                        Thread.sleep(10000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    headBtnPressed = false;
                }
                else
                    break;
            }
        }).start();
    }

}
