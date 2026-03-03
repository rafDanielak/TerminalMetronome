package com.projects.metronome;

public class mtrnm {
    private static final int maxTempo = 300, minTempo = 40, maxBeats = 16;
    private int tempo, currentBeat = 0, beatCount = 4;
    private boolean isRunning = false;
    private static Visualiser vis;

    static {
        try {
            vis = new Visualiser(4);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public mtrnm(int tmp) {
        setTempo(tmp);
    }

    public mtrnm(int tmp, int beatCnt) {
        this(tmp);
        setBeatCount(beatCnt);
    }

    private void setVisBeats(int beat) {
        vis.setParameters(beat,vis.getVisSize(), vis.getDistance());
    }

    public void setTempo(int tmp) {
        if (tmp < minTempo) {
            this.tempo = -2;
        }
        else if (tmp > maxTempo) {
            this.tempo = -1;
        }
        else {
            this.tempo = tmp;
        }
    }

    public int getTempo() {
        return this.tempo;
    }

    public void setBeatCount(int beat) {
        if (beat < 2) {
            this.beatCount = -2;
        }
        else if (beat > maxBeats) {
            this.beatCount = -1;
        }
        else {
            this.beatCount = beat;
            this.setVisBeats(beat);
        }
    }

    public int getBeatCount() {
        return this.beatCount;
    }

    private int getCurrentBeat() {
        return this.currentBeat;
    }

    private void setCurrentBeat(int beat) {
        this.currentBeat = beat;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getNextBeat() {
        this.setCurrentBeat(this.getCurrentBeat() + 1);
        if (this.getCurrentBeat() > this.getBeatCount()) {
            this.setCurrentBeat(1);
        }
        return this.getCurrentBeat();
    }

}
