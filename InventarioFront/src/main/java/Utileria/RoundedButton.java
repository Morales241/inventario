/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utileria;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author tacot
 */
public class RoundedButton extends JButton{
    private int arc = 0;

    public RoundedButton(String text, int arcp) {
        super(text);
        setBorder(new EmptyBorder(10, 20, 10, 20));
        arc = arcp;
        
        setContentAreaFilled(false); 
        
        Dimension dimension = new Dimension(240, 60);
        
        this.setMaximumSize(dimension);
        this.setMinimumSize(dimension);
        
        
        this.setBackground(new Color(234,244,251));
        
        this.setHorizontalAlignment(CENTER);
//        this.setHorizontalTextPosition(LEFT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Antialiasing para suavizar bordes
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo redondeado
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));

        // Texto
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground().darker());
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
        g2.dispose();
    }
}
