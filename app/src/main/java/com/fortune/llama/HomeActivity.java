package com.fortune.llama;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {

    Button logout, on, off;
    ImageButton mainButton, callButton;
    EditText phoneNum;
    private static final int REQUEST_CALL = 1;
    MediaPlayer player;
    private AudioManager audio;
    private SeekBar volBar, progressBar;
    private Runnable runnable;
    private Handler handler;
    TextView currentDate;
    private static final int SONG_NAME = R.raw.piano;
    private int currentVolume;

    // helper method to play the music
    private void playMusic() {
        progressBar.setMax(player.getDuration());
        playCycle();
        player.setLooping(true);
        player.start();
    }

    // music progress
    private void playCycle() {
        progressBar.setProgress(player.getCurrentPosition());
        runnable = new Runnable() {
            @Override
            public void run() {
                playCycle();
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    // phone call
    private void makePhoneCall() {
        String number = phoneNum.getText().toString();
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(dial));
                startActivity(callIntent);
            }
        } else {
            Toast.makeText(HomeActivity.this, "Enter Phone Number!", Toast.LENGTH_SHORT).show();
        }
    }

    // helper
    private void stopHelper() {
        if (player.isPlaying()) {
            handler.removeCallbacks(runnable);
            player.stop();
            Toast.makeText(HomeActivity.this, "Stopped Music!", Toast.LENGTH_SHORT).show();
        }
        player = MediaPlayer.create(getApplicationContext(), SONG_NAME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else  {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // restore value
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", player.getCurrentPosition());
        outState.putBoolean("isPlaying", player.isPlaying());
        outState.putInt("volume", currentVolume);
        if (player.isPlaying()) {
            player.pause();
        }
    }

    // progress bar needs to be fixed.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int position = savedInstanceState.getInt("position");
        player.seekTo(position);
        if (savedInstanceState.getBoolean("isPlaying")) {
            playMusic();
            currentVolume = savedInstanceState.getInt("volume");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // music progress part
        handler = new Handler();
        progressBar = (SeekBar) findViewById(R.id.progressBar);

        logout = findViewById(R.id.logout_button);
        mainButton = findViewById(R.id.mainIcon);
        callButton = findViewById(R.id.call_button);
        phoneNum = findViewById(R.id.enter_phoneNumber);
        on = findViewById(R.id.play);
        off = findViewById(R.id.stop);

        // media part
        final MediaPlayer llamaSound = MediaPlayer.create(this, R.raw.llama);
        player = MediaPlayer.create(this, SONG_NAME);

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // music volume part
        volBar = findViewById(R.id.volumeBar);
        audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        volBar.setMax(maxVolume);
        volBar.setProgress(currentVolume);

        // Date & Time
        currentDate = findViewById(R.id.current_date);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Calendar calendar = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                                        new SimpleDateFormat("EEEE\nyyyy-MM-dd\nHH:mm:ss a");
                                String date = simpleDateFormat.format(calendar.getTime()).toUpperCase();
                                currentDate.setText(date);
                            }
                        });
                    }
                } catch (Exception e) {
                    currentDate.setText(R.string.app_name);
                }
            }
        };
        thread.start();

        // progress bar control
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // volume bar control
        volBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    handler.removeCallbacks(runnable);
                    player.stop();
                    player = null;
                }
                if (llamaSound.isPlaying()) {
                    llamaSound.stop();
                }
                Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                finish();
                Toast.makeText(HomeActivity.this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // calling button
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopHelper();
                makePhoneCall();
            }
        });


        // play the music
        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!player.isPlaying()) {

                    // playing the music
                    playMusic();

                    audio.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                    Toast.makeText(HomeActivity.this, "Playing Music Now!", Toast.LENGTH_SHORT).show();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            player.reset();
                            player.release();
                        }
                    });
                }
            }
        });

        // stop the music
        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopHelper();
            }
        });

        // logo button
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamaSound.start();
            }
        });
    }
}