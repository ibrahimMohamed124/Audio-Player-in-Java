import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

// Using a modern Look and Feel (like "Nimbus" or "Metal" set at startup, though not required for the code itself)
public class MusicPlayerGUI extends JFrame {

    // Define a darker, more modern color palette
    public static final Color FRAME_BACKGROUND = new Color(20, 20, 25); // Very dark gray/black
    public static final Color COMPONENT_BACKGROUND = new Color(30, 30, 35); // Slightly lighter for containers
    public static final Color TEXT_COLOR = new Color(220, 220, 220); // Off-white
    public static final Color TEXT_BLACK_COLOR = new Color(20, 20, 25); // Off-white
    public static final Color ACCENT_COLOR = new Color(100, 180, 255); // Blue accent

    private final MusicPlayer musicPlayer;
    private final JFileChooser jFileChooser;
    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;
    private JLabel labelCurrentTime, labelEnd; // Renamed for clarity in the class scope
    private JLabel songImage; // get song image


    // Assuming Song class is defined elsewhere

    public MusicPlayerGUI() {
        super("Modern Music Player 🎵");

        // Basic frame setup
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing for a more modern feel

        // Title  bar icon
        ImageIcon imageIcon = new ImageIcon("src/assets/images/modio1.png");
        setIconImage(imageIcon.getImage());
        // Use BorderLayout for the main content pane
        setLayout(new BorderLayout());
        getContentPane().setBackground(FRAME_BACKGROUND);

        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("src/assets"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));

        // Setup key bindings before adding components
        setupKeyBindings();

        addGUIComponents();
    }

    private void addGUIComponents() {
        // 1. TOP: Tool Bar (File/Load)
        addToolBar();

        // 2. CENTER: Main Content (Image, Title, Artist)
        addCenterContent();

        // 3. SOUTH: Playback Controls (Slider, Buttons)
        addPlaybackControls();
    }

    private void addToolBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COMPONENT_BACKGROUND);

        JMenu songMenu = new JMenu("Song");
        songMenu.setForeground(TEXT_COLOR);

        // Load Song Item
        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.setForeground(TEXT_BLACK_COLOR);
        loadSong.addActionListener(e -> { // Using lambda for concise action listener
            int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
            File selectedFile = jFileChooser.getSelectedFile();

            if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                // --- THIS IS THE CRITICAL SECTION TO ENSURE EXECUTION ---

                // 1. Create the Song object
                Song song = new Song(selectedFile.getPath());

                // 2. Load the song into the player (which also calls playCurrentSong())
                musicPlayer.loadSong(song);

                // 3. Update the UI elements
                updateSongTitleAndArtist(song);
                updatePlaybackSlider(song);
                enablePauseButtonDisablePlayButton();
            }
        });
        songMenu.add(loadSong);

        // Simple Playlist Menu (keeping structure but simplifying implementation)
        JMenu playlistMenu = new JMenu("Playlist");
        playlistMenu.setForeground(TEXT_COLOR);
        playlistMenu.add(new JMenuItem("Create Playlist"));
        playlistMenu.add(new JMenuItem("Load Playlist"));

        menuBar.add(songMenu);
        menuBar.add(playlistMenu);

        // Add the menu bar to the top (North) of the frame
        setJMenuBar(menuBar);
    }

    private void addCenterContent() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for flexible centering
        centerPanel.setBackground(FRAME_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0); // Padding

        // 1. Song Image (Album Art)
