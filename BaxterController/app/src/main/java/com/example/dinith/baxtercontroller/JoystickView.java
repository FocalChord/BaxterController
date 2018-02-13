package com.example.dinith.baxtercontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import static java.lang.Math.*;

/**
 * Created by Dinith on 17/12/2017.
 * Reference: http://www.instructables.com/id/A-Simple-Android-UI-Joystick/
 */

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private float centreX;
    private float centreY;
    private float baseRadius;
    private float hatRadius;
    private JoystickListener joystickCallback;


    public JoystickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);       // Causes the SurfaceView to use the onTouch method from this class to handle user screen touches from now on
        if (context instanceof JoystickListener)
            joystickCallback = (JoystickListener)context;
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener)            // Allows you to call the onJoystickMoved method in the class representing the activity that contains the joystick, provided it has implemented the JoystickListener and has the appropriate onJoystickMoved method
            joystickCallback = (JoystickListener)context;   //
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener)
            joystickCallback = (JoystickListener)context;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setupDimensions();
        drawJoystick(centreX, centreY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private void setupDimensions() {
        centreX = getWidth() / 2;
        centreY = getHeight() / 2;
        baseRadius = min(getWidth(), getHeight()) / 3;
        hatRadius = min(getWidth(), getHeight()) / 6;
    }

    private void drawJoystick(float newX, float newY) {
        if (getHolder().getSurface().isValid()) {       // if statement prevents the drawing method from executing when the SurfaceView has not been created on-screen, preventing exceptions at runtime
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();

            myCanvas.drawARGB(255,48,48,48);
            colors.setARGB(255, 80, 50, 50);                       // A = transparency (0 = invisible, 255 = solid), RGB = red, green, blue.
            myCanvas.drawCircle(centreX, centreY, baseRadius, colors);          // Draw a circle for the base
            colors.setARGB(255, 200, 50, 50);
            myCanvas.drawCircle(newX, newY, hatRadius, colors);                 // Draw a circle for the hat (top)

            // Canvas has been drawn but has not been printed to the SurfaceView, so is not visible to user...
            getHolder().unlockCanvasAndPost(myCanvas);  // Write the new drawing to the SurfaceView. Now it is visible to user
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(this)) {       // Makes sure that the touch listener only accepts touches coming from this SurfaceView
            float displacement = (float)Math.sqrt(Math.pow(event.getX() - centreX, 2) + Math.pow(event.getY() - centreY, 2));       // Find the displacement of the touch point from the centre

            if (event.getAction() != event.ACTION_UP) {       // Checks that the touch event is NOT the user lifting their finger off the touch screen (to make sure joystick only moves while user is touching the screen, and resets to original position when the user lets go
                if (displacement < baseRadius) {              // Checks that touch point is inside base
                    drawJoystick(event.getX(), event.getY());     // getX() & getY() methods give X and Y coordinates where user touches screen. The joystick will therefore be drawn where the user touches the screen
                    joystickCallback.onJoystickMoved((event.getX() - centreX) / baseRadius, (event.getY() - centreY) / baseRadius, getId());        // Way of getting back information from moving the joystick
                }
                else {          // When touch point is outside base
                    float ratio = baseRadius / displacement;
                    float constrainedX = centreX + (event.getX() - centreX) * ratio; // X & Y drawing coords will be drawn on edge of base, in line with touch point and centre
                    float constrainedY = centreY + (event.getY() - centreY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    joystickCallback.onJoystickMoved((constrainedX - centreX) / baseRadius, (constrainedY - centreY) / baseRadius, getId());
                }
            }
            else {      // When the joystick is released
                drawJoystick(centreX, centreY);
                joystickCallback.onJoystickMoved(0, 0, getId());
            }
        }
        return true;    // Returning false would prevent the onTouch method from receiving future touches
    }

    public interface JoystickListener {     // An interface for the callback method below --> way of interacting with joystick from another activity, way of reporting the joystick being touched, how it's being touched (etc)
        void onJoystickMoved(float xPercent, float yPercent, int source);       // Percent is % moved relative to maximum (base radius) --> can put anything for inputs... % used to account for motor later on... (see tutorial), source is to differentiate between different joysticks
    }
}
