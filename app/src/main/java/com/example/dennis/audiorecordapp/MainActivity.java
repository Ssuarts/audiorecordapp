package com.example.dennis.audiorecordapp;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    Button btn_start;
    Button btn_stop;
    Button btn_playLast;
    Button btn_stopPlaying;

    EditText editText;

    String str_audioPath = null;

    public static final int RequestPermissionCode = 1;

    MediaPlayer mediaPlayer;

    MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Zuweisung der Buttons
        btn_start = (Button) findViewById(R.id.button);
        btn_stop = (Button) findViewById(R.id.button2);
        btn_playLast = (Button) findViewById(R.id.button3);
        btn_stopPlaying = (Button) findViewById(R.id.button4);

        editText = (EditText) findViewById(R.id.et1);

        //Bestimmte Buttons sollen erst zu einem späteren Zeitpunkt freigegeben werden
        btn_stop.setEnabled(false);
        btn_stopPlaying.setEnabled(false);
        btn_stopPlaying.setEnabled(false);

        //OnClick für Start-Button
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editText.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Bitte geben Sie einen Namen für die Audiodatei ein!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(checkPermission())
                    {
                        String audioName = editText.getText().toString();
                        str_audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                audioName + ".3gp";

                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        }
                        catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        //Buttons werden gesperrt bzw. freigegeben
                        btn_start.setEnabled(false);
                        btn_stop.setEnabled(true);

                        Toast.makeText(MainActivity.this, "Aufnahme wurde gestartet...", Toast.LENGTH_SHORT).show();


                    }
                    else
                    {
                        requestPermission();
                    }
                }

            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                btn_stop.setEnabled(false);
                btn_playLast.setEnabled(true);
                btn_start.setEnabled(true);
                btn_stopPlaying.setEnabled(false);

                Toast.makeText(MainActivity.this, "Aufnahme beendet!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_playLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_stop.setEnabled(false);
                btn_start.setEnabled(false);
                btn_stopPlaying.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try{
                    mediaPlayer.setDataSource(str_audioPath);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "Audio wird abgespielt!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_stopPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_stop.setEnabled(false);
                btn_start.setEnabled(true);
                btn_stopPlaying.setEnabled(false);
                btn_playLast.setEnabled(true);

                if(mediaPlayer != null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }

    public void MediaRecorderReady(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(str_audioPath);

    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode)
        {
            case RequestPermissionCode:
                if(grantResults.length>0)
                {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    
                    if(StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission()
    {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
}
