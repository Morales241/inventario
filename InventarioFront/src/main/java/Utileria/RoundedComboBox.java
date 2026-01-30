package Utileria;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class RoundedComboBox extends JComboBox<String> {

    public RoundedComboBox(int borde) {
        super(); // sin ítems por ahora
        setBorder(new RoundedBorder(borde)); // radio de redondeo
//        setBackground(Color.WHITE);
//        setForeground(Color.BLACK);
    }

    // Clase interna para el borde redondeado
    static class RoundedBorder implements Border {

        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
