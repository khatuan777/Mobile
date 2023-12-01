package com.example.lab05_music;

import static android.os.Environment.DIRECTORY_MUSIC;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Data> songList;
    static Intent intent;

    ImageButton btn_back;
    static String param = "";

    int requestCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareData(param);
        recyclerView = findViewById(R.id.recycle_view);
        MusicAdapter adapter = new MusicAdapter(songList, new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Data song = songList.get(position);
                param = song.getName();
                if (song.getFile() == "") {
                    intent = new Intent(MainActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, SongControllerActivity.class);
                    requestCode = 1;
                }
                intent.putExtra("name", song.getName());
                intent.putExtra("image", song.getImage());
                intent.putExtra("file", song.getFile());
                intent.putParcelableArrayListExtra("songList", songList);
                startActivityForResult(intent, requestCode);
            }
        }, MainActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));


//        Thêm sự kiện quay lại
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            finish();
            System.exit(0);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Kiểm tra mã yêu cầu
            if (resultCode == Activity.RESULT_OK) { // Kiểm tra kết quả có thành công không
                // Lấy dữ liệu kết quả từ Intent
                Data resultValue = data.getParcelableExtra("currentSong");
                Toast.makeText(this, resultValue.getName() + " đang được phát", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("ResourceType")
    private void prepareData(String param) {
        songList = new ArrayList<>();
        File musicDir = Environment.getExternalStoragePublicDirectory(param);
        // Lấy bài hát
        if (musicDir.exists() && musicDir.isDirectory()) {
            File[] musicReadFiles = musicDir.listFiles();
            if (musicReadFiles != null) {
                for (int i = 0; i < musicReadFiles.length; i++) {
                    // Lấy tên tệp âm nhạc và thêm vào danh sách
                    String musicFileName = musicReadFiles[i].getName();
                    String musicPath = musicReadFiles[i].getPath();
                    if (musicReadFiles[i].isFile()) {
                        songList.add(new Data(musicFileName.substring(0, musicFileName.lastIndexOf('.')), musicPath, getImage(musicPath)));
                    } else {
                        songList.add(new Data(musicFileName + "", "", R.drawable.ic_launcher_foreground));
                    }
                }
            }
        }
    }

    private int getImage(String filePath) {
        Resources resources = getResources();
        String resourceName = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.lastIndexOf('.'));
        resourceName = StringUtils.stripAccents(resourceName).replaceAll("\\s", "").toLowerCase();
        int resourceId = resources.getIdentifier(resourceName, "drawable", getPackageName());
        if (resourceId != 0) {
            // Tài nguyên đã được tìm thấy, và bạn có thể sử dụng resourceId để hiển thị hình ảnh, ví dụ:
            return resourceId;
        } else {
            // Tài nguyên không tồn tại
            return R.drawable.ic_launcher_foreground;
        }
    }
}