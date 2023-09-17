/**
 *
 * SENG Iteration 3 P3-3 | GradientButton.java
 *
 * @author Brian Vo - 10188952
 * @author Jason Ngu - 30145770
 * @author Andy Tran - 30125341
 * @author Brett Lyle -30103268
 * @author Caio Araujo 30148726
 * @author Nishan Soni 30147280
 * @author Brian Tran - 30064686
 * @author Duong Tran - 30113765
 * @author Imran Haji - 30141571
 * @author Sahil Brar - 30021440
 * @author Eugene Lee - 30137489
 * @author Maira Khan - 30047942
 * @author Zainab Bari - 30154224
 * @author Eungyo Song - 30079379
 * @author Anthony Krim - 30142199
 * @author Michelle Loi - 30019557
 * @author Alisha Nasir - 30140704
 * @author Alina Mansuri - 30008370
 * @author Adam Mogensen - 30071819
 * @author Nemanja Grujic - 30116614
 * @author Ali Savab Pour - 30154744
 * @author Rheanna Fielder - 30092060
 * @author Justin Clibbett - 30128271
 * @author Aminreza Tavakoli - 30127594
 * @author Amasil Rahim Zihad - 30164830
 */


package com.autovend.software.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GradientButton extends JButton {
	
	
	Color startColour; 
	Color endColour; 
	
    public GradientButton(String text, Color startColour, Color endColour) {
        super(text);
        setContentAreaFilled(false);
        
        // Set a custom rounded border for the button
        setBorder(BorderFactory.createLineBorder(new Color(205, 219, 210)));
        
        this.startColour = startColour; 
        this.endColour = endColour; 
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Create a rounded rectangle shape that matches the button size
        Shape shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20);
        g2.setClip(shape);
        
        // Draw the gradient background inside the rounded rectangle
        g2.setPaint(new GradientPaint(
                new Point(0, 0),
                startColour,
                new Point(0, getHeight()),
                endColour));
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        g2.dispose();
        super.paintComponent(g);
    }
}