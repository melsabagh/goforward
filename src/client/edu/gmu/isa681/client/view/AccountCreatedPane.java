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

public final class AccountCreatedPane extends Container {
  private static final long serialVersionUID = -5041515048805301891L;
  
  private JLabel titleLabel;
  private JLabel usernameLabel;
  private JLabel usernameField;
  
  private JLabel backButton;
  
  public AccountCreatedPane(final Controller controller, final String username) {
    
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    
    titleLabel = new JLabel("Account created succesfully");
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Helvatica", Font.BOLD, 24));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 2;
    constraints.anchor = GridBagConstraints.WEST;
    add(titleLabel, constraints);
    
    usernameLabel = new JLabel("username:");
    usernameLabel.setForeground(Color.WHITE);
    usernameLabel.setFont(new Font("Helvatica", Font.BOLD, 14));
    usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.ipadx = 0;
    constraints.ipady = 50;
    constraints.anchor = GridBagConstraints.WEST;
    add(usernameLabel, constraints);
    
    usernameField = new JLabel(username);
    usernameField.putClientProperty("html.disable", Boolean.TRUE);
    usernameField.setForeground(Color.WHITE);
    usernameField.setFont(new Font("Helvatica", Font.BOLD, 14));
    usernameField.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 1;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.ipadx = 50;
    constraints.ipady = 50;
    constraints.anchor = GridBagConstraints.WEST;
    add(usernameField, constraints);
    
    backButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>Back</U></FONT></HTML>");
    backButton.setOpaque(false);
    backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 1;
    constraints.gridy = 3;
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
          controller.goBack(AccountCreatedPane.class);
        }
      }
    });
  }
}
