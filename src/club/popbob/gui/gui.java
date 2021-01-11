package club.popbob.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;
import club.popbob.web.Reader;
import club.popbob.load.Loader;

public class gui {
    public gui() {
        String[] cheats = Reader.getCheats().toArray(new String[0]);
        JFrame frame = new JFrame("C4E");

        JLabel mcver = new JLabel("Select a cheat");
        mcver.setBounds(0, 20, 200, 100);

        JLabel updated = new JLabel("Select a cheat");
        updated.setBounds(0, 40, 200, 100);

        JComboBox<String> cheatList = new JComboBox<>(cheats);
        cheatList.setPreferredSize(new Dimension(200, 200));
        cheatList.setBounds(0,0,385, 50);
        cheatList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mcver.setText("MC Version: " + Reader.getCheatData(Objects.requireNonNull(cheatList.getSelectedItem()).toString()).mcversion);
                updated.setText("Last Update: " + Reader.getCheatData(Objects.requireNonNull(cheatList.getSelectedItem()).toString()).updated);
            }
        });
        JButton inject = new JButton("Inject");
        inject.setPreferredSize(new Dimension(50, 50));
        inject.setBounds(140, 110, 100, 50);
        inject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new Loader(Objects.requireNonNull(cheatList.getSelectedItem()).toString());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,200);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.getContentPane().add(cheatList);
        frame.getContentPane().add(inject);
        frame.getContentPane().add(mcver);
        frame.getContentPane().add(updated);
    }
}
