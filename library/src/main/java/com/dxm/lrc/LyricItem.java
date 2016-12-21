package com.dxm.lrc;

import android.text.TextUtils;

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
    interface Tag {
        String title = "ti";
        String artist = "ar";
        String author = "au";
        String album = "al";
        String offset = "offset";
        String t_time = "t_time";
        String length = "length";
    }
    private LyricItem() {
    }

    public interface Visitor1<P, R> {
        R visitTitle(Title title, P p);

        R visitArtist(Artist artist, P p);

        R visitAlbum(Album album, P p);

        R visitDuration(Duration duration, P p);

        R visitAuthor(Author author, P p);

        R visitLine(Line line, P p);

        R visitOffset(Offset offset, P p);
    }

    public abstract <P, R> R accept(P p, Visitor1<P, R> visitor);

    public static LyricItem parse(String input) {
        Pattern mainPattern = Pattern.compile(".*\\[((.*?):\\(?(.*?)\\)?)?\\](.*)");
        Matcher matcher = mainPattern.matcher(input);
        if (!matcher.find()) return null;
        final int groupCount = matcher.groupCount();
        if (groupCount <= 3) return null;
        String bracket = matcher.group(1);
        long time = parseTime(bracket);
        if (groupCount > 3 && time > 0L) return new Line(time, matcher.group(4));
        switch (matcher.group(2).toLowerCase()) {
            case Tag.title:
                return new Title(matcher.group(3));
            case Tag.artist:
                return new Artist(matcher.group(3));
            case Tag.author:
                return new Author(matcher.group(3));
            case Tag.album:
                return new Album(matcher.group(3));
            case Tag.offset:
                return new Offset(Long.parseLong(matcher.group(3).replaceAll("\\s", "")));
            case Tag.t_time:
            case Tag.length:
                return new Duration(parseTime(matcher.group(3).trim()));
        }
        return null;
    }

    private static long parseTime(String input) {
        Pattern timePattern = Pattern.compile("((\\d+:)*\\d+)(\\.(\\d+))?");
        Matcher time = timePattern.matcher(input);
        if (!time.matches()) return -1;
        String floatPartStr = time.group(4);
        final float floatPart = TextUtils.isEmpty(floatPartStr) ? 0F : Float.parseFloat("0." + time.group(4));
        long intPart = parseTimeIntParts(time.group(1));
        return intPart + (long) (floatPart * 1000);
    }

    private static long parseTimeIntParts(String input) {
        long timeMS = 0;
        Pattern intPattern = Pattern.compile("(\\d+):?");
        Matcher ints = intPattern.matcher(input);
        List<Integer> intParts = new LinkedList<>();
        while (ints.find()) intParts.add(Integer.parseInt(ints.group(1)));
        Collections.reverse(intParts);
        TimeUnit[] availableUnits = {TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS};
        for (int i = 0; i < Math.min(availableUnits.length, intParts.size()); i++)
            timeMS += TimeUnit.MILLISECONDS.convert(intParts.get(i), availableUnits[i]);
        return timeMS;
    }

    public enum LineParser implements FileLineReader.Parser<LyricItem> {
        Instance;

        @Override public LyricItem parse(String input) {
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
        private final String title;

        public Title(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override public <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitTitle(this, p);
        }

        @Override public String toString() {
            return "title = " + title;
        }
    }

    public static final class Artist extends LyricItem {
        private final String artist;

        public Artist(String artist) {
            this.artist = artist;
        }

        public String getArtist() {
            return artist;
        }

        @Override public <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitArtist(this, p);
        }

        @Override public String toString() {
            return "artist = " + artist;
        }
    }

    public static final class Author extends LyricItem {
        private final String author;

        public Author(String author) {
            this.author = author;
        }

        public String getAuthor() {
            return author;
        }

        @Override public <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitAuthor(this, p);
        }

        @Override public String toString() {
            return "author = " + author;
        }
    }


    public static final class Album extends LyricItem {
        private final String album;

        public Album(String album) {
            this.album = album;
        }

        public String getAlbum() {
            return album;
        }

        @Override public <P, R> R accept(P p, Visitor1<P, R> visitor) {
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

        @Override public <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitDuration(this, p);
        }

        @Override public String toString() {
            return "duration = " + duration;
        }
    }

    public static final class Offset extends LyricItem {
        private final long offsetMS;

        public Offset(long offsetMS) {
            this.offsetMS = offsetMS;
        }

        public long getOffsetMS() {
            return offsetMS;
        }

        @Override public <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitOffset(this, p);
        }

        @Override public String toString() {
            return "offset = " + offsetMS;
        }
    }

    public static final class Line extends LyricItem {
        private final long timeMS;
        private final String text;

        public Line(long timeMS, String text) {
            this.timeMS = timeMS;
            this.text = text;
        }

        public long getTimeMS() {
            return timeMS;
        }

        public String getText() {
            return text;
        }

        @Override public <P, R> R accept(P p, Visitor1<P, R> visitor) {
            return visitor.visitLine(this, p);
        }

        @Override public String toString() {
            return "" + timeMS + " : " + text;
        }
    }

}
