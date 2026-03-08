package com.projects.metronome;

import javax.sound.sampled.*;
import java.io.IOException;

public class SoundPlayer {

    public static void playClip(AudioInputStream audioInStream, boolean muteClip) {
        new Thread(()->{
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(audioInStream);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.CLOSE) {
                        clip.close();
                    }
                });

                if (muteClip) {
                    BooleanControl mute = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
                    mute.setValue(true);
                }
                clip.start();
                audioInStream.reset();
            } catch (LineUnavailableException | IOException e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }
}
