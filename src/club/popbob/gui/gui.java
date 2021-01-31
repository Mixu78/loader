package club.popbob.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

import club.popbob.Cheat;
import club.popbob.web.Reader;
import club.popbob.load.Loader;

public class gui {
    public gui() throws IOException {
        String[] cheats = Reader.getCheats().toArray(new String[0]);
        JFrame frame = new JFrame("C4E | " + Reader.getMotd());
        JComboBox<String> cheatList = new JComboBox<>(cheats);
        Cheat selected = Reader.getCheatData(Objects.requireNonNull(cheatList.getSelectedItem()).toString());
        JLabel mcver = new JLabel(" MC Version: " + selected.mcversion),
                updated = new JLabel(" Last Update: " + selected.updated);

        cheatList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cheat selected = Reader.getCheatData(Objects.requireNonNull(cheatList.getSelectedItem()).toString());
                mcver.setText(" MC Version: " + selected.mcversion);
                updated.setText(" Last Update: " + selected.updated);
            }
        });
        frame.getContentPane().add(cheatList);

        frame.getContentPane().add(mcver);
        frame.getContentPane().add(updated);
        JButton inject = new JButton("Load");
        inject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new Loader(Reader.getCheatData(Objects.requireNonNull(cheatList.getSelectedItem()).toString()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        frame.getContentPane().add(inject);

        frame.getContentPane().setLayout(new GridLayout(4, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,200);
        frame.setVisible(true);
    }
}
