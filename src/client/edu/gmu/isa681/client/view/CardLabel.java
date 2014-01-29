/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import edu.gmu.isa681.game.Card;


public final class CardLabel extends JLabel {
  private static final long serialVersionUID = -8768815644555937321L;
  private static Random rand = new Random(System.nanoTime());
  
  private Card card;
  
  private int offsetX;
  private int offsetY;
  
  private final static HashMap<Card, BufferedImage> largeImages;
  private final static HashMap<Card, BufferedImage> smallImages;
  static {
    largeImages = new HashMap<Card, BufferedImage>();
    smallImages = new HashMap<Card, BufferedImage>();
    
    try {
      for (Card.Value v : Card.Value.values()) {
        for (Card.Suit s : Card.Suit.values()) {
          Card c = Card.createPlayCard(v, s);
          String imageName = v.getStringValue() + s.getStringValue() + ".png";
          
          BufferedImage large = ImageIO.read(CardLabel.class.getResource("img1440/"+ imageName));
          largeImages.put(c, large);
          
          BufferedImage small = ImageIO.read(CardLabel.class.getResource("img800/"+ imageName));
          smallImages.put(c, small);
        }
      }
      
      largeImages.put(null, ImageIO.read(CardLabel.class.getResource("img1440/back.png")));
      smallImages.put(null, ImageIO.read(CardLabel.class.getResource("img800/back.png")));
      
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private static BufferedImage getLargeImage(Card c) {
    return largeImages.get(c);
  }
  
  private static BufferedImage getSmallImage(Card c) {
    return smallImages.get(c);
  }
  
  public CardLabel(Card card) {
    this.card = card;
    BufferedImage img = getLargeImage(card);
    if (img != null) {
      setIcon(new ImageIcon(img));
      Dimension dim = new Dimension(img.getWidth(), img.getHeight());
      setPreferredSize(dim);
    }
    
    generateOffsets();
    putClientProperty("html.disable", Boolean.TRUE);
  }
  
  private Dimension frameDimension = new Dimension(0,0);
  @Override
  public void paint(Graphics g) {
    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
    Dimension dim = topFrame.getSize();
    if (dim.width != frameDimension.width || dim.height != frameDimension.height) {
      frameDimension = dim;
      
      BufferedImage img;
      if (dim.width < 1200) {
        img = getSmallImage(getCard()); 
      } else {
        img = getLargeImage(getCard());
      }
      
      setIcon(new ImageIcon(img));
      setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
    }
    
    super.paint(g);
  }
  
  private void generateOffsets() {
    offsetX = (int)(rand.nextGaussian() * 15);
    offsetY = (int)(rand.nextGaussian() * 15);
  }
  
  public void setCard(Card card) {
    this.card = card;
  }
  
  public Card getCard() {
    return card;
  }
  
  public void setOffsetX(int offsetX) {
    this.offsetX = offsetX;
  }
  
  public int getOffsetX() {
    return offsetX;
  }
  
  public void setOffsetY(int offsetY) {
    this.offsetY = offsetY;
  }
  
  public int getOffsetY() {
    return offsetY;
  }
  
}