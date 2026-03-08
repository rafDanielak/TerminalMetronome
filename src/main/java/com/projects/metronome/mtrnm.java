package com.projects.metronome;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.*;
import javax.sound.sampled.*;


public class mtrnm implements Runnable{
    private final int maxTempo = 300, minTempo = 40, maxBeats = 16;
    private int tempo, currentBeat = 0, beatCount = 4;
    private boolean running = false;
    private final Visualiser vis = Visualiser.getInstance(beatCount,size.MEDIUM,3);

    private static final AudioInputStream audioRegular, audioAccent;

    static {
        try {

            audioRegular = AudioSystem.getAudioInputStream(new File("src/main/resources/ding.wav"));
            audioAccent = AudioSystem.getAudioInputStream(new File("src/main/resources/chord.wav"));
            audioRegular.mark(audioRegular.available());
            audioAccent.mark(audioAccent.available());
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String helpUsage = "Usage: mtrnm <tempo> [-b <number_of_beats>] [-s <size_of_visual>] [-d <visual_distance_between_beats>]";
    private static final String helpSettings = "Type: \n\tt - to set tempo in BPM \n\tb - to set the number of beats \n\ts - to set the size of the display\n\td - to set the visual beat distance \n\tm - to enable the muted beats mode";
    private static final String helpRun = "Type anything to pause/resume, type s to enable settings, type e to exit";

    public mtrnm() {}

    public mtrnm(int tmp) {
        setTempo(tmp);
    }

    public mtrnm(int tmp, int beatCnt) {
        this(tmp);
        setBeatCount(beatCnt);
    }

    public static void main(String[] args) throws LineUnavailableException, IOException {
        SoundPlayer.playClip(audioAccent,true);
        SoundPlayer.playClip(audioRegular,true);
        mtrnm metro;
        if (args.length == 0) {
            System.out.println("No positional agrument <tempo> was given. It needs to be set now");
            System.out.println(helpUsage);
            System.out.println();
            metro = new mtrnm();
            do {
                metro.inputSettings();
            } while (metro.getTempo() == 0);
        }
        else {
            System.out.println("running with arguments not implemented yet");
            return;
//            List<String> argList = List.of(args);
//            try {
//                tempo = Integer.parseInt(argList.getFirst());
//
//            } catch (NumberFormatException ex) {
//                System.out.println("Input the correct ");
//            }
//            if (argList.size() > 1) {
//
//            }
        }

        System.out.println(helpRun);

        while (true) {
            metro.run();
            Scanner scan = new Scanner(System.in);
            String line = scan.nextLine();
            metro.stopRunning();
            if (line.equals("s")) {
                metro.inputSettings();
            } else if (line.equals("e")) {
                System.out.println("Goodbye");
                return;
            }
            scan.nextLine();
        }
    }

    private void setVisParameters(int beat, size sz, int distance) {
        vis.setParameters(beat, sz, distance);
    }

    private void setBeat(int beat) {
        setVisParameters(beat, this.getVis().getVisSize(), this.getVis().getDistance());
    }

    private void setSize(size sz) {
        setVisParameters(this.getVis().getBeats(),sz,this.getVis().getDistance());
    }

    private void setDistance(int dist) {
        setVisParameters(this.getVis().getBeats(),this.getVis().getVisSize(), dist);
    }



    private Visualiser getVis() {
        return this.vis;
    }

    public int setTempo(int tmp) {
        if (tmp < minTempo) {
            return -2;
        }
        else if (tmp > maxTempo) {
            return -1;
        }
        this.tempo = tmp;
        return 0;
    }

    public int getTempo() {
        return this.tempo;
    }

    public int setBeatCount(int beat) {
        if (beat < 2) {
            return -2;
        }
        else if (beat > maxBeats) {
            return -1;
        }
        this.beatCount = beat;
        this.setBeat(beat);
        return 0;
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
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    
    public void inputSettings() {
        if (isRunning()) {
            return;
        }
        System.out.println(helpSettings);
        Scanner scan = new Scanner(System.in);
        String token = scan.next();
        token = token.toLowerCase();
        switch (token) {
            case "t" -> {
                System.out.printf("Write the desired tempo in BPM (must be between %d and %d)\n", minTempo, maxTempo);
                Integer tm = null;
                while (tm == null) {
                    try {
                        tm = scan.nextInt();
                        if (this.setTempo(tm) != 0) {
                            tm = null;
                            throw new InputMismatchException();
                        }
                    } catch (InputMismatchException e) {
                        scan.next();
                        System.out.println("Try again");
                    }
                }
                System.out.printf("The tempo is now %d\n",this.getTempo());
            }
            case "b" -> {
                System.out.printf("Write the wanted number of beats (smaller than %d)\n",maxBeats);
                Integer bt = null;
                while (bt == null) {
                    try {
                        bt = scan.nextInt();
                        if (this.setBeatCount(bt) != 0) {
                            bt = null;
                            throw new InputMismatchException();
                        }
                    } catch (InputMismatchException e) {
                        scan.next();
                        System.out.println("try again");
                    }
                }
                System.out.printf("The number of beats is now %d\n",this.getBeatCount());
            }
            case "s" -> {
                System.out.println("Possible sizes: TINY, SMALL, MEDIUM, BIG");
                System.out.printf("Write the desired size (current size is %s)\n",this.getVis().getVisSize().name());
                size sz = null;
                while (sz == null) {
                    String s = scan.nextLine();
                    sz = size.getSizeFromString(s);
                    if (sz == null) {
                        System.out.println("try again");
                    }
                }
                this.setSize(sz);
                System.out.printf("Size is now %s\n",this.getVis().getVisSize().name());
            }
            case "d" -> {
                System.out.printf("Write the desired distance (current distance is %d)\n",this.getVis().getDistance());
                Integer dist = null;
                while (dist == null) {
                    try {
                        dist = scan.nextInt();
                        this.setDistance(dist);
                    } catch (InputMismatchException ex) {
                        System.out.println("try again");
                    }
                }
                System.out.printf("The distance is now %d\n",this.getVis().getDistance());
            }
            case "m" -> {
                System.out.println("muted beats not implemented yet");
            }
            default -> {
                System.out.println("Unknown option");
            }
        }
    }

    public int getNextBeat() {
        this.setCurrentBeat(this.getCurrentBeat() + 1);
        if (this.getCurrentBeat() > this.getBeatCount()) {
            this.setCurrentBeat(1);
        }
        return this.getCurrentBeat();
    }

    public void printVis(int beat) {
        this.getVis().printVisual(beat);
    }

    public void clearVis() {
        if (currentBeat == 0) {
            return;
        }
        this.getVis().eraseVisualResetCursor();
    }

    public void run() {
        if (isRunning()) {
            return;
        }
        int oscill = 60000 / this.getTempo();
        setRunning(true);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        scheduler.scheduleAtFixedRate(()->{
            if (!isRunning()) {
                scheduler.shutdown();
                scheduler.close();
                return;
            }
            int beat = this.getNextBeat();
            clearVis();
            if (beat == 1) {
                SoundPlayer.playClip(audioAccent,false);
            } else {
                SoundPlayer.playClip(audioRegular,false);
            }
            printVis(beat);},0,oscill,TimeUnit.MILLISECONDS);
    }



    public void stopRunning() {
        if (!isRunning()) {
            return;
        }
        setRunning(false);
    }
}
