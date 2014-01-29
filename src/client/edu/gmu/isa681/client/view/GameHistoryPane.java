/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.gmu.isa681.client.controller.Controller;

public final class GameHistoryPane extends Container {
  private static final long serialVersionUID = -5041515048805301891L;
  
  private JLabel titleLabel;
  private JLabel instructionsLabel;
  private JList<String> historyList;
  private JLabel backButton;
  
  public GameHistoryPane(final Controller controller, final String username, final String date, final String[] history) {
    
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    
    titleLabel = new JLabel("Game History");
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Helvatica", Font.BOLD, 14));
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.WEST;
    add(titleLabel, constraints);
    
    instructionsLabel = new JLabel("Date played: "+ date);
    instructionsLabel.setForeground(Color.WHITE);
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.WEST;
    add(instructionsLabel, constraints);
    
    
    JPanel listContainer = new JPanel();
    
    listContainer.setLayout(new BorderLayout());
    listContainer.setPreferredSize(new Dimension(450, 450));
    historyList = new JList<String>(history);
    listContainer.add(new JScrollPane(historyList, 
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
    
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.anchor = GridBagConstraints.CENTER;
    add(listContainer, constraints);
    
    
    backButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>Back</U></FONT></HTML>");
    backButton.setOpaque(false);
    backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 0;
    constraints.gridy = 3;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.insets.right = -15;
    constraints.ipady = 5;
    constraints.anchor = GridBagConstraints.EAST;
    add(backButton, constraints);
    
    backButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (backButton.isEnabled()) {
          controller.goBack(GameHistoryPane.class);
        }
      }
    });
  }
}
