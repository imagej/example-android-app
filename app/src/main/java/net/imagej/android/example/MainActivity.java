package net.imagej.android.example;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.imagej.android.ImgLibUtils;
import net.imagej.example.R;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.view.Views;

import org.scijava.android.AndroidGateway;

public class MainActivity extends AppCompatActivity {

    private ViewGroup sciJavaView;
    private LinearLayout mainView;

    private AndroidGateway gateway;

    private CameraHandler cameraHandler;
    private RandomAccessibleInterval<ARGBType> img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBaseLayout();
        launchGateway();
        setupCamera();
        setContentViewLayout();
        setupTakePictureButton();
        setupShareButton();
    }

    private void setupCamera() {
        cameraHandler = new CameraHandler(this);
    }

    private void setupBaseLayout() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view);
        mainView = findViewById(R.id.main_content);
        sciJavaView = findViewById(R.id.scijava_group);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentViewLayout();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraHandler.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    private void launchGateway() {
        gateway = new AndroidGateway(new org.scijava.Context(), this);
        gateway.launch();
    }

    private ASCIICommand launchAsciiCommand() {
        ASCIICommand asciiCommand = new ASCIICommand();
        gateway.context().inject(asciiCommand);
        gateway.module().run(asciiCommand, true, "input", img);
        return asciiCommand;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gateway.dispose();
    }

    private void setContentViewLayout() {
        cameraHandler.setOrientation(getResources().getConfiguration().orientation);
        int align = LinearLayout.VERTICAL;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            align = LinearLayout.HORIZONTAL;
        }
        setAlignView(mainView, align);
        setLayoutParams(cameraHandler.getPreviewView(), align);
        setLayoutParams(sciJavaView, align);
    }

    private void setAlignView(LinearLayout view, int alignment) {
        view.setOrientation(alignment);
    }

    private void setLayoutParams(View view, int alignment) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(alignment == LinearLayout.HORIZONTAL) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = 0;
        } else {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = 0;
        }
        view.setLayoutParams(params);
    }

    private void setupTakePictureButton() {
        View button = findViewById(R.id.make_picture_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cameraHandler.takePicture(this::pictureTaken);
            }
            private void pictureTaken(byte[] bytes, Camera camera) {
                updateCameraImage(bytes, camera);
            }
        });
    }

    private void updateCameraImage(byte[] bytes, Camera camera) {
        camera.startPreview();
        if(bytes != null) {
            gateway.thread().run(() -> {
                makeImage(bytes, camera.getParameters().getPictureSize());
                ASCIICommand command = launchAsciiCommand();
                command.setInput(img);
            });
        }
    }

    private void makeImage(byte[] bytes, Camera.Size size) {
        img = new ArrayImgFactory<>(new ARGBType()).create(size.width, size.height);
        ImgLibUtils.cameraBytesToImage(bytes, img);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            img = Views.zeroMin(Views.rotate(img, 0, 1));
        }
    }

    private void setupShareButton() {
//        View button = findViewById(R.id.share_button);
//        button.setOnClickListener(view -> {
//            String text = (String) asciiCommand.getOutput("output");
//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
//            sendIntent.setType("text/plain");
//            Intent shareIntent = Intent.createChooser(sendIntent, null);
//            startActivity(shareIntent);
//        });
    }
}