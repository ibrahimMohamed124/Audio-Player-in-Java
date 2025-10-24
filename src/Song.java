import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

public class Song {
    private String songTitle;
    private String songArtist;
    private String songLength;
    private BufferedImage albumArt;
    private final String filePAth;
    private Mp3File mp3File;
    private double frameRatePerMillisecond;

    public Song(String filePAth){
        this.filePAth = filePAth;
        try {
            mp3File = new Mp3File(new File(filePAth));
            songLength = convertToSongLengthFormat();
            frameRatePerMillisecond = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();

            AudioFile audioFile = AudioFileIO.read(new File(filePAth));

            // read through the metadata of the audio file
            Tag tag = audioFile.getTag();
            if(tag != null){
                songTitle = tag.getFirst(FieldKey.TITLE);
                songArtist = tag.getFirst(FieldKey.ARTIST);

                // قراءة صورة الألبوم (Album Art)
                var artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    byte[] imageData = artwork.getBinaryData();
                    albumArt = ImageIO.read(new ByteArrayInputStream(imageData));
                }

            }else {
                // could not read through .mp3 file metadata
                songTitle = "N/A";
                songArtist = "N/A";
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String convertToSongLengthFormat(){
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        return String.format("%02d:%02d", minutes,seconds);
    }

    // getters
    public String getSongTitle(){return songTitle;}

    public String getSongArtist(){return songArtist;}

    public String getSongLength(){return songLength;}

    public String getFilePAth(){return filePAth;}

    public Mp3File getMp3File(){return mp3File;}

    public BufferedImage getAlbumArt() {
        return albumArt;
    }

}
