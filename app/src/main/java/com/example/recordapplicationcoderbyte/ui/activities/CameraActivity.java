package com.example.recordapplicationcoderbyte.ui.activities;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordapplicationcoderbyte.CameraPreview;
import com.example.recordapplicationcoderbyte.R;
import com.example.recordapplicationcoderbyte.room.AppDatabase;
import com.example.recordapplicationcoderbyte.room.RecordDao;
import com.example.recordapplicationcoderbyte.room.RecordEntity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

public class CameraActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static String title ="";
    public static CountDownTimer timer;
    public static long seconds;
    public static String path;
    public static long perValue;

    public static AppDatabase db ;
    public static RecordDao recordDao ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle extras = getIntent().getExtras();
        title = extras.getString("title");
        seconds = extras.getLong("seconds")*1000;
        db = AppDatabase.getInstance(this);
        recordDao = db.recordDao();
        final TextView textView = findViewById(R.id.counterTxt);
        final TextView sec = findViewById(R.id.sec);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        //set TextView
        textView.setText("The Video Will Start In ");

        /** CountDownTimer for video duration */
        timer =  new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                sec.setText("" + millisUntilFinished / 1000 + " sec");
            }

            public void onFinish() {
                textView.setText("");
                Toast.makeText(getApplicationContext(),"Video Started Recording",Toast.LENGTH_SHORT).show();
                new CountDownTimer(seconds, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long perValue = millisUntilFinished/1000;
                        if(perValue>60){
                            if (perValue%60 == 0 ) {
                                sec.setText(perValue / 60 +" min ");
                            }
                            else sec.setText(perValue / 60 +" min " + perValue%60+ " sec");
                        }
                        else sec.setText(perValue + " sec");

                    }

                    public void onFinish() {
                        // stop recording and release camera
                        mediaRecorder.stop();  // stop the recording
                        releaseMediaRecorder(); // release the MediaRecorder object
                        mCamera.lock();         // take camera access back from MediaRecorder

                        // inform the user that recording has stopped
                        Toast.makeText(getApplicationContext(),"Video Saved",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }.start();
            }
        }.start();

        /** Waiting 3 seconds before start Recording */
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    mediaRecorder.start();

                    // inform the user that recording has started
                } else {
                    // prepare didn't work, release the camera
                    releaseMediaRecorder();
                    // inform user
                }

                Log.d("Handler", "Running Handler");
            }
        }, 3000);
    }
    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            setCameraDisplayOrientation(this,0,c);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /** detect camera orientation */
    public static void setCameraDisplayOrientation(Activity activity,int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /**  Prepare MediaRecorder */
    private boolean prepareVideoRecorder(){

        mCamera = getCameraInstance();
        mediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /** release MediaRecorder */
    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    // release the camera for other applications
    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File mediaStorageDir = new File(pictureFolder, "MyRecordApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyRecordApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + title + ".mp4");
            path = mediaStorageDir.getPath() + File.separator + title + ".mp4";
            perValue = (seconds/1000)-1;
            if(perValue>60){
                if (perValue%60 == 0 ) {
                    recordDao.insert(new RecordEntity(title,""+perValue / 60 +" min ",path)).subscribeOn(Schedulers.computation())
                            .subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onComplete() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                }
                else recordDao.insert(new RecordEntity(title,""+perValue / 60 +" min " + perValue%60+ " sec",path)).subscribeOn(Schedulers.computation())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
            }
            else recordDao.insert(new RecordEntity(title,""+perValue + " sec",path)).subscribeOn(Schedulers.computation())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });

        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
        timer.cancel();
        timer = null;
    }
}