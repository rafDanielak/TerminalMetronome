package com.projects.metronome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

enum sizes {
    TINY,
    SMALL,
    MEDIUM,
    BIG;
}

public class Visualiser {
    private int size = 4, distance = 3;
    private int beats;
    private List<String> arts = new ArrayList<String>();

    public Visualiser(int beats) throws InterruptedException {
        this.beats = beats;
        System.out.println("heloFromVis");
        createArts(4,4);
//        doVisual(3000);
    }

    public void setBeats(int beats) {
        this.beats = beats;
    }

    private void eraseVisualResetCursor() {
        System.out.print("\r");
        int line = size;
        while (line > 0) {
            System.out.print("\u001b[1A\u001b[0K");
            line--;
        }
    }


    private static String activeBeatArt(int num) {
        if (num == 3) {
            return " /***\\ \n|*****|\n \\***/ ";
        }
        if (num == 4) {
            return " /*******\\ \n|*********|\n|*********|\n \\*******/ ";
        }
        if (num == 6) {
            return "  /*********\\  \n |***********| \n|*************|\n|*************|\n |***********| \n  \\*********/  ";
        }
        return "   \n{@}\n   ";
    }

    private static String idleBeatArt(int num) {
        if (num == 3) {
            return "   \n{@}\n   ";
        }
        if (num == 4) {
            return "     \n/*@*\\\n\\*@*/\n     ";
        }
        if (num == 6) {
            return "     \n     \n/*@*\\\n\\*@*/\n     \n     ";
        }
        return " \n*\n ";
    }

    private static String beatConnection(int num) {
        if (num == 3) {
            return "    \n--__\n    ";
        }
        if (num == 4) {
            return "    \n--__\n--__\n    ";
        }
        if (num == 6) {
            return "    \n    \n--__\n--__\n    \n    ";
        }
        return "    \n--__\n    ";
    }

    public static int paddingSize(int num) {
        return activeBeatArt(num).indexOf('\n') - idleBeatArt(num).indexOf('\n');
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
    private void createArts(int size, int dist) {
        arts.clear();
        int padOneSide = paddingSize(size)/2;
        int size2 = size;
        if (size != 3 && size != 4 && size != 6) {
            size2 = 3;
        }
        for (int i = 0; i < beats; i++) {
            int j = 0;
            StringBuilder[] art = new StringBuilder[size2];
            Arrays.setAll(art,_ -> new StringBuilder());
            while (j < beats) {
                String[] beatLines;
                boolean padding = false;
                if (j == i) {
                    beatLines = Visualiser.activeBeatArt(size).split("\n");
                    padding = true;
                }
                else {
                    beatLines = Visualiser.idleBeatArt(size).split("\n");
                }
                String[] connections = Visualiser.beatConnection(size).split("\n");
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
