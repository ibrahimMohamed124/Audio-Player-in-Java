import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MusicPlayerGUI().setVisible(true);

//                System.out.println(song.getSongTitle());
//                System.out.println(song.getSongArtist());

            }
        });
    }
}
