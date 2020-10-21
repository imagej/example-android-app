package net.imagej.android.example;

import android.content.Intent;
import android.content.res.Configuration;
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

import org.scijava.android.AndroidSciJavaGateway;
import org.scijava.command.CommandService;

public class MainActivity extends AppCompatActivity {

    private CommandService commandService;
    private RandomAccessibleInterval<ARGBType> img;
    private LinearLayout mainView;
    private ASCIICommand asciiCommand;
    private CameraHandler cameraHandler;
    private ViewGroup scijavaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBaseLayout();
        setupCamera();
        initGatewayAndServices();
        initResultImage();
        launchAsciiCommand();
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
        scijavaView = findViewById(R.id.scijava_group);
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

    private void initGatewayAndServices() {
        AndroidSciJavaGateway gateway = new AndroidSciJavaGateway(new org.scijava.Context(), this);
        gateway.launch();
        commandService = gateway.get(CommandService.class);
    }

    private void initResultImage() {
        img = new ArrayImgFactory<>(new ARGBType()).create(CameraPreview.preferredWidth, CameraPreview.preferredHeight);
    }

    private void launchAsciiCommand() {
        asciiCommand = new ASCIICommand();
        commandService.context().inject(asciiCommand);
        commandService.moduleService().run(asciiCommand, true, "input", img);
    }

    private void setContentViewLayout() {
        int align = LinearLayout.VERTICAL;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            align = LinearLayout.HORIZONTAL;
        }
        setAlignView(mainView, align);
        setLayoutParams(cameraHandler.getPreviewView(), align);
        setLayoutParams(scijavaView, align);
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
                if(bytes == null) {
                    camera.startPreview();
                    return;
                }
                img = new ArrayImgFactory<>(new ARGBType()).create(camera.getParameters().getPictureSize().width, camera.getParameters().getPictureSize().height);
                ImgLibUtils.cameraBytesToImage(bytes, img);
                img = Views.rotate(img, 0, 1);
                asciiCommand.setInput("input", img);
                camera.startPreview();
            }
        });
    }

    private void setupShareButton() {
        View button = findViewById(R.id.share_button);
        button.setOnClickListener(view -> {
            String text = (String) asciiCommand.getOutput("output");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
    }
}