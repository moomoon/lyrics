package com.yi.lrc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ants on 20/12/2016.
 */

public class FileLineReader {
    @NonNull private final BufferedReader reader;

    private FileLineReader(@NonNull BufferedReader reader) {
        this.reader = reader;
    }

    @Nullable private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable public static FileLineReader fromPath(@NonNull String path) {
        try {
            return new FileLineReader(new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "utf-8")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull public<T> List<T> read(Output<T> output) {
        List<T> l = new LinkedList<>();
        String s;
        while (!output.stop() && null != (s = readLine())) {
            Log.e("read", "s = " + s);
            T value = output.parse(s);
            if (null != value) l.add(value);
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableList(l);
    }

    interface Output<T> {
        @Nullable T parse(@NonNull String input);

        boolean stop();
    }
}
