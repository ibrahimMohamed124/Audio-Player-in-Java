import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistUI extends JFrame {

    public static final Color FRAME_BACKGROUND = new Color(20, 20, 25);
    public static final Color COMPONENT_BACKGROUND = new Color(30, 30, 35);
    public static final Color TEXT_COLOR = new Color(220, 220, 220);
    public static final Color ACCENT_COLOR = new Color(100, 180, 255);

    private final JFileChooser fileChooser;
    private final DefaultListModel<String> playlistModel;
    private final JList<String> playlistList;
    private final JButton btnCreatePlaylist, btnLoadPlaylist, btnSavePlaylist;

    private List<String> currentPlaylist = new ArrayList<>();

    public PlaylistUI() {
        super("🎶 Manage Playlist");

        setSize(400, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(FRAME_BACKGROUND);
        setLayout(new BorderLayout(15, 15));

        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));
        fileChooser.setCurrentDirectory(new File("src/assets"));

        // Playlist list view
        playlistModel = new DefaultListModel<>();
        playlistList = new JList<>(playlistModel);
        playlistList.setBackground(COMPONENT_BACKGROUND);
        playlistList.setForeground(TEXT_COLOR);
        playlistList.setFont(new Font("Dialog", Font.PLAIN, 16));
        playlistList.setSelectionBackground(ACCENT_COLOR);
        playlistList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(playlistList), BorderLayout.CENTER);

        // Bottom buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(FRAME_BACKGROUND);

        btnCreatePlaylist = createStyledButton("Create");
        btnLoadPlaylist = createStyledButton("Load");
        btnSavePlaylist = createStyledButton("Save");

        buttonPanel.add(btnCreatePlaylist);
        buttonPanel.add(btnLoadPlaylist);
        buttonPanel.add(btnSavePlaylist);

        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        setupButtonActions();
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Dialog", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_COLOR.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_COLOR);
            }
        });
        return btn;
    }

    private void setupButtonActions() {
        // Create new playlist
        btnCreatePlaylist.addActionListener((ActionEvent e) -> {
            int result = fileChooser.showOpenDialog(PlaylistUI.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                currentPlaylist.clear();
                playlistModel.clear();
                for (File file : fileChooser.getSelectedFiles()) {
                    currentPlaylist.add(file.getAbsolutePath());
                    playlistModel.addElement(file.getName());
                }
                JOptionPane.showMessageDialog(this, "✅ Playlist created successfully!");
            }
        });

        // Save playlist to file
        btnSavePlaylist.addActionListener(e -> {
            if (currentPlaylist.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ No playlist to save!");
                return;
            }

            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setDialogTitle("Save Playlist");
            saveChooser.setFileFilter(new FileNameExtensionFilter("Playlist Files", "m3u", "txt"));

            int userSelection = saveChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File saveFile = saveChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
                    for (String path : currentPlaylist) {
                        writer.write(path);
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(this, "💾 Playlist saved successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "❌ Error saving playlist: " + ex.getMessage());
                }
            }
        });

        // Load existing playlist from file
        btnLoadPlaylist.addActionListener(e -> {
            JFileChooser loadChooser = new JFileChooser();
            loadChooser.setDialogTitle("Load Playlist");
            loadChooser.setFileFilter(new FileNameExtensionFilter("Playlist Files", "m3u", "txt"));

            int result = loadChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = loadChooser.getSelectedFile();
                currentPlaylist.clear();
                playlistModel.clear();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        File songFile = new File(line);
                        if (songFile.exists()) {
                            currentPlaylist.add(line);
                            playlistModel.addElement(songFile.getName());
                        }
                    }
                    JOptionPane.showMessageDialog(this, "🎵 Playlist loaded successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "❌ Error loading playlist: " + ex.getMessage());
                }
            }
        });
    }

    public List<String> getCurrentPlaylist() {
        return currentPlaylist;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new PlaylistUI().setVisible(true));
    }
}
