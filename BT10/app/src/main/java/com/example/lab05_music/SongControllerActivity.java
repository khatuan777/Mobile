package com.example.lab05_music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SongControllerActivity extends AppCompatActivity {
    TextView tv_title, tv_timeCurrent, tv_timeSong;
    SeekBar seekBar;
    ImageButton btn_previous, btn_stop, btn_next, btn_play, btn_back;
    static CardView disc;
    ImageView disc_cd;
    static List<Data> arrayData = new ArrayList<>();
    static List<Data> songList = new ArrayList<>();
    static MediaPlayer mediaPlayer;
    Animation animation;
    Intent intent;
    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_controller);
        initView();
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        disc.startAnimation(animation);
        if (mediaPlayer == null) {
            prepareData();
            initMedia();
        } else {
            intent = getIntent();
            Data temp = new Data(intent.getStringExtra("name"), intent.getStringExtra("file"), intent.getIntExtra("image", 0));
            if (!arrayData.get(0).getName().equalsIgnoreCase(temp.getName())) {
                arrayData.clear();
                arrayData.add(temp);
                mediaPlayer.stop();
                mediaPlayer.release();
                initMedia();
            } else {
                tv_title.setText(arrayData.get(0).getName());
                disc_cd.setBackground(getDrawable(arrayData.get(0).getImage()));
                if (mediaPlayer.isPlaying()) {
                    btn_play.setImageResource(R.drawable.pause);
                }
                setTimeSong();
                setTimeCurrent();
            }
        }


//        Dừng khi được chọn bài mới
        btn_back.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("currentSong", arrayData.get(0)); // Đặt dữ liệu kết quả (nếu cần)
            setResult(Activity.RESULT_OK, resultIntent); // Đặt mã kết quả và intent kết quả
            finish();
        });

        btn_next.setOnClickListener(v -> {
            getCurrentSong();
            position++;
            if (position > songList.size() - 1) {
                position = 0;
            }
            arrayData.clear();
            arrayData.add(songList.get(position));
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            initMedia();
            btn_play.setImageResource(R.drawable.pause);
        });

        btn_previous.setOnClickListener(v -> {
            getCurrentSong();
            position--;
            if (position < 0) {
                position = songList.size() - 1;
            }
            arrayData.clear();
            arrayData.add(songList.get(position));
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            initMedia();
            btn_play.setImageResource(R.drawable.pause);
        });

        btn_stop.setOnClickListener(v -> {
            btn_play.setImageResource(R.drawable.play_buttton);
            mediaPlayer.reset();
            initMedia();
        });

        btn_play.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                btn_play.setImageResource(R.drawable.play_buttton);
                mediaPlayer.pause();
            } else {
                btn_play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

    }

    private void prepareData() {
        intent = getIntent();
        Data temp = new Data(intent.getStringExtra("name"), intent.getStringExtra("file"), intent.getIntExtra("image", 0));
        arrayData.add(temp);
    }

    private void getCurrentSong() {
        for (int i = 0; i < songList.size(); i++) {
            if (arrayData.get(0).getName().equalsIgnoreCase(songList.get(i).getName())) {
                position = i;
            }
        }
    }

    private void initMedia() {
        playSong(arrayData.get(0).getFile());
        tv_title.setText(arrayData.get(0).getName());
        btn_play.setImageResource(R.drawable.pause);
        disc_cd.setBackground(getDrawable(arrayData.get(0).getImage()));
        setTimeSong();
        setTimeCurrent();
    }

    private void playSong(String filePath) {
        mediaPlayer = new MediaPlayer(this);
        try {
            mediaPlayer.setDataSource(filePath);
            // Chuẩn bị MediaPlayer
            mediaPlayer.prepare();
            // Phát tệp MP3
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTimeSong() {
        SimpleDateFormat n = new SimpleDateFormat("mm:ss");
        tv_timeSong.setText(n.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void setTimeCurrent() {
        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                tv_timeCurrent.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        getCurrentSong();
                        position++;
                        if (position > songList.size() - 1) {
                            position = 0;
                        }
                        arrayData.clear();
                        arrayData.add(songList.get(position));
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        initMedia();
                        btn_play.setImageResource(R.drawable.pause);
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }


    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_timeCurrent = findViewById(R.id.tv_timeCurrent);
        tv_timeSong = findViewById(R.id.tv_timeSong);
        seekBar = findViewById(R.id.seekbar);
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        btn_play = findViewById(R.id.btn_play);
        btn_stop = findViewById(R.id.btn_stop);
        disc = findViewById(R.id.disc_rotate);
        btn_back = findViewById(R.id.btn_back);
        disc_cd = findViewById(R.id.disc_cd);

        //Lấy danh sách bài hát
        songList = getIntent().getParcelableArrayListExtra("songList");

    }
}
