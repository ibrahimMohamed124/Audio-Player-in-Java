import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

// 🎧 واجهة Music Player حديثة وجميلة
public class MusicPlayerGUI extends JFrame {

    public static final Color FRAME_BACKGROUND = new Color(20, 20, 25);
    public static final Color COMPONENT_BACKGROUND = new Color(30, 30, 35);
    public static final Color TEXT_COLOR = new Color(220, 220, 220);
    public static final Color TEXT_BLACK_COLOR = new Color(20, 20, 25);
    public static final Color ACCENT_COLOR = new Color(100, 180, 255);

    private final MusicPlayer musicPlayer;
    private final JFileChooser jFileChooser;
    private JLabel songTitle, songArtist, songImage;
    private JPanel playbackBtns;
    private JSlider playbackSlider;
    private JLabel labelCurrentTime, labelEnd;

    public String prevBtn = "src/assets/drive-download-20250713T123450Z-1-001/previous.png";
    public String playBtn = "src/assets/drive-download-20250713T123450Z-1-001/play.png";
    public String pauseBtn = "src/assets/drive-download-20250713T123450Z-1-001/pause.png";
    public String nextBtn = "src/assets/drive-download-20250713T123450Z-1-001/next.png";

    public MusicPlayerGUI() {
        super("Modern Music Player 🎵");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        ImageIcon imageIcon = new ImageIcon("src/assets/images/modio1.png");
        setIconImage(imageIcon.getImage());

        setLayout(new BorderLayout());
        getContentPane().setBackground(FRAME_BACKGROUND);

        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("src/assets"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));

        setupKeyBindings();
        addGUIComponents();
    }

    private void addGUIComponents() {
        addToolBar();
        addCenterContent();
        addPlaybackControls();
    }

    private void addToolBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COMPONENT_BACKGROUND);

        JMenu songMenu = new JMenu("Song");
        songMenu.setForeground(TEXT_COLOR);

        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.setForeground(TEXT_BLACK_COLOR);
        loadSong.addActionListener(e -> {
            int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
            File selectedFile = jFileChooser.getSelectedFile();
            if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                Song song = new Song(selectedFile.getPath());
                musicPlayer.loadSong(song);
                updateSongTitleAndArtist(song);
                updatePlaybackSlider(song);
                enablePauseButtonDisablePlayButton();
            }
        });
        songMenu.add(loadSong);

        JMenu playlistMenu = new JMenu("Playlist");
        playlistMenu.setForeground(TEXT_COLOR);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(e -> new PlaylistUI().setVisible(true));

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(e -> new PlaylistUI().setVisible(true));

        playlistMenu.add(createPlaylist);
        playlistMenu.add(loadPlaylist);

        menuBar.add(songMenu);
        menuBar.add(playlistMenu);

        setJMenuBar(menuBar);
    }

    private void addCenterContent() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(FRAME_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        songImage = new JLabel();
        songImage.setPreferredSize(new Dimension(250, 250));
        songImage.setHorizontalAlignment(SwingConstants.CENTER);
        songImage.setIcon(loadImage("src/assets/drive-download-20250713T123450Z-1-001/record.png"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(songImage, gbc);

        songTitle = new JLabel("Song Title");
        songTitle.setFont(new Font("Dialog", Font.BOLD, 28));
        songTitle.setForeground(ACCENT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        centerPanel.add(songTitle, gbc);

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
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(COMPONENT_BACKGROUND);

        // === Modern Custom Slider ===
        playbackSlider = new JSlider(0, 100, 0);
        playbackSlider.setOpaque(false);
        playbackSlider.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        playbackSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        playbackSlider.setFocusable(false);
        // 🎵 Draggable JSlider
        playbackSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (musicPlayer.isPlaying()) {
                    musicPlayer.pauseSong(); // نوقف مؤقتًا
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                int newFrame = playbackSlider.getValue();
                musicPlayer.seekToFrame(newFrame); // نبدأ من الموضع الجديد
                enablePauseButtonDisablePlayButton();
            }
        });

        playbackSlider.setUI(new BasicSliderUI(playbackSlider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int trackY = trackRect.y + (trackRect.height / 2) - 3;
                int trackWidth = trackRect.width;

                g2.setColor(new Color(45, 45, 50));
                g2.fillRoundRect(trackRect.x, trackY, trackWidth, 6, 6, 6);

                float progress = (float) playbackSlider.getValue() / playbackSlider.getMaximum();
                int filledWidth = (int) (trackWidth * progress);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(100, 180, 255),
                        trackWidth, 0, new Color(60, 130, 250));
                g2.setPaint(gradient);
                g2.fillRoundRect(trackRect.x, trackY, filledWidth, 6, 6, 6);
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int thumbX = thumbRect.x + (thumbRect.width / 2) - 7;
                int thumbY = thumbRect.y + (thumbRect.height / 2) - 7;

                g2.setColor(new Color(100, 180, 255));
                g2.fillOval(thumbX, thumbY, 14, 14);

                g2.setColor(new Color(0, 0, 0, 100));
                g2.drawOval(thumbX, thumbY, 14, 14);
            }
        });

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

        playbackBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        playbackBtns.setBackground(COMPONENT_BACKGROUND);

        JButton previousBtn = createStyledButton(prevBtn);
        JButton playButton = createStyledButton(playBtn);
        JButton pauseButton = createStyledButton(pauseBtn);
        JButton nextButton = createStyledButton(nextBtn);

        playButton.addActionListener(e -> {
            enablePauseButtonDisablePlayButton();
            musicPlayer.playCurrentSong();
        });
        pauseButton.addActionListener(e -> {
            enablePlayButtonDisablePauseButton();
            musicPlayer.pauseSong();
        });
        pauseButton.setVisible(false);

        playbackBtns.add(previousBtn);
        playbackBtns.add(playButton);
        playbackBtns.add(pauseButton);
        playbackBtns.add(nextButton);

        controlPanel.add(playbackSlider);
        controlPanel.add(timePanel);
        controlPanel.add(playbackBtns);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String imagePath) {
        JButton button = new JButton(loadImage(imagePath));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setRolloverIcon(loadImage(imagePath, 1.1f));
        return button;
    }

    public void setPlaybackSliderValue(int frame) {
        playbackSlider.setValue(frame);
    }

    public void updateCurrentTimeLabel(int frame, double frameRatePerMs) {
        if (labelEnd != null && !labelEnd.getText().equals("00:00")) {
            int currentTimeInMs = (int) (frame / frameRatePerMs);
            int minutes = (currentTimeInMs / 1000) / 60;
            int seconds = (currentTimeInMs / 1000) % 60;
            labelCurrentTime.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
        if (song.getAlbumArt() != null) {
            Image scaled = song.getAlbumArt().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            songImage.setIcon(new ImageIcon(scaled));
        } else {
            songImage.setIcon(loadImage("src/assets/drive-download-20250713T123450Z-1-001/record.png"));
        }
    }

    void updatePlaybackSlider(Song song) {
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());
        labelEnd.setText(song.getSongLength());
    }

    private void enablePauseButtonDisablePlayButton() {
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

    private ImageIcon loadImage(String imagePath, float scaleFactor) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int newWidth = (int) (image.getWidth() * scaleFactor);
            int newHeight = (int) (image.getHeight() * scaleFactor);
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
        }
        return null;
    }

    private ImageIcon loadImage(String imagePath) {
        return loadImage(imagePath, 1.0f);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new MusicPlayerGUI().setVisible(true));
    }
}
