/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import edu.gmu.isa681.client.controller.Controller;

public final class GameOverPane extends Container {
  private static final long serialVersionUID = -5041515048805301891L;
  
  private JLabel titleLabel;
  private JLabel reasonLabel;
  
  private JLabel player1Label;
  private JLabel player1Score;
  
  private JLabel player2Label;
  private JLabel player2Score;
  
  private JLabel backButton;
  
  public GameOverPane(final Controller controller, final String reason, final String player1, final String player2, final String victor) {
    
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    
    titleLabel = new JLabel("Game Over!");
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Helvatica", Font.BOLD, 24));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 2;
    constraints.anchor = GridBagConstraints.CENTER;
    add(titleLabel, constraints);
    
    reasonLabel = new JLabel(reason);
    reasonLabel.putClientProperty("html.disable", Boolean.TRUE);
    reasonLabel.setForeground(Color.WHITE);
    reasonLabel.setFont(new Font("Helvatica", Font.BOLD, 14));
    reasonLabel.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 2;
    constraints.ipadx = 0;
    constraints.ipady = 50;
    constraints.anchor = GridBagConstraints.CENTER;
    add(reasonLabel, constraints);
    
    player1Label = new JLabel(player1);
    player1Label.putClientProperty("html.disable", Boolean.TRUE);
    player1Label.setForeground(Color.WHITE);
    player1Label.setFont(new Font("Helvatica", Font.BOLD, 14));
    player1Label.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    constraints.ipadx = 0;
    constraints.ipady = 50;
    constraints.anchor = GridBagConstraints.CENTER;
    add(player1Label, constraints);
    
    player1Score = new JLabel(player1.equals(victor) ? "1" : "0");
    player1Score.setForeground(Color.WHITE);
    player1Score.setFont(new Font("Helvatica", Font.BOLD, 48));
    player1Score.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 0;
    constraints.gridy = 3;
    constraints.gridwidth = 1;
    constraints.ipadx = 50;
    constraints.ipady = 50;
    constraints.anchor = GridBagConstraints.CENTER;
    add(player1Score, constraints);
    
    player2Label = new JLabel(player2);
    player2Label.putClientProperty("html.disable", Boolean.TRUE);
    player2Label.setForeground(Color.WHITE);
    player2Label.setFont(new Font("Helvatica", Font.BOLD, 14));
    player2Label.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 1;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    constraints.ipadx = 0;
    constraints.ipady = 50;
    constraints.anchor = GridBagConstraints.CENTER;
    add(player2Label, constraints);
    
    player2Score = new JLabel(player2.equals(victor) ? "1" : "0");
    player2Score.setForeground(Color.WHITE);
    player2Score.setFont(new Font("Helvatica", Font.BOLD, 48));
    player2Score.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 1;
    constraints.gridy = 3;
    constraints.gridwidth = 1;
    constraints.ipadx = 50;
    constraints.ipady = 50;
    constraints.anchor = GridBagConstraints.CENTER;
    add(player2Score, constraints);
    
    
    backButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>Back</U></FONT></HTML>");
    backButton.setOpaque(false);
    backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 1;
    constraints.gridy = 4;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.insets.right = -15;
    constraints.ipadx = 0;
    constraints.ipady = 5;
    constraints.anchor = GridBagConstraints.EAST;
    add(backButton, constraints);
    
    backButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (backButton.isEnabled()) {
          controller.goBack(GameOverPane.class);
        }
      }
    });
  }
}
