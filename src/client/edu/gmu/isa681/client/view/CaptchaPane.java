/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.gmu.isa681.client.controller.Controller;

public final class CaptchaPane extends Container {
  private static final long serialVersionUID = -5041515048805301891L;
  
  private JLabel captchaLabel;
  private JTextField solutionField;
  
  private JButton submitButton;
  
  private JLabel statusLabel;
  
  public CaptchaPane(final Controller controller) {
    
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    
    captchaLabel = new JLabel();
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.insets.top = 0;
    constraints.insets.right = 0; 
    constraints.ipady = 0;
    constraints.anchor = GridBagConstraints.CENTER;
    add(captchaLabel, constraints);
    
    solutionField = new JTextField(16);
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.insets.top = 10;
    constraints.insets.right = 0; 
    constraints.ipady = 0;
    constraints.anchor = GridBagConstraints.CENTER;
    add(solutionField, constraints);
    
    
    submitButton = new JButton("Submit");
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    constraints.insets.top = 10;
    constraints.insets.right = 0; 
    constraints.ipady = 0;
    constraints.anchor = GridBagConstraints.CENTER;
    add(submitButton, constraints);
    
    submitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (submitButton.isEnabled()) {
          statusLabel.setText("");
          submitButton.setEnabled(false);
          controller.submitCaptcha(solutionField.getText());
        }
      }
    });
    
    statusLabel = new JLabel("");
    statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
    statusLabel.setForeground(Color.PINK);
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    constraints.gridx = 0;
    constraints.gridy = 4;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.insets.right = 0; 
    constraints.ipady = 5;
    constraints.anchor = GridBagConstraints.CENTER;
    add(statusLabel, constraints);
  }
  
  public void setCaptcha(final BufferedImage image) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        captchaLabel.setIcon(new ImageIcon(image));
        solutionField.setText("");
      }
    });
  }
  
  public void setStatusMessage(final String text) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        statusLabel.setText(text);
        submitButton.setEnabled(true);
      }
    });
  }
}
