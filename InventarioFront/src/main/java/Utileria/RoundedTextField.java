/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utileria;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author tacot
 */
public class RoundedTextField extends JTextField {

    private final int arcWidth = 10;
    private final int arcHeight = 10;

    private java.awt.Color borderColor = new Color(176, 176, 176);
    private java.awt.Color focusBorderColor = new Color(0,0,255);

    public RoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 10, 8, 10));

        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                repaint();
            }
        });
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

        if (hasFocus()) {
            g2.setColor(focusBorderColor);
        } else {
            g2.setColor(borderColor);
        }

        g2.setStroke(new java.awt.BasicStroke(1f));

        g2.draw(new RoundRectangle2D.Float(
                0, 0,
                getWidth() - 1,
                getHeight() - 1,
                arcWidth,
                arcHeight
        ));

        g2.dispose();
    }

    public void setBorderColor(java.awt.Color color) {
        this.borderColor = color;
        repaint();
    }

    public void setFocusBorderColor(java.awt.Color color) {
        this.focusBorderColor = color;
        repaint();
    }

}
