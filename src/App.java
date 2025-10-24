import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MusicPlayerGUI().setVisible(true);

//                Song song = new Song("src/assets/Duncan Laurence - Loving You Is A Losing Game (Lyrics) _ Arcade(MP3_320K).mp3");
//                System.out.println(song.getSongTitle());
//                System.out.println(song.getSongArtist());
            }
        });
    }
}
