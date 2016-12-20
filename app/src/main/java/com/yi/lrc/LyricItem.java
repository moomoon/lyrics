package com.yi.lrc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ants on 20/12/2016.
 */

abstract public class LyricItem {
    private LyricItem() {
    }

    interface Visitor1<P, R> {
        R visitTitle(@NonNull Title title, P p);

        R visitAuthor(@NonNull Author author, P p);

        R visitAlbum(@NonNull Album album, P p);

        R visitDuration(@NonNull Duration duration, P p);

        R visitLine(@NonNull Line line, P p);
    }

    abstract <P, R> R accept(P p, Visitor1<P, R> visitor);

    @Nullable public static LyricItem parse(@NonNull String input) {
        Pattern mainPattern = Pattern.compile(".*\\[((.*?):\\(?(.*?)\\)?)?\\](.*)");
        Matcher matcher = mainPattern.matcher(input);
        if (!matcher.find()) return null;
        final int groupCount = matcher.groupCount();
        if (groupCount <= 3) return null;
        String bracket = matcher.group(1);
        long time = parseTime(bracket);
        if (groupCount > 3 && time > 0L) return new Line(time, matcher.group(4));
        switch (matcher.group(2)) {
            case "ti":
                return new Title(matcher.group(3));
            case "ar":
                return new Author(matcher.group(3));
            case "al":
                return new Album(matcher.group(3));
            case "t_time":
                return new Duration(parseTime(matcher.group(3)));
        }
        return null;
    }

    private static long parseTime(@NonNull String input) {
        Pattern timePattern = Pattern.compile("(\\d+:)*(\\d+(\\.(\\d+))?)");
        Matcher time = timePattern.matcher(input);
        if (!time.matches()) return -1;
        String floatPartStr = time.group(4);
        final float floatPart = TextUtils.isEmpty(floatPartStr) ? 0F : Float.parseFloat("0." + time.group(4));
        long intPart = parseTimeIntParts(input);
        return intPart + (long) (floatPart * 1000);
    }

    private static long parseTimeIntParts(@NonNull String input) {
        long timeMS = 0;
        Pattern intPattern = Pattern.compile("(\\d{2})[:.]");
        Matcher ints = intPattern.matcher(input);
        List<Integer> intParts = new LinkedList<>();
        while (ints.find()) intParts.add(Integer.parseInt(ints.group(1)));
        Collections.reverse(intParts);
        TimeUnit[] availableUnits = {TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS};
        for (int i = 0; i < Math.min(availableUnits.length, intParts.size()); i++)
            timeMS += TimeUnit.MILLISECONDS.convert(intParts.get(i), availableUnits[i]);
        return timeMS;
    }

    enum LineParser implements FileLineReader.Output<LyricItem> {
        Instance;


        @Nullable @Override public LyricItem parse(@NonNull String input) {
            try {
                return LyricItem.parse(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override public boolean stop() {
            return false;
        }
    }


    public static final class Title extends LyricItem {
        @NonNull private final String title;

        public Title(@NonNull String title) {
            this.title = title;
        }

        @NonNull public String getTitle() {
            return title;
        }

        @Override <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitTitle(this, p);
        }

        @Override public String toString() {
            return "title = " + title;
        }
    }

    public static final class Author extends LyricItem {
        @NonNull private final String author;

        public Author(@NonNull String author) {
            this.author = author;
        }

        @NonNull public String getAuthor() {
            return author;
        }

        @Override <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitAuthor(this, p);
        }

        @Override public String toString() {
            return "author = " + author;
        }
    }

    public static final class Album extends LyricItem {
        @NonNull final String album;

        public Album(@NonNull String album) {
            this.album = album;
        }

        @NonNull public String getAlbum() {
            return album;
        }

        @Override <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitAlbum(this, p);
        }

        @Override public String toString() {
            return "album = " + album;
        }
    }

    public static final class Duration extends LyricItem {
        private final long duration;

        public Duration(long duration) {
            this.duration = duration;
        }

        public long getDuration() {
            return duration;
        }

        @Override <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitDuration(this, p);
        }

        @Override public String toString() {
            return "duration = " + duration;
        }
    }

    public static final class Line extends LyricItem {
        private final long timeMS;
        @NonNull private final String text;

        public Line(long timeMS, @NonNull String text) {
            this.timeMS = timeMS;
            this.text = text;
        }

        public long getTimeMS() {
            return timeMS;
        }

        @NonNull public String getText() {
            return text;
        }

        @NonNull private static Line parse(@NonNull List<Integer> intPart, int floatPart, @NonNull String stringPart) {
            List<Integer> ints = new ArrayList<>(intPart);
            Collections.reverse(ints);
            long time = 0;
            TimeUnit[] availableUnits = {TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS};
            for (int i = 0; i < Math.min(availableUnits.length, ints.size()); i++) {
                time += TimeUnit.MILLISECONDS.convert(ints.get(i), availableUnits[i]);
            }
            time += (long) (floatPart * 10);
            return new Line(time, stringPart);
        }


        @Override <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitLine(this, p);
        }

        @Override public String toString() {
            return "" + timeMS + " : " + text;
        }
    }

}
