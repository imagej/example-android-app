package net.imagej.android.example;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import net.imagej.example.R;

class CameraHandler {


    private static final int MY_CAMERA_REQUEST_CODE = 123;
    private final ViewGroup previewView;
    private final Activity activity;
    private Camera camera;
    private CameraPreview preview;

    CameraHandler(Activity activity) {
        this.activity = activity;
        View mainLayout = activity.findViewById(R.id.myCoordinatorLayout);
        previewView = activity.findViewById(R.id.camera_preview);
        boolean allowed = checkPermission();
        if(!allowed) {
            Snackbar.make(mainLayout, R.string.no_permission,
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;

        }
        setupCameraAndPreview();
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        return false;
    }


    private void setupCameraAndPreview() {
        if(safeCameraOpen()) {
            preview = new CameraPreview(activity, camera);
            previewView.addView(preview);
        }
    }


    private boolean safeCameraOpen() {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            camera = Camera.open();
            qOpened = (camera != null);
        } catch (Exception e) {
            Log.e(activity.getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        if(preview != null) {
//            preview.removeCamera();
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    void takePicture(Camera.PictureCallback callback) {
        if(camera == null) return;
        camera.takePicture(null, null, callback);
    }

    void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "camera permission granted", Toast.LENGTH_LONG).show();
                setupCameraAndPreview();
            } else {
                Toast.makeText(activity, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public View getPreviewView() {
        return previewView;
    }

    public void setOrientation(int orientation) {
        preview.setOrientation(orientation);
    }
}
