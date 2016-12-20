package com.yi.lrc;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileLineReader reader = FileLineReader.fromPath(Environment.getExternalStorageDirectory() + "/lyric.lrc");
        for (LyricItem item : reader.read(LyricItem.LineParser.Instance)) {
            Log.e("item", "" + item);
        };
    }
}
