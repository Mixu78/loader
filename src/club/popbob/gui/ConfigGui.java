package club.popbob.gui;

import club.popbob.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ConfigGui extends JFrame {
    private Image icon;

    public ConfigGui(String baseTitle) {
        super(baseTitle + " - Config");
        try {
            icon = ImageIO.read(getClass().getResource("/resources/cfe.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JCheckBox bigrat = new JCheckBox("Bigrat");
        bigrat.setSelected(Main.config.getConfig().bigrat);

        bigrat.addActionListener(e -> Main.config.getConfig().bigrat = bigrat.isSelected());

        add(bigrat);
        setSize(400, 100);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
