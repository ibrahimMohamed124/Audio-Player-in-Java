import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
     private MusicPlayerGUI musicPlayerGUI;
     private Song currentSong;

     private AdvancedPlayer advancedPlayer;
     private Thread musicThread;

     private boolean isPaused = false;
     private boolean isPlaying = false;

     private int currentFrame = 0;
     private int totalFrames = 0;

     public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
          this.musicPlayerGUI = musicPlayerGUI;
     }

     public void loadSong(Song song) {
          stopSong();
          currentSong = song;
          currentFrame = 0;
          isPaused = false;

          if (currentSong != null) {
               totalFrames = song.getMp3File().getFrameCount();
               playCurrentSong();
          }
     }

     public void pauseSong() {
          if (advancedPlayer != null && isPlaying) {
               isPaused = true;
               isPlaying = false;
               advancedPlayer.close();
          }
     }

     public void stopSong() {
          if (advancedPlayer != null) {
               advancedPlayer.close();
               advancedPlayer = null;
          }
          isPaused = false;
          isPlaying = false;
          currentFrame = 0;
     }

     public void playCurrentSong() {
          try {
               FileInputStream fis = new FileInputStream(currentSong.getFilePAth());
               BufferedInputStream bis = new BufferedInputStream(fis);

               advancedPlayer = new AdvancedPlayer(bis);
               advancedPlayer.setPlayBackListener(this);

               musicThread = new Thread(() -> {
                    try {
                         isPlaying = true;
                         isPaused = false;
                         advancedPlayer.play(currentFrame, totalFrames);
                    } catch (Exception e) {
                         e.printStackTrace();
                    }
               });

               musicThread.start();
               startPlaybackSliderThread();

          } catch (Exception e) {
               e.printStackTrace();
          }
     }


     private void startPlaybackSliderThread() {
          new Thread(() -> {
               long durationMs = currentSong.getMp3File().getLengthInMilliseconds();
               double msPerFrame = (double) durationMs / totalFrames;

               while (isPlaying && !isPaused && currentFrame < totalFrames) {
                    try {
                         currentFrame++;
                         musicPlayerGUI.setPlaybackSliderValue(currentFrame);
                         musicPlayerGUI.updateCurrentTimeLabel(currentFrame, 1.0 / msPerFrame);
                         Thread.sleep((long) msPerFrame);
                    } catch (Exception e) {
                         e.printStackTrace();
                    }
               }
          }).start();
     }

     public boolean isPlaying() {
          return isPlaying;
     }

     @Override
     public void playbackFinished(PlaybackEvent evt) {
          if (isPaused) {
               currentFrame = evt.getFrame();
               System.out.println("Paused at frame: " + currentFrame);
          } else {
               System.out.println("Finished at frame: " + evt.getFrame() + "/" + totalFrames);
               currentFrame = 0;
               isPlaying = false;
          }
     }
}
