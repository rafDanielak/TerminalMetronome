package com.projects.metronome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Visualiser {
    private static Visualiser instance;
    private size visSize;
    private int distance;
    private int beats;
    private final List<String> arts = new ArrayList<>();

    public static Visualiser getInstance(int beats, size sz, int distance) {
        if (instance == null) {
            instance = new Visualiser(beats, sz, distance);
        }
        instance.setParameters(beats,sz,distance);
        return instance;
    }

    private Visualiser(int beats, size sz, int distance){
        this.setParameters(beats,sz,distance);
    }

    public size getVisSize() {
        return visSize;
    }

    private void setVisSize(size visSize) {
        this.visSize = visSize;
    }

    public int getDistance() {
        return distance;
    }

    private void setDistance(int distance) {
        if (distance > 8) {
            distance = 8;
        }
        if (distance < 3) {
            distance = 3;
        }
        this.distance = distance;
    }

    int getBeats() {
        return this.beats;
    }

    private void setBeats(int beats) {
        this.beats = beats;
    }

    public List<String> getArts() {
        if (this.arts.isEmpty()) {
            return null;
        }
        return arts;
    }

    public void setParameters(int beats, size sz, int distance) {
        this.setBeats(beats);
        this.setVisSize(sz);
        this.setDistance(distance);
        this.createArts();
    }

    public void eraseVisualResetCursor() {
        System.out.print("\r");
        int line = this.getVisSize().getS();
        while (line > 0) {
            System.out.print("\u001b[1A\u001b[0K");
            line--;
        }
    }

    private static String activeBeatArt(size s) {
        if (s == size.SMALL) {
            return " /***\\ \n|*****|\n \\***/ ";
        }
        if (s == size.MEDIUM) {
            return " /*******\\ \n|*********|\n|*********|\n \\*******/ ";
        }
        if (s == size.BIG) {
            return "  /*********\\  \n |***********| \n|*************|\n|*************|\n |***********| \n  \\*********/  ";
        }
        return "   \n{@}\n   ";
    }

    private static String idleBeatArt(size s) {
        if (s == size.SMALL) {
            return "   \n{@}\n   ";
        }
        if (s == size.MEDIUM) {
            return "     \n/*@*\\\n\\*@*/\n     ";
        }
        if (s == size.BIG) {
            return "     \n     \n/*@*\\\n\\*@*/\n     \n     ";
        }
        return " \n*\n ";
    }

    private static String beatConnection(size s) {
        if (s == size.SMALL) {
            return "    \n--__\n    ";
        }
        if (s == size.MEDIUM) {
            return "    \n--__\n--__\n    ";
        }
        if (s == size.BIG) {
            return "    \n    \n--__\n--__\n    \n    ";
        }
        return "    \n--__\n    ";
    }

    private static int paddingSize(size s) {
        return activeBeatArt(s).indexOf('\n') - idleBeatArt(s).indexOf('\n');
    }
// TO BE REMOVED

//    public static void showThem() {
//        Integer[] ar = {1,3,4,6};
//        for (int i : ar) {
//            String ac = activeBeatArt(i);
//            System.out.println(ac);
//            String[] a = ac.split("\n");
//            System.out.println(Arrays.toString(a));
//            for (String kj : a) {
//                System.out.println(kj.length());
//            }
//            String ic = idleBeatArt(i);
//            System.out.println(ic);
//            String[] ib = ic.split("\n");
//            System.out.println(Arrays.toString(ib));
//            for (String ij : ib) {
//                System.out.println(ij.length());
//            }
//
//        }
//    }

    // arts get created only once, until attributes are to be changed
    private void createArts() {
        arts.clear();
        int beats = this.getBeats();
        size sz = this.getVisSize();
        int dist = this.getDistance();
        int szInt = sz.getS();
        int padOneSide = paddingSize(sz)/2;
        for (int i = 0; i < beats; i++) {
            int j = 0;
            StringBuilder[] art = new StringBuilder[szInt];
            Arrays.setAll(art,_ -> new StringBuilder());
            while (j < beats) {
                String[] beatLines;
                boolean padding = false;
                if (j == i) {
                    beatLines = Visualiser.activeBeatArt(sz).split("\n");
                    padding = true;
                }
                else {
                    beatLines = Visualiser.idleBeatArt(sz).split("\n");
                }
                String[] connections = Visualiser.beatConnection(sz).split("\n");
                for (int k = 0; k < beatLines.length; k++) {
                    // active beat not on first position doesn't need have a connector that needs to be padded
                    // the other ones require "padding" which is basically removing a part of a connector
                    if (padding && j != 0) {
                        art[k].delete(art[k].length()-padOneSide,art[k].length());
                    }
                    // but non-active beats on first position require initial whitespace padding
                    if (!padding && j == 0) {
                        art[k].append(" ".repeat(padOneSide));
                    }
                    // adding connection on the left side of the beat (the first one shouldn't have it)
                    if (j != 0) {
                        art[k].append(connections[k].repeat(dist/2));
                    }

                    // this appends the actual beat
                    art[k].append(beatLines[k]);

                    // adding connection on the right side of the beat (the last one doesn't have it)
                    if (j != beats - 1) {
                        art[k].append(connections[k].repeat(dist/2));
                    }
                    // adding the right side of the "padding"
                    if (padding && j != beats - 1) {
                        art[k].delete(art[k].length()-padOneSide,art[k].length());
                    }
                }
                j++;
            }
            StringBuilder artString = new StringBuilder("");
            for (StringBuilder sb : art) {
                artString.append(sb).append('\n');
            }
            arts.add(artString.toString());
        }
    }


    public void printVisual(int activeBeat) {
        var visArts = this.getArts();
        if (visArts == null) {
            return;
        }
        String arts = visArts.get(activeBeat - 1);
        System.out.print(arts);
    }

}
