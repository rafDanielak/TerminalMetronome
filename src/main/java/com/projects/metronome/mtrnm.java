package com.projects.metronome;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;


public class mtrnm implements Runnable{
    private final int maxTempo = 300, minTempo = 40, maxBeats = 16;
    private int tempo, currentBeat = 0, beatCount = 4;
    private boolean running = false;
    private final Visualiser vis = Visualiser.getInstance(beatCount,size.MEDIUM,3);

    
    private static final String helpUsage = "Usage: mtrnm <tempo> [-b <number_of_beats>] [-s <size_of_visual>] [-d <visual_distance_between_beats>]";
    private static final String help = "Type: \n\tt - to set tempo in BPM \n\tb - to set the number of beats \n\ts - to set the size of the display\n\td - to set the visual beat distance \n\tm - to enable the muted beats mode";
    public mtrnm(int tmp) {
        setTempo(tmp);
    }

    public mtrnm(int tmp, int beatCnt) {
        this(tmp);
        setBeatCount(beatCnt);
    }

    public static void main(String[] args) {
        int tempo;
        if (args.length == 0) {
            System.out.println("No positional agrument <tempo> was given. It needs to be set now");
            System.out.println(helpUsage);
            System.out.println();
            System.out.println(help.substring(help.indexOf('\n')));
            
        }
        else {
            List<String> argList = List.of(args);
            try {
                tempo = Integer.parseInt(argList.getFirst());
                
            } catch (NumberFormatException ex) {
                System.out.println("Input the correct ");
            }
            if (argList.size() > 1) {
                
            }
        }
        
        mtrnm metro = new mtrnm(tempo);
        System.out.println(metro.getTempo());
        metro.run();

        Scanner scan = new Scanner(System.in);
        scan.nextLine();
        metro.stopRunning();
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
    
    public int inputSettings() {
        if (isRunning()) {
            return -1;
        }
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
                        System.out.println("try again");
                    }
                }
                System.out.printf("The tempo is now %d\n",this.getTempo());
            }
            case "b" -> {
                System.out.printf("Write the wanted number of beats (smaller than %d)",maxBeats);
                Integer bt = null;
                while (bt == null) {
                    try {
                        bt = scan.nextInt();
                        if (this.setBeatCount(bt) != 0) {
                            bt = null;
                            throw new InputMismatchException();
                        }
                    } catch (InputMismatchException e) {
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
                System.out.printf("Size is now %s",this.getVis().getVisSize().name());
            }
            case "d" -> {
                System.out.printf("Write the desired distance (current distance is %d)",this.getVis().getDistance());
                Integer dist = null;
                while (dist == null) {
                    try {
                        dist = scan.nextInt();
                        this.setDistance(dist);
                    } catch (InputMismatchException ex) {
                        System.out.println("try again");
                    }
                }
                System.out.printf("The distance is now %d",this.getVis().getDistance());
            }
            case "m" -> {
            }
            default -> {
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
        System.out.println(oscill);
        setRunning(true);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(()->{
            if (!isRunning()) {
                scheduler.shutdown();
                scheduler.close();
                return;
            }
            clearVis();
            printVis(this.getNextBeat());},0,oscill,TimeUnit.MILLISECONDS);
            System.lo
    }



    public void stopRunning() {
        if (!isRunning()) {
            return;
        }
        setRunning(false);
    }
}
