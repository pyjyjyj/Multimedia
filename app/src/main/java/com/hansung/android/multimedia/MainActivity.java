package com.hansung.android.multimedia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static String TAG="MultimediaTest";
    private ListView mListView;
    private int mSelectePoistion;
    private MediaItemAdapter mAdapter;
    private MediaPlayer mMediaPlayer;
    private MediaRecorder mMediaRecorder;
    private String mVideoFileName = null;
    private File mPhotoFile =null;
    private String mPhotoFileName = null;

    private int mPlaybackPosition = 0;   // media play 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) // 액티비티가 재시작되는 경우, 기존에 저장한 상태 복구
            mPhotoFileName = savedInstanceState.getString("mPhotoFileName");

        checkDangerousPermissions();

        final Button musicPlayBtn = (Button) findViewById(R.id.musicPlayBtn);
        Button videoPlayBtn = (Button) findViewById(R.id.videoPlayBtn);
        Button imageCaptureBtn = (Button) findViewById(R.id.imageCaptureBtn);
        Button videoRecBtn = (Button) findViewById(R.id.videoRecBtn);

        musicPlayBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                playAudioFromExternalStorage();
            }
        });

        videoPlayBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                playVideo();
            }
        });
    }

    private void playAudioFromExternalStorage(){
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gitan);
        mediaPlayer.start();
    }

    private void playVideo(){
        final VideoView videoView = (VideoView) findViewById(R.id.videoView);

        String VIDEO_URL = "file://" +Environment.getExternalStorageDirectory().getPath()+"/Movies/"+"twice.mp4";

        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        videoView.setVideoURI(Uri.parse(VIDEO_URL));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer player) {
                videoView.seekTo(0);
                videoView.start();
            }
        });
    }


    final int  REQUEST_EXTERNAL_STORAGE_FOR_MULTIMEDIA=1;

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_EXTERNAL_STORAGE_FOR_MULTIMEDIA);

        }

    }


    private void playAudio(Uri uri) throws Exception {
        killMediaPlayer();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(getApplicationContext(), uri);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

    private String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void killMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static final int REQUEST_IMAGE_PICK = 0;

    private void dispatchPickPictureIntent() {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK);
        pickPictureIntent.setType("image/*");

        if (pickPictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPictureIntent,REQUEST_IMAGE_PICK);
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //1. 카메라 앱으로 찍은 이미지를 저장할 파일 객체 생성
            mPhotoFileName = "IMG"+currentDateFormat()+".jpg";
            mPhotoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), mPhotoFileName);

            if (mPhotoFile !=null) {
                //2. 생성된 파일 객체에 대한 Uri 객체를 얻기
                Uri imageUri = FileProvider.getUriForFile(this, "com.hansung.android.multimedia", mPhotoFile);

                //3. Uri 객체를 Extras를 통해 카메라 앱으로 전달
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else
                Toast.makeText(getApplicationContext(), "file null", Toast.LENGTH_SHORT).show();
        }
    }

    static final int REQUEST_VIDEO_CAPTURE = 2;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            //1. 카메라 앱으로 찍은 동영상을 저장할 파일 객체 생성
            mVideoFileName = "VIDEO"+currentDateFormat()+".mp4";
            File destination = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), mVideoFileName);

            if (destination != null) {
                //2. 생성된 파일 객체에 대한 Uri 객체를 얻기
                Uri videoUri = FileProvider.getUriForFile(this, "com.example.kwanwoo.multimediatest", destination);

                //3. Uri 객체를 Extras를 통해 카메라 앱으로 전달
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            try {
                Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);

                mPhotoFileName = "IMG"+currentDateFormat()+".jpg";
                mPhotoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), mPhotoFileName);

                imgBitmap.compress(Bitmap.CompressFormat.JPEG,100,
                        new FileOutputStream(mPhotoFile));
                mAdapter.addItem(new MediaItem(MediaItem.SDCARD, mPhotoFileName, Uri.fromFile(mPhotoFile), MediaItem.IMAGE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mPhotoFileName != null) {
                mPhotoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), mPhotoFileName);
                mAdapter.addItem(new MediaItem(MediaItem.SDCARD, mPhotoFileName, Uri.fromFile(mPhotoFile), MediaItem.IMAGE));
            } else
                Toast.makeText(getApplicationContext(), "mPhotoFile is null", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            if (mVideoFileName != null) {
                File destination = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), mVideoFileName);
                mAdapter.addItem(new MediaItem(MediaItem.SDCARD, mVideoFileName, Uri.fromFile(destination) ,MediaItem.VIDEO));
            } else
                Toast.makeText(getApplicationContext(), "!!! null video.", Toast.LENGTH_LONG).show();
        }
    }

    /*
        카메라 앱을 통해 이미지를 저장하고 다시 현재 앱으로 돌아오는 경우, 예기치 않게 액티비티가 재시작되는 경우
        기존 상태 (mPhotoFileName)을 저장하는 메소드. 안드로이드 프레임워크에 의해서 자동으로 호출
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mPhotoFileName",mPhotoFileName);
        super.onSaveInstanceState(outState);
    }

    protected void onStop() {
        super.onStop();
        killMediaPlayer();
    }

    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }
}
