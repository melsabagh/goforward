/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.gmu.isa681.client.controller.Controller;
import edu.gmu.isa681.client.model.Client;
import edu.gmu.isa681.util.Constants;

public final class Main {
  private static Log log = LogFactory.getLog(Main.class);

  public static void main(String[] args) {
    log.info("Setting look and feel...");

    try {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
      
    } catch (Exception ex1) {
      log.warn(ex1.getMessage(), ex1);
      log.warn("Nimbus is not available.");
      log.warn("Switching to system look and feel");
      log.warn("Some GUI discrepancies may occur!");
      
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ex2) {
        log.error(ex2.getMessage(), ex2);
        log.error("Could not setup a look and feel.");
        System.exit(1);
      }
    }
    
    log.info("Initializing GUI...");
    
    final JFrame frame = new JFrame();
    frame.setTitle("GoForward");
    frame.setBackground(new Color(0, 100, 0));
    UIManager.put("nimbusBase", new Color(0, 100, 0));
    //UIManager.put("nimbusBlueGrey", new Color(0, 100, 0));
    UIManager.put("control", new Color(0, 100, 0));

    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        frame.setPreferredSize(frame.getSize());
      }
    });
    
    Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    if (dim.width < 1366) {
      frame.setPreferredSize(new Dimension(800, 600));
    } else {
      frame.setPreferredSize(new Dimension(1200, 700));
    }
    
    //frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.pack();
    
    Client client = new Client("localhost", Constants.SERVER_PORT);
    Controller controller = new Controller(client, frame);
    controller.applicationStarted();
    
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    
    log.info("Started");
  }
}
