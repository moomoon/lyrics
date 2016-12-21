package com.dxm.sample;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.dxm.lrc.FileLineReader;
import com.dxm.lrc.LyricGroup;
import com.dxm.lrc.LyricItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            FileLineReader reader = FileLineReader.fromStream(getAssets().open("lyric.lrc"));
            if (null != reader) {
                LyricGroup group = LyricGroup.builder().addItems(reader.parse(LyricItem.LineParser.Instance), LyricGroup.Policy.PickFirst).build();
                ((TextView) findViewById(R.id.tv)).setText(group.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
