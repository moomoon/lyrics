package com.dxm.lrc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ants on 20/12/2016.
 */

public class FileLineReader {
    private final BufferedReader reader;

    private FileLineReader(BufferedReader reader) {
        this.reader = reader;
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static FileLineReader fromStream(InputStream stream) {
        try {
            return new FileLineReader(new BufferedReader(new InputStreamReader(stream, "utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> List<T> parse(Parser<T> output) {
        List<T> l = new LinkedList<>();
        String s;
        while (!output.stop() && null != (s = readLine())) {
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

    interface Parser<T> {
        T parse(String input);

        boolean stop();
    }
}
