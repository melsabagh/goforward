/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.game;

import java.io.Serializable;

public final class Card implements Comparable<Card>, Serializable {
  private static final long serialVersionUID = 171999343179444956L;

  public enum Value {
    THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), 
    JACK("j"), QUEEN("q"), KING("k"), ACE("1"), TWO("2");
    
    private final String v;
    private Value(String v) {
      this.v = v;
    }
    
    public String getStringValue() {
      return v;
    }
  };
  
  public enum Suit {
    SPADE("s"), CLUB("c"), DIAMOND("d"), HEART("h");
    
    private final String v;
    private Suit(String v) {
      this.v = v;
    }
    
    public String getStringValue() {
      return v;
    }
  };
  
  public enum Type {
    PLAY, PASS, FORFEIT
  }
  
  public enum SpecialType {
    PASS, FORFEIT
  }
  
  private Type type;
  private Suit suit;
  private Value value;

  private Card(Type type) {
    this.type = type;
  }
  
  private Card(Type type, Suit suit, Value value) {
    this.type = type;
    this.suit = suit;
    this.value = value;
  }
  
  public static Card createPlayCard(Value value, Suit suit) {
    return new Card(Type.PLAY, suit, value);
  }

  public static Card createSpecialCard(SpecialType specialType) {
    if (specialType == SpecialType.PASS) {
      return new Card(Type.PASS);
      
    } else if (specialType == SpecialType.FORFEIT) {
      return new Card(Type.FORFEIT);
      
    } else {
      throw new RuntimeException("Should not happen!");
    }
  }
  
  public Type getType() {
    return type;
  }
  
  public Suit getSuit() {
    return suit;
  }

  public Value getValue() {
    return value;
  }
  
  public String toString() {
    if (type == Type.PLAY) {
      return value.toString() +" OF "+ suit.toString();
    } else {
      return type.toString();
    }
  }
  
  public int compareTo(Card c) {
    int c0 = this.type.compareTo(c.type);
    
    if (c0 == 0) {
      int c1 = this.value.compareTo(c.value);
      int c2 = this.suit.compareTo(c.suit);
      
      if (c1 == 0) {
        return c2;
      } else {
        return c1;
      }
    } else {
      return c0;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((suit == null) ? 0 : suit.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    
    Card other = (Card) obj;
    if (type != other.type) return false;
    if (suit != other.suit) return false;
    if (value != other.value) return false;
    return true;
  }

}
