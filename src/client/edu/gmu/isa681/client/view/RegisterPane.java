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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.gmu.isa681.client.controller.Controller;

public final class RegisterPane extends Container {
  private static final long serialVersionUID = -5041515048805301891L;
  
  private JLabel titleLabel;
  
  private JLabel usernameLabel;
  private JTextField usernameField;
  
  private JLabel emailLabel;
  private JTextField emailField;
  
  private JLabel passwordLabel;
  private JPasswordField passwordField;
  
  private JButton registerButton;
  private JLabel backButton;
  
  //private JLabel statusLabel;
  private JTextArea statusLabel;
  
  public RegisterPane(final Controller controller) {
    
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    
    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridBagLayout());
    panel1.setOpaque(false);
    
    titleLabel = new JLabel("Create a new account");
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Helvatica", Font.BOLD, 14));
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 3;
    panel1.add(titleLabel, constraints);
    
    
    usernameLabel = new JLabel("Username:");
    usernameLabel.setForeground(Color.WHITE);
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.insets.top = 30;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(usernameLabel, constraints);
    
    usernameField = new JTextField(16);
    constraints.gridx = 1;
    constraints.gridy = 1;
    constraints.gridwidth = 3;
    constraints.insets.top = 30;
    constraints.anchor = GridBagConstraints.WEST;
    panel1.add(usernameField, constraints);
    
    emailLabel = new JLabel("Email:");
    emailLabel.setForeground(Color.WHITE);
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    constraints.insets.top = 0;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(emailLabel, constraints);
    
    emailField = new JTextField(16);
    constraints.gridx = 1;
    constraints.gridy = 2;
    constraints.gridwidth = 3;
    constraints.insets.top = 0;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(emailField, constraints);
    
    passwordLabel = new JLabel("Password:");
    passwordLabel.setForeground(Color.WHITE);
    constraints.gridx = 0;
    constraints.gridy = 3;
    constraints.gridwidth = 1;
    constraints.insets.top = 0;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(passwordLabel, constraints);
    
    passwordField = new JPasswordField(16);
    constraints.gridx = 1;
    constraints.gridy = 3;
    constraints.gridwidth = 3;
    constraints.insets.top = 0;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(passwordField, constraints);
    
    
    registerButton = new JButton("Register");
    constraints.gridx = 3;
    constraints.gridy = 4;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.ipady = 5;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(registerButton, constraints);
    
    registerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (registerButton.isEnabled()) {
          statusLabel.setText("");
          controller.register(usernameField.getText(), emailField.getText(), new String(passwordField.getPassword()));
          registerButton.setEnabled(false);
        }
      }
    });
    
    backButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>Back</U></FONT></HTML>");
    backButton.setOpaque(false);
    backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 3;
    constraints.gridy = 5;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.insets.right = 0;
    constraints.ipady = 5;
    constraints.anchor = GridBagConstraints.EAST;
    panel1.add(backButton, constraints);
    
    backButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (backButton.isEnabled()) {
          controller.goBack(RegisterPane.class);
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
    
    statusLabel = new JTextArea();
    statusLabel.putClientProperty("html.disable", Boolean.TRUE);
    statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
    statusLabel.setBackground(new Color(0,0,0,0));
    statusLabel.setForeground(Color.PINK);
    statusLabel.setOpaque(false);
    statusLabel.setBorder(null);
    statusLabel.setEditable(false);
    //statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

    panel2.add(statusLabel, BorderLayout.CENTER);
    
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.insets.right = 0; 
    constraints.ipady = 5;
    constraints.anchor = GridBagConstraints.WEST;
    add(panel2, constraints);
  }
  
  public void setStatusMessage(final String text) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        statusLabel.setText(text);
        registerButton.setEnabled(true);
      }
    });
  }
}
