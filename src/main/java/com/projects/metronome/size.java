package com.projects.metronome;

public enum size {
    TINY(1),
    SMALL(3),
    MEDIUM(4),
    BIG(6);

    private final int s;

    size(int size) {
        this.s = size;
    }

    public int getS() {
        return s;
    }
}
