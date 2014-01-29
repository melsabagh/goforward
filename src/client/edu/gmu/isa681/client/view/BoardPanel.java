/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.view;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

public final class BoardPanel extends JPanel {
  private static final long serialVersionUID = -884792211736236635L;

  public BoardPanel() {
    setLayout(null);
    
    
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        repositionCards();
      }
    });
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    Graphics2D g2d = (Graphics2D) g.create();
    String s = getName();
    char[] sc = s.toCharArray();
    int w = getFontMetrics(getFont()).charsWidth(sc, 0, sc.length);
    g2d.drawString(s, (getWidth() - w) /2, getHeight() /2);
    g2d.dispose();
  }
  
  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    super.addImpl(comp, constraints, 0);
    adjustLocation(comp);
  }
  
  private void repositionCards() {
    synchronized (getTreeLock()) {
      Component[] components = getComponents();
      for (Component comp : components) {
        adjustLocation(comp);
      }
    }
  }
  
  private void adjustLocation(Component comp) {
    Dimension dim = comp.getPreferredSize();
    comp.setBounds(0, 0, dim.width, dim.height);
    
    int x = 0;
    int y = 0;
    
    if (comp instanceof CardLabel) {
      int offsetX = ((CardLabel)comp).getOffsetX();
      int offsetY = ((CardLabel)comp).getOffsetY();
      
      x = (getWidth() - comp.getWidth()) / 2 + offsetX;
      y = (getHeight() - comp.getHeight()) / 2 + offsetY;
    }
    
    comp.setLocation(x, y);
  }
}
