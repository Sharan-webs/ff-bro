package com.ff.aimbot.ui;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingButtonService extends Service {
    private WindowManager windowManager;
    private View floatingButton;
    private View expandedMenu;
    private Button headshotToggle;
    private ImageButton closeButton;
    private boolean isMenuExpanded = false;
    private boolean isHeadshotEnabled = true;
    private long closeButtonPressTime = 0;
    private static final long CLOSE_PRESS_DURATION = 6000; // 6 seconds

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            createFloatingButton();
            createExpandedMenu();
        }
        return START_STICKY;
    }

    private void createFloatingButton() {
        // Red circle floating button
        floatingButton = new View(this) {
            @Override
            protected void onDraw(android.graphics.Canvas canvas) {
                super.onDraw(canvas);
                android.graphics.Paint paint = new android.graphics.Paint();
                paint.setColor(Color.RED);
                paint.setStyle(android.graphics.Paint.Style.FILL);
                canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, paint);
            }
        };

        floatingButton.setBackgroundColor(Color.TRANSPARENT);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                120, 120,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        params.x = 20;
        params.y = 0;

        floatingButton.setOnTouchListener(new View.OnTouchListener() {
            private float lastX;
            private float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float deltaX = Math.abs(event.getRawX() - lastX);
                        float deltaY = Math.abs(event.getRawY() - lastY);
                        if (deltaX < 10 && deltaY < 10) {
                            toggleMenu();
                        }
                        break;
                }
                return true;
            }
        });

        windowManager.addView(floatingButton, params);
    }

    private void createExpandedMenu() {
        LinearLayout menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setBackgroundColor(Color.argb(240, 30, 30, 30));
        menuLayout.setPadding(20, 20, 20, 20);

        // Headshot toggle button
        headshotToggle = new Button(this);
        headshotToggle.setText("HEADSHOT: ON");
        headshotToggle.setTextColor(Color.BLACK);
        headshotToggle.setBackgroundColor(Color.GREEN);
        headshotToggle.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        headshotToggle.setPadding(20, 20, 20, 20);
        headshotToggle.setOnClickListener(v -> {
            isHeadshotEnabled = !isHeadshotEnabled;
            headshotToggle.setText(isHeadshotEnabled ? "HEADSHOT: ON" : "HEADSHOT: OFF");
            headshotToggle.setBackgroundColor(isHeadshotEnabled ? Color.GREEN : Color.RED);

            // Send command to accessibility service
            Intent cmd = new Intent("com.ff.aimbot.CONTROL");
            cmd.putExtra("action", "toggle_headshot");
            cmd.putExtra("headshot", isHeadshotEnabled);
            sendBroadcast(cmd);
        });

        menuLayout.addView(headshotToggle);

        // Close button (X) with long press
        closeButton = new ImageButton(this);
        closeButton.setText("X");
        closeButton.setTextColor(Color.WHITE);
        closeButton.setBackgroundColor(Color.RED);
        closeButton.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        closeButton.setOnLongClickListener(v -> {
            closeButtonPressTime = System.currentTimeMillis();
            Toast.makeText(FloatingButtonService.this, "Closing in 6 seconds...", Toast.LENGTH_SHORT).show();

            new Thread(() -> {
                try {
                    long pressed = System.currentTimeMillis() - closeButtonPressTime;
                    while (pressed < CLOSE_PRESS_DURATION) {
                        Thread.sleep(100);
                        pressed = System.currentTimeMillis() - closeButtonPressTime;
                    }
                    // Stop service after 6 seconds
                    stopSelf();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            return true;
        });

        closeButton.setOnClickListener(v -> {
            // Quick click just collapses menu
            toggleMenu();
        });

        menuLayout.addView(closeButton);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                350, 250,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        params.x = 20;
        params.y = -200;

        expandedMenu = menuLayout;
    }

    private void toggleMenu() {
        if (!isMenuExpanded) {
            windowManager.addView(expandedMenu, new WindowManager.LayoutParams(
                    350, 250,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                            WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            ));
            isMenuExpanded = true;
        } else {
            windowManager.removeView(expandedMenu);
            isMenuExpanded = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingButton != null) {
            windowManager.removeView(floatingButton);
        }
        if (expandedMenu != null && isMenuExpanded) {
            windowManager.removeView(expandedMenu);
        }
    }
}
