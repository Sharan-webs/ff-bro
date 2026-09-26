package com.ff.aimbot.ui;

import android.accessibilityservice.AccessibilityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.ff.aimbot.core.AimbotAccessibilityService;
import com.ff.aimbot.R;

public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private static final int ACCESSIBILITY_PERMISSION_REQ_CODE = 5678;
    private LinearLayout permissionContainer;
    private LinearLayout controlContainer;
    private TextView statusText;
    private Button overlayPermButton;
    private Button accessibilityPermButton;
    private Button startOverlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionContainer = findViewById(R.id.permission_container);
        controlContainer = findViewById(R.id.control_container);
        statusText = findViewById(R.id.status_text);
        overlayPermButton = findViewById(R.id.overlay_perm_button);
        accessibilityPermButton = findViewById(R.id.accessibility_perm_button);
        startOverlayButton = findViewById(R.id.start_overlay_button);

        // Permission buttons
        overlayPermButton.setOnClickListener(v -> requestOverlayPermission());
        accessibilityPermButton.setOnClickListener(v -> requestAccessibilityPermission());
        startOverlayButton.setOnClickListener(v -> startOverlay());

        checkAllPermissions();
    }

    private void checkAllPermissions() {
        boolean overlayOk = canDrawOverlays();
        boolean accessibilityOk = isAccessibilityEnabled();

        overlayPermButton.setEnabled(!overlayOk);
        overlayPermButton.setText(overlayOk ? "✓ Overlay Permission" : "Overlay Permission");

        accessibilityPermButton.setEnabled(!accessibilityOk);
        accessibilityPermButton.setText(accessibilityOk ? "✓ Accessibility Service" : "Accessibility Service");

        startOverlayButton.setEnabled(overlayOk && accessibilityOk);

        if (overlayOk && accessibilityOk) {
            statusText.setText("All permissions granted ✓");
            permissionContainer.setVisibility(android.view.View.GONE);
        } else {
            statusText.setText("Grant all permissions to start");
        }
    }

    private boolean canDrawOverlays() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                Settings.canDrawOverlays(this);
    }

    private boolean isAccessibilityEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (am != null) {
            for (android.accessibilityservice.AccessibilityServiceInfo service : 
                    am.getEnabledAccessibilityServiceList(AccessibilityManager.FEEDBACK_GENERIC)) {
                if (service.getId().contains("com.ff.aimbot")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
    }

    private void requestAccessibilityPermission() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "Enable 'FF Aimbot' service in accessibility", Toast.LENGTH_LONG).show();
    }

    private void startOverlay() {
        if (!canDrawOverlays() || !isAccessibilityEnabled()) {
            Toast.makeText(this, "Missing permissions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Start floating button overlay service
        Intent serviceIntent = new Intent(this, FloatingButtonService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Close app
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAllPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            checkAllPermissions();
        }
    }
}
