package com.projects.metronome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Visualiser {
    private size visSize = size.MEDIUM;
    private int distance = 3;
    private int beats;
    private List<String> arts = new ArrayList<>();

    public Visualiser(int beats){
        this.setBeats(beats);
        createArts();
//        doVisual(3000);
    }

    public Visualiser(int beats, size sz, int distance){
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

    private int getBeats() {
        return this.beats;
    }

    private void setBeats(int beats) {
        this.beats = beats;
    }

    public void setParameters(int beats, size sz, int distance) {
        this.setBeats(beats);
        this.setVisSize(sz);
        this.setDistance(distance);
        this.createArts();
    }

    private void eraseVisualResetCursor() {
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

    public static void showThem() {
        Integer[] ar = {1,3,4,6};
        for (int i : ar) {
            String ac = activeBeatArt(i);
            System.out.println(ac);
            String[] a = ac.split("\n");
            System.out.println(Arrays.toString(a));
            for (String kj : a) {
                System.out.println(kj.length());
            }
            String ic = idleBeatArt(i);
            System.out.println(ic);
            String[] ib = ic.split("\n");
            System.out.println(Arrays.toString(ib));
            for (String ij : ib) {
                System.out.println(ij.length());
            }

        }
    }

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
                    if (padding && j != 0) {
                        art[k].delete(art[k].length()-padOneSide,art[k].length());
                    }
                    if (j != 0) {
                        art[k].append(connections[k].repeat(dist/2));
                    }
                    art[k].append(beatLines[k]);

                    if (j != beats - 1) {
                        art[k].append(connections[k].repeat(dist/2));
                    }
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
        arts.forEach(System.out::println);
    }

    private void doVisual(int time) throws InterruptedException{
        while (true) {
            printVisual(4);
            Thread.sleep(time);
            eraseVisualResetCursor();
            Thread.sleep(time);
        }
    }
}
