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
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static com.example.dinith.baxtercontroller.MainActivity.isConnected;
import static com.example.dinith.baxtercontroller.SettingsActivity.client;
import static com.example.dinith.baxtercontroller.SettingsActivity.host;
import static com.example.dinith.baxtercontroller.SettingsActivity.port;


public class XyzActivity extends AppCompatActivity implements JoystickView.JoystickListener {

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
    TextView xyzConnectionStatusText;
    TextView xPercentXyz;
    TextView yPercentXyz;
    // The arrays use index 0 for left arm and index 1 for right arm
    private static double px[] = new double[2];
    private static double py[] = new double[2];
    private static double pz[] = new double[2];
    private static double pr[] = new double[2];
    private static double pp[] = new double[2];
    private static double pya[] = new double[2];
    private static double pxMove[] = new double[2];
    private static double pyMove[] = new double[2];
    private static double pzMove[] = new double[2];
    private static double prMove[] = new double[2];
    private static double ppMove[] = new double[2];
    private static double pyaMove[] = new double[2];
    private static String armPosRight;
    private static String armPosLeft;
    private static String[] armPosRightArray = new String[6];
    private static String[] armPosLeftArray = new String[6];


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_xyz);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Toast.makeText(getApplicationContext(), "USE WITH CAUTION", Toast.LENGTH_SHORT).show();

        xyzConnectionStatusText = findViewById(R.id.xyzConnectionStatusText);
        xyzStatusChange();

        currentLimbID = 0;
        currentLimb = "left_limb";
        getHeadPos();
        getArmPos();



        Button headLeftBtn = findViewById(R.id.headLeftBtnXyz);
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



        Button headRightBtn = findViewById(R.id.headRightBtnXyz);
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



        Switch armSwitch = findViewById(R.id.armSwitchXyz);
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



        Button gripperLeftBtn = findViewById(R.id.gripperLeftBtnXyz);
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



        Button gripperRightBtn = findViewById(R.id.gripperRightBtnXyz);
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



        Button rollBtn = findViewById(R.id.rollBtn);
        rollBtn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                getArmPos();
                for (int n = 0; n < 2; n++) {
                    pxMove[n] = px[n];
                    pyMove[n] = py[n];
                    pzMove[n] = pz[n];
                    prMove[n] = pr[n];
                    ppMove[n] = pp[n];
                    pyaMove[n] = pya[n];
                }
                new Thread(() -> {
                    try {
                        prMove[currentLimbID] += 0.075;
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("limb_moveto " + currentLimb + " " + Double.toString(pxMove[currentLimbID]) + " " + Double.toString(pyMove[currentLimbID]) + " " + Double.toString(pzMove[currentLimbID]) + " " + Double.toString(prMove[currentLimbID]) + " " + Double.toString(ppMove[currentLimbID]) + " " + Double.toString(pyaMove[currentLimbID]) + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;
                        out2.flush();
                        client5.close();

                        long startTime = System.currentTimeMillis();
                        do {
                            if (!isConnected)
                                break;
                            // The commented code below was originally used for the set_joint_positions() method on baxter_arm_control.py
                            //Thread.sleep(250);
                            //getArmPos();
                        } while (((System.currentTimeMillis() - startTime) < 25) && (!(abs(px[0] - pxMove[0]) < 0.005) || !(abs(py[0] - pyMove[0]) < 0.005) || !(abs(pz[0] - pzMove[0]) < 0.005) || !(abs(pr[0] - prMove[0]) < 0.005) || !(abs(pp[0] - ppMove[0]) < 0.005) || !(abs(pya[0] - pyaMove[0]) < 0.005) || !(abs(px[1] - pxMove[1]) < 0.005) || !(abs(py[1] - pyMove[1]) < 0.005) || !(abs(pz[1] - pzMove[1]) < 0.005) || !(abs(pr[1] - prMove[1]) < 0.005) || !(abs(pp[1] - ppMove[1]) < 0.005) || !(abs(pya[1] - pyaMove[1]) < 0.005)));

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(250);
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



        Button rollBtn2 = findViewById(R.id.rollBtn2);
        rollBtn2.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                getArmPos();
                for (int n = 0; n < 2; n++) {
                    pxMove[n] = px[n];
                    pyMove[n] = py[n];
                    pzMove[n] = pz[n];
                    prMove[n] = pr[n];
                    ppMove[n] = pp[n];
                    pyaMove[n] = pya[n];
                }
                new Thread(() -> {
                    try {
                        prMove[currentLimbID] -= 0.075;
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("limb_moveto " + currentLimb + " " + Double.toString(pxMove[currentLimbID]) + " " + Double.toString(pyMove[currentLimbID]) + " " + Double.toString(pzMove[currentLimbID]) + " " + Double.toString(prMove[currentLimbID]) + " " + Double.toString(ppMove[currentLimbID]) + " " + Double.toString(pyaMove[currentLimbID]) + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;
                        out2.flush();
                        client5.close();

                       long startTime = System.currentTimeMillis();
                        do {
                            if (!isConnected)
                                break;
                            // The commented code below was originally used for the set_joint_positions() method on baxter_arm_control.py
                            //Thread.sleep(250);
                            //getArmPos();
                        } while (((System.currentTimeMillis() - startTime) < 25) && (!(abs(px[0] - pxMove[0]) < 0.005) || !(abs(py[0] - pyMove[0]) < 0.005) || !(abs(pz[0] - pzMove[0]) < 0.005) || !(abs(pr[0] - prMove[0]) < 0.005) || !(abs(pp[0] - ppMove[0]) < 0.005) || !(abs(pya[0] - pyaMove[0]) < 0.005) || !(abs(px[1] - pxMove[1]) < 0.005) || !(abs(py[1] - pyMove[1]) < 0.005) || !(abs(pz[1] - pzMove[1]) < 0.005) || !(abs(pr[1] - prMove[1]) < 0.005) || !(abs(pp[1] - ppMove[1]) < 0.005) || !(abs(pya[1] - pyaMove[1]) < 0.005)));

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(250);
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



        Button pitchBtn = findViewById(R.id.pitchBtn);
        pitchBtn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                getArmPos();
                for (int n = 0; n < 2; n++) {
                    pxMove[n] = px[n];
                    pyMove[n] = py[n];
                    pzMove[n] = pz[n];
                    prMove[n] = pr[n];
                    ppMove[n] = pp[n];
                    pyaMove[n] = pya[n];
                }
                new Thread(() -> {
                    try {
                        ppMove[currentLimbID] += 0.075;
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("limb_moveto " + currentLimb + " " + Double.toString(pxMove[currentLimbID]) + " " + Double.toString(pyMove[currentLimbID]) + " " + Double.toString(pzMove[currentLimbID]) + " " + Double.toString(prMove[currentLimbID]) + " " + Double.toString(ppMove[currentLimbID]) + " " + Double.toString(pyaMove[currentLimbID]) + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;

                        out2.flush();
                        client5.close();

                        long startTime = System.currentTimeMillis();
                        do {
                            if (!isConnected)
                                break;
                            // The commented code below was originally used for the set_joint_positions() method on baxter_arm_control.py
                            //Thread.sleep(250);
                            //getArmPos();
                        } while (((System.currentTimeMillis() - startTime) < 25) && (!(abs(px[0] - pxMove[0]) < 0.005) || !(abs(py[0] - pyMove[0]) < 0.005) || !(abs(pz[0] - pzMove[0]) < 0.005) || !(abs(pr[0] - prMove[0]) < 0.005) || !(abs(pp[0] - ppMove[0]) < 0.005) || !(abs(pya[0] - pyaMove[0]) < 0.005) || !(abs(px[1] - pxMove[1]) < 0.005) || !(abs(py[1] - pyMove[1]) < 0.005) || !(abs(pz[1] - pzMove[1]) < 0.005) || !(abs(pr[1] - prMove[1]) < 0.005) || !(abs(pp[1] - ppMove[1]) < 0.005) || !(abs(pya[1] - pyaMove[1]) < 0.005)));

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(250);
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



        Button pitchBtn2 = findViewById(R.id.pitchBtn2);
        pitchBtn2.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                getArmPos();
                for (int n = 0; n < 2; n++) {
                    pxMove[n] = px[n];
                    pyMove[n] = py[n];
                    pzMove[n] = pz[n];
                    prMove[n] = pr[n];
                    ppMove[n] = pp[n];
                    pyaMove[n] = pya[n];
                }
                new Thread(() -> {
                    try {
                        ppMove[currentLimbID] -= 0.075;
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("limb_moveto " + currentLimb + " " + Double.toString(pxMove[currentLimbID]) + " " + Double.toString(pyMove[currentLimbID]) + " " + Double.toString(pzMove[currentLimbID]) + " " + Double.toString(prMove[currentLimbID]) + " " + Double.toString(ppMove[currentLimbID]) + " " + Double.toString(pyaMove[currentLimbID]) + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;

                        out2.flush();
                        client5.close();

                        long startTime = System.currentTimeMillis();
                        do {
                            if (!isConnected)
                                break;
                            // The commented code below was originally used for the set_joint_positions() method on baxter_arm_control.py
                            //Thread.sleep(250);
                            //getArmPos();
                        } while (((System.currentTimeMillis() - startTime) < 25) && (!(abs(px[0] - pxMove[0]) < 0.005) || !(abs(py[0] - pyMove[0]) < 0.005) || !(abs(pz[0] - pzMove[0]) < 0.005) || !(abs(pr[0] - prMove[0]) < 0.005) || !(abs(pp[0] - ppMove[0]) < 0.005) || !(abs(pya[0] - pyaMove[0]) < 0.005) || !(abs(px[1] - pxMove[1]) < 0.005) || !(abs(py[1] - pyMove[1]) < 0.005) || !(abs(pz[1] - pzMove[1]) < 0.005) || !(abs(pr[1] - prMove[1]) < 0.005) || !(abs(pp[1] - ppMove[1]) < 0.005) || !(abs(pya[1] - pyaMove[1]) < 0.005)));

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(250);
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



        Button yawBtn = findViewById(R.id.yawBtn);
        yawBtn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                getArmPos();
                for (int n = 0; n < 2; n++) {
                    pxMove[n] = px[n];
                    pyMove[n] = py[n];
                    pzMove[n] = pz[n];
                    prMove[n] = pr[n];
                    ppMove[n] = pp[n];
                    pyaMove[n] = pya[n];
                }
                new Thread(() -> {
                    try {
                        pyaMove[currentLimbID] += 0.075;
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("limb_moveto " + currentLimb + " " + Double.toString(pxMove[currentLimbID]) + " " + Double.toString(pyMove[currentLimbID]) + " " + Double.toString(pzMove[currentLimbID]) + " " + Double.toString(prMove[currentLimbID]) + " " + Double.toString(ppMove[currentLimbID]) + " " + Double.toString(pyaMove[currentLimbID]) + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;

                        out2.flush();
                        client5.close();

                       long startTime = System.currentTimeMillis();
                        do {
                            if (!isConnected)
                                break;
                            // The commented code below was originally used for the set_joint_positions() method on baxter_arm_control.py
                            //Thread.sleep(250);
                            //getArmPos();
                        } while (((System.currentTimeMillis() - startTime) < 25) && (!(abs(px[0] - pxMove[0]) < 0.005) || !(abs(py[0] - pyMove[0]) < 0.005) || !(abs(pz[0] - pzMove[0]) < 0.005) || !(abs(pr[0] - prMove[0]) < 0.005) || !(abs(pp[0] - ppMove[0]) < 0.005) || !(abs(pya[0] - pyaMove[0]) < 0.005) || !(abs(px[1] - pxMove[1]) < 0.005) || !(abs(py[1] - pyMove[1]) < 0.005) || !(abs(pz[1] - pzMove[1]) < 0.005) || !(abs(pr[1] - prMove[1]) < 0.005) || !(abs(pp[1] - ppMove[1]) < 0.005) || !(abs(pya[1] - pyaMove[1]) < 0.005)));

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(250);
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



        Button yawBtn2 = findViewById(R.id.yawBtn2);
        yawBtn2.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                new Thread(() -> {
                    try {
                        pyaMove[currentLimbID] -= 0.075;
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("limb_moveto " + currentLimb + " " + Double.toString(pxMove[currentLimbID]) + " " + Double.toString(pyMove[currentLimbID]) + " " + Double.toString(pzMove[currentLimbID]) + " " + Double.toString(prMove[currentLimbID]) + " " + Double.toString(ppMove[currentLimbID]) + " " + Double.toString(pyaMove[currentLimbID]) + "\n");

                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;
                        out2.flush();
                        client5.close();

                        long startTime = System.currentTimeMillis();
                        do {
                            if (!isConnected)
                                break;
                            // The commented code below was originally used for the set_joint_positions() method on baxter_arm_control.py
                            //Thread.sleep(250);
                            //getArmPos();
                        } while (((System.currentTimeMillis() - startTime) < 25) && (!(abs(px[0] - pxMove[0]) < 0.005) || !(abs(py[0] - pyMove[0]) < 0.005) || !(abs(pz[0] - pzMove[0]) < 0.005) || !(abs(pr[0] - prMove[0]) < 0.005) || !(abs(pp[0] - ppMove[0]) < 0.005) || !(abs(pya[0] - pyaMove[0]) < 0.005) || !(abs(px[1] - pxMove[1]) < 0.005) || !(abs(py[1] - pyMove[1]) < 0.005) || !(abs(pz[1] - pzMove[1]) < 0.005) || !(abs(pr[1] - prMove[1]) < 0.005) || !(abs(pp[1] - ppMove[1]) < 0.005) || !(abs(pya[1] - pyaMove[1]) < 0.005)));

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(250);
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



        Button zUpBtn = findViewById(R.id.zUpBtn);
        zUpBtn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                getArmPos();
                for (int n = 0; n < 2; n++) {
                    pxMove[n] = px[n];
                    pyMove[n] = py[n];
                    pzMove[n] = pz[n];
                    prMove[n] = pr[n];
                    ppMove[n] = pp[n];
                    pyaMove[n] = pya[n];
                }
                new Thread(() -> {
                    try {
                        pzMove[currentLimbID] += 0.015;
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));

                        out2.write("limb_moveto " + currentLimb + " " + Double.toString(pxMove[currentLimbID]) + " " + Double.toString(pyMove[currentLimbID]) + " " + Double.toString(pzMove[currentLimbID]) + " " + Double.toString(prMove[currentLimbID]) + " " + Double.toString(ppMove[currentLimbID]) + " " + Double.toString(pyaMove[currentLimbID]) + "\n");
                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;
                        out2.flush();
                        client5.close();

                        long startTime = System.currentTimeMillis();
                        do {
                            if (!isConnected)
                                break;
                            // The commented code below was originally used for the set_joint_positions() method on baxter_arm_control.py
                            //Thread.sleep(100);
                            //getArmPos();
                        } while (((System.currentTimeMillis() - startTime) < 25) && (!(abs(px[0] - pxMove[0]) < 0.005) || !(abs(py[0] - pyMove[0]) < 0.005) || !(abs(pz[0] - pzMove[0]) < 0.005) || !(abs(pr[0] - prMove[0]) < 0.005) || !(abs(pp[0] - ppMove[0]) < 0.005) || !(abs(pya[0] - pyaMove[0]) < 0.005) || !(abs(px[1] - pxMove[1]) < 0.005) || !(abs(py[1] - pyMove[1]) < 0.005) || !(abs(pz[1] - pzMove[1]) < 0.005) || !(abs(pr[1] - prMove[1]) < 0.005) || !(abs(pp[1] - ppMove[1]) < 0.005) || !(abs(pya[1] - pyaMove[1]) < 0.005)));

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(250);
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



        Button zDownBtn = findViewById(R.id.zDownBtn);
        zDownBtn.setOnTouchListener((v, event) -> {
            if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady))) {
                getArmPos();
                for (int n = 0; n < 2; n++) {
                    pxMove[n] = px[n];
                    pyMove[n] = py[n];
                    pzMove[n] = pz[n];
                    prMove[n] = pr[n];
                    ppMove[n] = pp[n];
                    pyaMove[n] = pya[n];
                }
                new Thread(() -> {
                    try {
                        pzMove[currentLimbID] -= 0.015;
                        Socket client5 = new Socket(host, port);
                        BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client5.getOutputStream()));
                        out2.write("limb_moveto " + currentLimb + " " + Double.toString(pxMove[currentLimbID]) + " " + Double.toString(pyMove[currentLimbID]) + " " + Double.toString(pzMove[currentLimbID]) + " " + Double.toString(prMove[currentLimbID]) + " " + Double.toString(ppMove[currentLimbID]) + " " + Double.toString(pyaMove[currentLimbID]) + "\n");
                        if (currentLimbID == 0)
                            armLeftIsReady = false;
                        else
                            armRightIsReady = false;
                        out2.flush();
                        out2.close();
                        client5.close();

                        long startTime = System.currentTimeMillis();
                        do {
                            if (!isConnected)
                                break;
                            // The commented code below was originally used for the set_joint_positions() method on baxter_arm_control.py
                            //Thread.sleep(100);
                            //getArmPos();
                        } while (((System.currentTimeMillis() - startTime) < 25) && (!(abs(px[0] - pxMove[0]) < 0.01) || !(abs(py[0] - pyMove[0]) < 0.01) || !(abs(pz[0] - pzMove[0]) < 0.01) || !(abs(pr[0] - prMove[0]) < 0.01) || !(abs(pp[0] - ppMove[0]) < 0.01) || !(abs(pya[0] - pyaMove[0]) < 0.01) || !(abs(px[1] - pxMove[1]) < 0.01) || !(abs(py[1] - pyMove[1]) < 0.01) || !(abs(pz[1] - pzMove[1]) < 0.01) || !(abs(pr[1] - prMove[1]) < 0.01) || !(abs(pp[1] - ppMove[1]) < 0.01) || !(abs(pya[1] - pyaMove[1]) < 0.01)));

                        if (!armLeftIsReady)
                            armLeftIsReady = true;
                        else if (!armRightIsReady)
                            armRightIsReady = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(250);
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
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        xPercentXyz = findViewById(R.id.xPercentXyz);
        yPercentXyz = findViewById(R.id.yPercentXyz);
        switch (id) {
            case R.id.joystickXyz:
                xPercentXyz.setText(Float.toString(xPercent));
                yPercentXyz.setText(Float.toString(yPercent));

                if (isConnected && (((currentLimbID == 0) && armLeftIsReady) || ((currentLimbID == 1) && armRightIsReady)) && ((xPercent < -0.6) || (xPercent > 0.6) || (yPercent < -0.6) || (yPercent > 0.6))) {
                    getArmPos();
                    for (int n = 0; n < 2; n++) {
                        pxMove[n] = px[n];
                        pyMove[n] = py[n];
                        pzMove[n] = pz[n];
                        prMove[n] = pr[n];
                        ppMove[n] = pp[n];
                        pyaMove[n] = pya[n];
                    }
                    new Thread(() -> {
                        try {
                            // xPercent corresponds to py and yPercent to px
                                if (xPercent > 0.6)
                                    pyMove[currentLimbID] -= 0.015;
                                else if (xPercent < -0.6)
                                    pyMove[currentLimbID] += 0.015;

                                if (yPercent > 0.6)
                                    pxMove[currentLimbID] -= 0.015;
                                else if (yPercent < -0.6)
                                    pxMove[currentLimbID] += 0.015;

                            Socket client4 = new Socket(host, port);
                            BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client4.getOutputStream()));
                            out2.write("limb_moveto " + currentLimb + " " + Double.toString(pxMove[currentLimbID]) + " " + Double.toString(pyMove[currentLimbID]) + " " + Double.toString(pzMove[currentLimbID]) + " " + Double.toString(prMove[currentLimbID]) + " " + Double.toString(ppMove[currentLimbID]) + " " + Double.toString(pyaMove[currentLimbID]) + "\n");
                            if (currentLimbID == 0)
                                armLeftIsReady = false;
                            else
                                armRightIsReady = false;
                            out2.flush();
                            client4.close();

                            long startTime = System.currentTimeMillis();
                            do {
                                if (!isConnected)
                                    break;
                                // The commented code below was originally used for the set_joint_positions() method on baxter_arm_control.py
                                //getArmPos();
                                //Thread.sleep(100);
                            } while (((System.currentTimeMillis() - startTime) < 25) && (!(abs(px[0] - pxMove[0]) < 0.01) || !(abs(py[0] - pyMove[0]) < 0.01) || !(abs(pz[0] - pzMove[0]) < 0.01) || !(abs(pr[0] - prMove[0]) < 0.01) || !(abs(pp[0] - ppMove[0]) < 0.01) || !(abs(pya[0] - pyaMove[0]) < 0.01) || !(abs(px[1] - pxMove[1]) < 0.01) || !(abs(py[1] - pyMove[1]) < 0.01) || !(abs(pz[1] - pzMove[1]) < 0.01) || !(abs(pr[1] - prMove[1]) < 0.01) || !(abs(pp[1] - ppMove[1]) < 0.01) || !(abs(pya[1] - pyaMove[1]) < 0.01)));

                            if (!armLeftIsReady)
                                armLeftIsReady = true;
                            else if (!armRightIsReady)
                                armRightIsReady = true;

                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                Thread.sleep(250);
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

    public void xyzStatusChange() {
        new Thread(() -> {
            while (true) {
                try {
                    if (isConnected) {
                        xyzConnectionStatusText.setText("Connected to: " + host + ":" + Integer.toString(port));
                        xyzConnectionStatusText.setTextColor(Color.rgb(50, 200, 50));
                    } else {
                        xyzConnectionStatusText.setText("Not connected!");
                        xyzConnectionStatusText.setTextColor(Color.rgb(200, 50, 50));
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
        // Determine current value of theta 10 seconds after any head button has NOT been pressed
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
                else
                    break;
            }
        }).start();
    }

    public void getArmPos() {
        // Determine current arm coordinates/angles for both arms
        if (isConnected) {
            new Thread(() -> {
                try {
                    Socket client3 = new Socket(host, port);
                    BufferedWriter out5 = new BufferedWriter(new OutputStreamWriter(client3.getOutputStream()));
                    out5.write("limb_getpose left_limb\n");
                    out5.flush();
                    BufferedReader in5 = new BufferedReader(new InputStreamReader(client3.getInputStream()));
                    armPosLeft = in5.readLine();
                    in5.close();

                    int j = 1;
                    int k = 0;
                    for (int i = 1; i < armPosLeft.length(); i++) {
                        if ((armPosLeft.charAt(i) != 45) && (armPosLeft.charAt(i) != 46) && !((armPosLeft.charAt(i) >= 48) && (armPosLeft.charAt(i) <= 57))) {
                            armPosLeftArray[k] = armPosLeft.substring(j, i) + "\n";
                            i++;
                            j = i + 1;
                            k++;
                        }
                    }
                    px[0] = Double.parseDouble(armPosLeftArray[0]);
                    py[0] = Double.parseDouble(armPosLeftArray[1]);
                    pz[0] = Double.parseDouble(armPosLeftArray[2]);
                    pr[0] = Double.parseDouble(armPosLeftArray[3]);
                    pp[0] = Double.parseDouble(armPosLeftArray[4]);
                    pya[0] = Double.parseDouble(armPosLeftArray[5]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Socket client3 = new Socket(host, port);
                    BufferedWriter out5 = new BufferedWriter(new OutputStreamWriter(client3.getOutputStream()));
                    out5.write("limb_getpose right_limb\n");
                    out5.flush();
                    BufferedReader in5 = new BufferedReader(new InputStreamReader(client3.getInputStream()));
                    armPosRight = in5.readLine();
                    in5.close();
                    client3.close();

                    int j = 1;
                    int k = 0;
                    for (int i = 1; i < armPosRight.length(); i++) {
                        if ((armPosRight.charAt(i) != 45) && (armPosRight.charAt(i) != 46) && !((armPosRight.charAt(i) >= 48) && (armPosRight.charAt(i) <= 57))) {
                            armPosRightArray[k] = armPosRight.substring(j, i) + "\n";
                            i++;
                            j = i + 1;
                            k++;
                        }
                    }
                    px[1] = Double.parseDouble(armPosRightArray[0]);
                    py[1] = Double.parseDouble(armPosRightArray[1]);
                    pz[1] = Double.parseDouble(armPosRightArray[2]);
                    pr[1] = Double.parseDouble(armPosRightArray[3]);
                    pp[1] = Double.parseDouble(armPosRightArray[4]);
                    pya[1] = Double.parseDouble(armPosRightArray[5]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