// نحفظه كمتحول عضو علشان نقدر نحدثه
        songImage = new JLabel();
        songImage.setPreferredSize(new Dimension(250, 250));
        songImage.setHorizontalAlignment(SwingConstants.CENTER);
        songImage.setVerticalAlignment(SwingConstants.CENTER);
        songImage.setIcon(loadImage("src/assets/drive-download-20250713T123450Z-1-001/record.png"));
        songImage.setPreferredSize(new Dimension(250, 250)); // Set a preferred size for the image
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(songImage, gbc);

        // 2. Song Title
        songTitle = new JLabel("Song Title");
        songTitle.setFont(new Font("Dialog", Font.BOLD, 28)); // Slightly larger font
        songTitle.setForeground(ACCENT_COLOR); // Use accent color for title
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        centerPanel.add(songTitle, gbc);

        // 3. Song Artist
        songArtist = new JLabel("Artist Name");
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 20));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        centerPanel.add(songArtist, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void addPlaybackControls() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS)); // Vertical stack
        controlPanel.setBackground(COMPONENT_BACKGROUND);

        // 1. Playback Slider with Time Labels
        playbackSlider = new JSlider(0, 100, 0);
        playbackSlider.setBackground(COMPONENT_BACKGROUND);
        playbackSlider.setForeground(ACCENT_COLOR);
        playbackSlider.setMajorTickSpacing(100);
        playbackSlider.setPaintTrack(true);
        playbackSlider.setPaintTicks(false);
        // Remove the default labels until a song is loaded, but keep space
        playbackSlider.setPaintLabels(false);
        playbackSlider.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        // Time labels panel for alignment
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBackground(COMPONENT_BACKGROUND);
        timePanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));

        labelCurrentTime = new JLabel("00:00");
        labelCurrentTime.setFont(new Font("Dialog", Font.BOLD, 14));
        labelCurrentTime.setForeground(TEXT_COLOR);
        timePanel.add(labelCurrentTime, BorderLayout.WEST);

        labelEnd = new JLabel("00:00");
        labelEnd.setFont(new Font("Dialog", Font.BOLD, 14));
        labelEnd.setForeground(TEXT_COLOR);
        timePanel.add(labelEnd, BorderLayout.EAST);


        // 2. Playback Buttons
        playbackBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5)); // Spacing between buttons
        playbackBtns.setBackground(COMPONENT_BACKGROUND);

        // Reusing original button setup logic but with modern styling
        JButton previousBtn = createStyledButton("src/assets/drive-download-20250713T123450Z-1-001/previous.png");
        JButton playButton = createStyledButton("src/assets/drive-download-20250713T123450Z-1-001/play.png");
        JButton pauseButton = createStyledButton("src/assets/drive-download-20250713T123450Z-1-001/pause.png");
        JButton nextButton = createStyledButton("src/assets/drive-download-20250713T123450Z-1-001/next.png");

        // Action Listeners
        playButton.addActionListener(e -> {
            enablePauseButtonDisablePlayButton();
            musicPlayer.playCurrentSong();
        });

        pauseButton.addActionListener(e -> {
            enablePlayButtonDisablePauseButton();
            musicPlayer.pauseSong();
        });
        pauseButton.setVisible(false); // Start with play button visible

        playbackBtns.add(previousBtn);
        playbackBtns.add(playButton);
        playbackBtns.add(pauseButton);
        playbackBtns.add(nextButton);

        // Add components to the main control panel
        controlPanel.add(playbackSlider);
        controlPanel.add(timePanel);
        controlPanel.add(playbackBtns);

        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Helper method to create styled buttons for playback controls.
     */
    private JButton createStyledButton(String imagePath) {
        JButton button = new JButton(loadImage(imagePath));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // Make button transparent
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Indicate it's clickable
        // Added a Rollover effect for modernity (optional, but nice)
        button.setRolloverIcon(loadImage(imagePath, 1.1f)); // 10% larger on hover
        return button;
    }


    // --- UTILITY METHODS (Slightly Refined) ---

    public void setPlaybackSliderValue(int frame) {
        playbackSlider.setValue(frame);
    }

    public void updateCurrentTimeLabel(int frame, double frameRatePerMs) {
        // Only update if labelEnd has a valid text (i.e., song is loaded)
        if (labelEnd != null && !labelEnd.getText().equals("00:00")) {
            int currentTimeInMs = (int) (frame / frameRatePerMs);
            int minutes = (currentTimeInMs / 1000) / 60;
            int seconds = (currentTimeInMs / 1000) % 60;
            labelCurrentTime.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    private void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());

        // عرض صورة الألبوم إن وُجدت
        if (song.getAlbumArt() != null) {
            Image scaled = song.getAlbumArt().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            songImage.setIcon(new ImageIcon(scaled));
        } else {
            // fallback إلى الصورة الافتراضية
            songImage.setIcon(loadImage("src/assets/drive-download-20250713T123450Z-1-001/record.png"));
        }
    }


    private void updatePlaybackSlider(Song song) {
        // Set Max and ensure labels are painted
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());
        playbackSlider.setPaintLabels(false); // We are using separate labels (labelCurrentTime, labelEnd) now

        // Update the end time label
        labelEnd.setText(song.getSongLength());
    }

    private void enablePauseButtonDisablePlayButton() {
        // Assumes order: previous, play, pause, next
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        playButton.setVisible(false);
        pauseButton.setVisible(true);
    }

    private void enablePlayButtonDisablePauseButton() {
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        playButton.setVisible(true);
        pauseButton.setVisible(false);
    }

    private void setupKeyBindings() {
        // Keep the original key binding logic
        JComponent contentPane = (JComponent) this.getContentPane();
        InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = contentPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "togglePlayPause");

        actionMap.put("togglePlayPause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (musicPlayer.isPlaying()) {
                    musicPlayer.pauseSong();
                    enablePlayButtonDisablePauseButton();
                } else {
                    musicPlayer.playCurrentSong();
                    enablePauseButtonDisablePlayButton();
                }
            }
        });
    }

    /**
     * Loads and resizes an image icon.
     * @param imagePath The path to the image file.
     * @param scaleFactor Factor to scale the image (e.g., 1.0 for original, 1.1 for 10% larger)
     */
    private ImageIcon loadImage(String imagePath, float scaleFactor) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            if (image == null) return null;

            int newWidth = (int) (image.getWidth() * scaleFactor);
            int newHeight = (int) (image.getHeight() * scaleFactor);

            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            // e.printStackTrace(); // Uncomment for full error trace
        }
        return null;
    }

    private ImageIcon loadImage(String imagePath) {
        return loadImage(imagePath, 1.0f);
    }

    public static void main(String[] args) {
        // Optionally set a modern Look and Feel (e.g., Nimbus) for a better default look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to the default Metal L&F or do nothing
        }

        // Running the GUI on the Event Dispatch Thread (standard Swing practice)
        SwingUtilities.invokeLater(() -> {
            new MusicPlayerGUI().setVisible(true);
        });
    }
}