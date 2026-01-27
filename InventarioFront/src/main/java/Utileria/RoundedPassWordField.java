/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utileria;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author tacot
 */
public class RoundedPassWordField extends JPasswordField{
    private final int arcWidth = 20;
    private final int arcHeight = 20;

    public RoundedPassWordField(int columns) {
        super(columns);
        setOpaque(false); 
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Espaciado interno
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Antialiasing para bordes suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo
        g2.setColor(getBackground());
        Shape rounded = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
        g2.fill(rounded);

        // Clipping para que el texto no se salga de la forma redonda
        g2.setClip(rounded);

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground().darker());

        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        g2.dispose();
    }
}
