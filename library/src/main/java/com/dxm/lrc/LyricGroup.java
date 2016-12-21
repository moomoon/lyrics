package com.dxm.lrc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ants on 21/12/2016.
 */

public class LyricGroup {
    public enum Policy {
        Strict {
            @Override <T> T pick(T current, T newItem) {
                if (null != current)
                    throw new IllegalArgumentException("Duplicate entries: " + current + " and " + newItem);
                return newItem;
            }
        },
        PickFirst {
            @Override <T> T pick(T current, T newItem) {
                return null == current ? newItem : current;
            }
        },

        PickLast {
            @Override <T> T pick(T current, T newItem) {
                return newItem;
            }
        };

        abstract <T> T pick(T current, T newItem);
    }

    private final LyricItem.Title title;
    private final LyricItem.Artist artist;
    private final LyricItem.Author author;
    private final LyricItem.Album album;
    private final LyricItem.Duration duration;
    private final List<LyricItem.Line> lines;

    private LyricGroup(Builder builder) {
        this.title = builder.title;
        this.artist = builder.artist;
        this.author = builder.author;
        this.album = builder.album;
        this.duration = builder.duration;
        LyricItem.Offset offset = builder.offset;
        this.lines = null == offset ? Collections.unmodifiableList(builder.lines) : offsetLines(builder.lines, offset);
    }

    public LyricItem.Title getTitle() {
        return title;
    }

    public LyricItem.Artist getArtist() {
        return artist;
    }

    public LyricItem.Author getAuthor() {
        return author;
    }

    public LyricItem.Album getAlbum() {
        return album;
    }

    public LyricItem.Duration getDuration() {
        return duration;
    }

    public List<LyricItem.Line> getLines() {
        return lines;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        if (null != title) sb.append(title);
        if (null != album) sb.append('\n').append(album);
        if (null != artist) sb.append('\n').append(artist);
        if (null != author) sb.append('\n').append(author);
        if (null != duration) sb.append('\n').append(duration);
        for (LyricItem.Line line : lines) sb.append('\n').append(line);
        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static List<LyricItem.Line> offsetLines(List<LyricItem.Line> lines, LyricItem.Offset offset) {
        final long offsetMS = offset.getOffsetMS();
        List<LyricItem.Line> offsetLines = new ArrayList<>(lines.size());
        for (LyricItem.Line line : lines) offsetLines.add(new LyricItem.Line(line.getTimeMS() + offsetMS, line.getText()));
        return Collections.unmodifiableList(offsetLines);
    }

    public static class Builder implements LyricItem.Visitor1<Policy, Builder> {


        private LyricItem.Title title;
        private LyricItem.Artist artist;
        private LyricItem.Author author;
        private LyricItem.Album album;
        private LyricItem.Duration duration;
        private LyricItem.Offset offset;
        private List<LyricItem.Line> lines = new LinkedList<>();

        private Builder() {
        }

        public Builder addItem(LyricItem item, Policy policy) {
            return item.accept(policy, this);
        }

        public Builder addItems(List<LyricItem> items, Policy policy) {
            for (LyricItem item : items) item.accept(policy, this);
            return this;
        }

        public Builder setTitle(LyricItem.Title title) {
            this.title = title;
            return this;
        }

        public Builder setArtist(LyricItem.Artist artist) {
            this.artist = artist;
            return this;
        }

        public Builder setAuthor(LyricItem.Author author) {
            this.author = author;
            return this;
        }

        public Builder setAlbum(LyricItem.Album album) {
            this.album = album;
            return this;
        }

        public Builder setDuration(LyricItem.Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder setOffset(LyricItem.Offset offset) {
            this.offset = offset;
            return this;
        }

        public LyricGroup build() {
            Collections.sort(lines);
            return new LyricGroup(this);
        }

        @Override public Builder visitTitle(LyricItem.Title title, Policy policy) {
            this.title = policy.pick(this.title, title);
            return this;
        }

        @Override public Builder visitArtist(LyricItem.Artist artist, Policy policy) {
            this.artist = policy.pick(this.artist, artist);
            return this;
        }

        @Override public Builder visitAlbum(LyricItem.Album album, Policy policy) {
            this.album = policy.pick(this.album, album);
            return this;
        }

        @Override public Builder visitDuration(LyricItem.Duration duration, Policy policy) {
            this.duration = policy.pick(this.duration, duration);
            return this;
        }

        @Override public Builder visitAuthor(LyricItem.Author author, Policy policy) {
            this.author = policy.pick(this.author, author);
            return this;
        }

        @Override public Builder visitLine(LyricItem.Line line, Policy policy) {
            this.lines.add(line);
            return this;
        }

        @Override public Builder visitOffset(LyricItem.Offset offset, Policy policy) {
            this.offset = policy.pick(this.offset, offset);
            return this;
        }

    }
}
