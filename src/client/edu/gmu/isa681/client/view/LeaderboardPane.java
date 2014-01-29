/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import edu.gmu.isa681.client.controller.Controller;

public final class LeaderboardPane extends Container {
  private static final long serialVersionUID = -5041515048805301891L;
  
  private JLabel titleLabel;
  private JTable leaderboardTable;
  private JLabel backButton;
  
  public LeaderboardPane(final Controller controller, final String username, final String[][] data, final String[] columnNames) {
    
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    
    titleLabel = new JLabel("Leaderboard");
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Helvatica", Font.BOLD, 14));
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.WEST;
    add(titleLabel, constraints);
    
    
    leaderboardTable = new JTable();
    leaderboardTable.setRowHeight(25);
    leaderboardTable.setRowSelectionAllowed(true);
    leaderboardTable.setModel(new DefaultTableModel(data, columnNames) {
      private static final long serialVersionUID = -288340391339523006L;
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    });
    
    class MyRenderer extends DefaultTableCellRenderer {
      private static final long serialVersionUID = 8908348083861674412L;

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, 
          boolean isSelected, boolean hasFocus, int row, int column) {
        
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.putClientProperty("html.disable", Boolean.TRUE);
        
        
        if (isSelected) {
          label.setBackground(leaderboardTable.getSelectionBackground());
          label.setForeground(leaderboardTable.getSelectionForeground());
          
        } else {
          if (username.equals(data[row][0])) {
            label.setBackground(Color.GREEN.brighter());
            label.setForeground(Color.BLACK);
          } else {
            label.setBackground(leaderboardTable.getBackground());
            label.setForeground(leaderboardTable.getForeground());
          }
        }
        
        label.setBorder(BorderFactory.createEmptyBorder());
        
        return label;
      }
    }
    
    leaderboardTable.setDefaultRenderer(Object.class, new MyRenderer());
    
    
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.insets.top = 20;
    constraints.anchor = GridBagConstraints.CENTER;
    add(new JScrollPane(leaderboardTable), constraints);
    
    
    backButton = new JLabel("<HTML><FONT color=\"#00ffff\"><U>Back</U></FONT></HTML>");
    backButton.setOpaque(false);
    backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    constraints.gridx = 0;
    constraints.gridy = 2;
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
          controller.goBack(LeaderboardPane.class);
        }
      }
    });
  }
}
