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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.gmu.isa681.client.controller.Controller;

public final class WelcomePane extends Container {
  private static final long serialVersionUID = -5041515048805301891L;
  
  private JLabel usernameLabel;
  
  private JLabel startgameButton;
  private JLabel leaderboardButton;
  private JLabel historyButton;
  private JLabel auditButton;
  
  private JLabel logoutButton;
  
  private JLabel statusLabel;
  
  public WelcomePane(final Controller controller, final String username) {
    
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    
    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridBagLayout());
    panel1.setOpaque(false);
    
    usernameLabel = new JLabel("Logged in as: "+ username);
    usernameLabel.setForeground(Color.WHITE);
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    //constraints.insets.right = -50;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(usernameLabel, constraints);
    constraints.insets.right = 0;
    
    startgameButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>Start Game</U></FONT></HTML>");
    startgameButton.setOpaque(false);
    startgameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.insets.top = 20; 
    constraints.anchor = GridBagConstraints.WEST;
    panel1.add(startgameButton, constraints);
    
    startgameButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (startgameButton.isEnabled()) {
          controller.startGame();
          statusLabel.setText("Waiting for more players...");
          startgameButton.setEnabled(false);
        }
      }
    });
    
    leaderboardButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>View Leaderboard</U></FONT></HTML>");
    leaderboardButton.setOpaque(false);
    leaderboardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    constraints.insets.top = 20; 
    constraints.anchor = GridBagConstraints.WEST;
    panel1.add(leaderboardButton, constraints);
    
    leaderboardButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (leaderboardButton.isEnabled()) {
          controller.viewLeaderboard();
        }
      }
    });
    
    historyButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>View History</U></FONT></HTML>");
    historyButton.setOpaque(false);
    historyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 0;
    constraints.gridy = 3;
    constraints.gridwidth = 1;
    constraints.insets.top = 20; 
    constraints.anchor = GridBagConstraints.WEST;
    panel1.add(historyButton, constraints);
    
    
    historyButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (historyButton.isEnabled()) {
          controller.viewUserHistory(true);
        }
      }
    });
    
    auditButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>View games audit</U></FONT></HTML>");
    auditButton.setOpaque(false);
    auditButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 0;
    constraints.gridy = 4;
    constraints.gridwidth = 1;
    constraints.insets.top = 20; 
    constraints.anchor = GridBagConstraints.WEST;
    panel1.add(auditButton, constraints);
    
    
    auditButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (auditButton.isEnabled()) {
          controller.viewUserHistory(false);
        }
      }
    });
    
    logoutButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>Logout</U> </FONT></HTML>");
    logoutButton.setOpaque(false);
    logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 0;
    constraints.gridy = 5;
    constraints.gridwidth = 1;
    constraints.insets.top = 20; 
    constraints.anchor = GridBagConstraints.WEST;
    panel1.add(logoutButton, constraints);
    
    logoutButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (logoutButton.isEnabled()) {
          controller.logout(WelcomePane.class);
        }
      }
    });
    
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.insets.top = 0;
    constraints.insets.right = 0;
    constraints.ipady = 0;
    constraints.anchor = GridBagConstraints.CENTER;
    add(panel1, constraints);
    
    
    JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout());
    panel2.setOpaque(false);
    
    statusLabel = new JLabel("");
    statusLabel.putClientProperty("html.disable", Boolean.TRUE);
    statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
    statusLabel.setForeground(Color.PINK);
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    panel2.add(statusLabel, BorderLayout.CENTER);
    
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.insets.top = 30;
    constraints.insets.right = 0; 
    constraints.ipady = 5;
    constraints.anchor = GridBagConstraints.CENTER;
    add(panel2, constraints);
    
  }
  
  public void setStatusMessage(final String text) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        statusLabel.setText(text);
        startgameButton.setEnabled(true);
      }
    });
  }
}
