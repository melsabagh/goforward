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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.gmu.isa681.client.controller.Controller;

public final class LoginPane extends Container {
  private static final long serialVersionUID = -5041515048805301891L;
  
  private JLabel usernameLabel;
  private JTextField usernameField;
  
  private JLabel passwordLabel;
  private JPasswordField passwordField;
  
  private JButton loginButton;
  private JLabel registerButton;
  
  private JLabel statusLabel;
  
  public LoginPane(final Controller controller) {
    
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    
    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridBagLayout());
    panel1.setOpaque(false);
    
    usernameLabel = new JLabel("Username:");
    usernameLabel.setForeground(Color.WHITE);
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.insets.top = 0;
    constraints.insets.right = 0; 
    constraints.ipady = 0;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(usernameLabel, constraints);
    
    usernameField = new JTextField(16);
    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.gridwidth = 3;
    constraints.insets.top = 0;
    constraints.insets.right = 0; 
    constraints.ipady = 0;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(usernameField, constraints);
    
    
    passwordLabel = new JLabel("Password:");
    passwordLabel.setForeground(Color.WHITE);
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.insets.top = 0;
    constraints.insets.right = 0; 
    constraints.ipady = 0;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(passwordLabel, constraints);
    
    passwordField = new JPasswordField(16);
    constraints.gridx = 1;
    constraints.gridy = 1;
    constraints.gridwidth = 3;
    constraints.insets.top = 0;
    constraints.insets.right = 0; 
    constraints.ipady = 0;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(passwordField, constraints);
    
    
    loginButton = new JButton("Login");
    constraints.gridx = 3;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.insets.right = 0; 
    constraints.ipady = 5;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(loginButton, constraints);
    
    loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (loginButton.isEnabled()) {
          statusLabel.setText("");
          controller.login(usernameField.getText(), new String(passwordField.getPassword()));
          loginButton.setEnabled(false);
          usernameField.setEnabled(false);
          passwordField.setEnabled(false);
        }
      }
    });
    
    registerButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>Create an account</U></FONT></HTML>");
    registerButton.setOpaque(false);
    registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 3;
    constraints.gridy = 3;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.insets.right = 0; 
    constraints.ipady = 5;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(registerButton, constraints);
    
    registerButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (registerButton.isEnabled()) {
          controller.register();
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
  
  public void setUsername(final String username) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        usernameField.setText(username);
        loginButton.setEnabled(true);
        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
      }
    });
  }
  
  public void setPassword(final String password) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        passwordField.setText(password);
        loginButton.setEnabled(true);
        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
      }
    });
  }
  
  public void setStatusMessage(final String text) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        statusLabel.setText(text);
        loginButton.setEnabled(true);
        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
      }
    });
  }
}
