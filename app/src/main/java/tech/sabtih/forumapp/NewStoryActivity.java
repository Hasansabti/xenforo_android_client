package tech.sabtih.forumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;

import androidx.camera.core.VideoCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import android.net.Uri;
import java.util.concurrent.Executor;

public class NewStoryActivity extends AppCompatActivity implements  SurfaceHolder.Callback {

    private int REQUEST_CODE_PERMISSIONS = 10;
    private String[] REQUIRED_PERMISSIONS  = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String tag = NewStoryActivity.class.getSimpleName();


    private ImageButton btn;
    public static final String LOGTAG = "VIDEOCAPTURE";

    MediaRecorder recorder;
    MediaPlayer mp;

    SurfaceHolder holder;
    private CamcorderProfile camcorderProfile;
    private Camera camera;
    SurfaceView cameraView;

    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;
    boolean playing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);


        btn = findViewById(R.id.capture_button);


        cameraView = (SurfaceView) findViewById(R.id.view_finder);






        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);




// Request camera permissions
        if (allPermissionsGranted()) {
            recorder = new MediaRecorder();
            recorder.setAudioSamplingRate(16000);

            //usecamera = false;


            holder = cameraView.getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
           // play();


        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        //cameraView.setClickable(true);
       // cameraView.setOnClickListener(this);

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){


                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                   Log.d("Downtime", ""+motionEvent.getDownTime());

                       btn.setBackgroundColor(Color.RED);
                       if (usecamera && recording) {
                           try {
                               recorder.stop();
                               recording = false;

                               play();
                           }catch (Exception ex){
                               Log.e("Rec stop",ex.getMessage());
                               ex.printStackTrace();
                           }
                       }

                       // Let's initRecorder so we can record again
                    if(motionEvent.getDownTime() > 166844584) {
                       //prepareRecorder();

                   }else{
                       //take picture

                   }
                }

                return false;
            }
        });
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(usecamera) {
                    btn.setBackgroundColor(Color.GREEN);
                    recording = true;
                    recorder.start();
                }


                return false;
            }
        });


    }

    Boolean allPermissionsGranted(){
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



    private void prepareRecorder() {
        recorder = new MediaRecorder();
        recorder.setPreviewDisplay(holder.getSurface());
        recorder.setOrientationHint(90);


        if (usecamera) {
            camera.unlock();
            recorder.setCamera(camera);
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        recorder.setProfile(camcorderProfile);

        // This is all very sloppy
        if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
            try {

                File newFile = new File( getFilesDir(),"videocapture.3gp");
                if(!newFile.exists())
                    newFile.createNewFile();
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            try {
                File newFile = new File( getFilesDir(),"videocapture.mp4");
                if(!newFile.exists())
                    newFile.createNewFile();


                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else {
            try {
                File newFile = new File( getFilesDir(),"videocapture.mp4");
                if(!newFile.exists())
                    newFile.createNewFile();
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }

        }
        recorder.setMaxDuration(50000); // 50 seconds
        recorder.setMaxFileSize(5000000); // Approximately 5 megabytes

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {

                recorder = new MediaRecorder();

                SurfaceView cameraView = (SurfaceView) findViewById(R.id.view_finder);
                holder = cameraView.getHolder();
                holder.addCallback(this);
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.v(LOGTAG, "surfaceCreated");
        if (usecamera) {
            camera = Camera.open();

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
                camera.setDisplayOrientation(90);

            }
            catch (IOException e) {
                Log.e(LOGTAG,e.getMessage());
                e.printStackTrace();
            }
        }else{
            mp.setDisplay(holder);


        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.v(LOGTAG, "surfaceChanged");

        if (!recording && usecamera) {
            if (previewRunning){
                camera.stopPreview();
            }

            try {
                Camera.Parameters p = camera.getParameters();

                p.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
                p.setPreviewFrameRate(camcorderProfile.videoFrameRate);
                p.set("orientation", "portrait");
                p.set("rotation",90);



                camera.setParameters(p);

                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            }
            catch (IOException e) {
                Log.e(LOGTAG,e.getMessage());
                e.printStackTrace();
            }

            prepareRecorder();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v(LOGTAG, "surfaceDestroyed");
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        if (usecamera) {
            previewRunning = false;
            //camera.lock();
            camera.release();
        }
        if(playing){
            mp.stop();
            mp.release();

        }
        finish();
    }

    void play(){
        try {
            mp = new MediaPlayer();

           // holder.removeCallback(this);
            if (recording) {
                recorder.stop();
                recording = false;
            }
//            recorder.release();
            if (usecamera) {
                previewRunning = false;
                camera.lock();
              //  camera.release();
                camera.setPreviewDisplay(null);
                camera.stopPreview();
            }

            usecamera = false;
//            holder.addCallback(this);
            mp.setDisplay(holder);
           // mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource( getFilesDir()+"/videocapture.mp4");
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mp.setLooping(true);
                    mp.start();

                }
            });


           // mp = MediaPlayer.create(getApplicationContext(), Uri.parse("file://"+getFilesDir()+"/videocapture.mp4"),holder);
          //  mp.prepare();
            playing = true;



        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mp.start();
    }

    public void stop(){
        if(playing) {
            mp.stop();
            mp.setDisplay(null);
            mp.release();
            playing = false;
        }
            usecamera = true;

        try {


            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //camera.startPreview();
        previewRunning = true;
        prepareRecorder();




    }

    @Override
    public void onBackPressed() {
        if(playing){
            stop();
        }else {
            super.onBackPressed();
        }
    }
}
