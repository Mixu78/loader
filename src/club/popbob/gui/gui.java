package club.popbob.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import club.popbob.Cheat;
import club.popbob.Main;
import club.popbob.web.Reader;
import club.popbob.load.Loader;

public class gui {
    private JFrame openGui;

    private void SetupMenuBar(JFrame frame) {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenuItem configGui = new JMenuItem("Open config window");
        configGui.setMnemonic(KeyEvent.VK_C);
        configGui.addActionListener(e -> {
            if (openGui != null) openGui.dispose();
            openGui = new ConfigGui(frame.getTitle());
        });
        JMenuItem configFile = new JMenuItem("Open config file");
        configFile.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(Main.config.configFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        file.add(configGui);
        file.add(configFile);

        bar.add(file);

        frame.setJMenuBar(bar);
    }

    public gui() throws IOException {
        final Color transparent = new Color(1, 1, 1, 0.5f);

        String[] cheats = Reader.getCheats().toArray(new String[0]);
        JFrame frame = new JFrame("C4E | " + Reader.getMotd());
        SetupMenuBar(frame);
        BufferedImage rat = null;
        try {
            rat = ImageIO.read(getClass().getResource("/resources/bigrat.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        Image finalRat = rat != null ? rat.getScaledInstance(400, 200, Image.SCALE_SMOOTH) : null;
        frame.setContentPane(new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalRat != null && Main.config.getConfig().bigrat) {
                    g.drawImage(finalRat.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0,0, null);
                } else {
                    g.clearRect(getX(), getY(), getWidth(), getHeight());
                }
            }
        });
        JComboBox<String> cheatList = new JComboBox<>(cheats) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBackground().getAlpha() < 255) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };
        Cheat selected = Reader.getCheatData(Objects.requireNonNull(cheatList.getSelectedItem()).toString());
        JLabel mcver = new JLabel(" MC Version: " + selected.mcversion),
                updated = new JLabel(" Last Update: " + selected.updated);
        JButton inject = new JButton("Load") {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBackground().getAlpha() < 255) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g); //Gotta be in this order so the text renders not behind
            }
        };
        inject.addActionListener(e -> {
            try {
                new Loader(Reader.getCheatData(Objects.requireNonNull(cheatList.getSelectedItem()).toString()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        mcver.setBackground(transparent);
        updated.setBackground(transparent);
        cheatList.setBackground(transparent);
        inject.setBackground(transparent);
        mcver.setOpaque(true);
        updated.setOpaque(true);
        cheatList.setOpaque(false);
        inject.setOpaque(false);

        mcver.setBorder(BorderFactory.createEmptyBorder());
        updated.setBorder(BorderFactory.createEmptyBorder());

        inject.setContentAreaFilled(false);


        cheatList.addActionListener(e -> {
            Cheat selected1 = Reader.getCheatData(Objects.requireNonNull(cheatList.getSelectedItem()).toString());
            mcver.setText(" MC Version: " + selected1.mcversion);
            updated.setText(" Last Update: " + selected1.updated);
        });

        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResource("/resources/cfe.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.getContentPane().add(cheatList);
        frame.getContentPane().add(mcver);
        frame.getContentPane().add(updated);
        frame.getContentPane().add(inject);

        frame.getContentPane().setLayout(new GridLayout(4, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,200);

        if (image != null) frame.setIconImage(image);
        frame.setVisible(true);
    }
}
