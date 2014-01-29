/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.server.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.validator.routines.EmailValidator;

import edu.vt.middleware.password.AlphabeticalSequenceRule;
import edu.vt.middleware.password.CharacterCharacteristicsRule;
import edu.vt.middleware.password.DigitCharacterRule;
import edu.vt.middleware.password.LengthRule;
import edu.vt.middleware.password.LowercaseCharacterRule;
import edu.vt.middleware.password.NonAlphanumericCharacterRule;
import edu.vt.middleware.password.NumericalSequenceRule;
import edu.vt.middleware.password.Password;
import edu.vt.middleware.password.PasswordData;
import edu.vt.middleware.password.PasswordValidator;
import edu.vt.middleware.password.QwertySequenceRule;
import edu.vt.middleware.password.RepeatCharacterRegexRule;
import edu.vt.middleware.password.Rule;
import edu.vt.middleware.password.RuleResult;
import edu.vt.middleware.password.UppercaseCharacterRule;
import edu.vt.middleware.password.WhitespaceRule;

final class Validator {

  /**
   * Determines whether the given string contains only printable US-ASCII characters (ASCII 32 to 126).
   * @param str
   * @return <code>true</code> if and only if the given string is in printable US-ASCII, <code>false</code> otherwise. 
   */
  public static boolean isPrintableAscii(String str) {
    if (str == null) return false;
    
    char[] chars = str.toCharArray();
    for (char c : chars) {
      if (c < 32 || c > 126) {
        return false;
      }
    }
    
    return true;
  }
  
  /**
   * Determines if the given string is empty. Note: A <code>null</code> string or a string with only whitespaces are 
   * also considered empty.
   * @param str
   * @return <code>true</code> if the given string is empty, <code>false</code> otherwise.
   */
  public static boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }
  
  /**
   * Checks whether the given email address is valid, by 1) checking if the given address is empty and 2) using 
   * Apache Commons EmailValidator to check the validity of the address.
   * @param email
   * @return List of validation errors, if any. If the give email address is valid, returns an empty list.
   */
  public static List<String> validateEmail(final String email) {
    List<String> errors = new ArrayList<String>();
    
    if (isEmpty(email)) {
      errors.add("Email required");
    }
    
    if (!EmailValidator.getInstance().isValid(email)) {
      errors.add("Invalid email address");
    }
    
    return errors;
  }
  
  public final static int MIN_USERNAME_LENGTH = 5;
  public final static int MAX_USERNAME_LENGTH = 16;
  
  /**
   * Checks whether the given username is valid. A valid username must be nonempty, in printable US-ASCII, 
   * between {@link #MIN_USERNAME_LENGTH} and {@link #MAX_USERNAME_LENGTH} (inclusive), contains only letters
   * and numbers, with at least one letter. 
   * @param username
   * @return List of validation errors, if any. If the username is valid, returns an empty list.
   */
  public static List<String> validateUsername(final String username) {
    List<String> errors = new ArrayList<String>();
    
    if (isEmpty(username)) {
      errors.add("Username required.");
      
    } else {
    
      if (!isPrintableAscii(username)) {
        errors.add("Username must be in US-ASCII.");
        
      } else {
      
        if (username.length() < MIN_USERNAME_LENGTH) {
          errors.add("Username must be between"+ MIN_USERNAME_LENGTH +" and " + MAX_USERNAME_LENGTH +" characters.");
        }
      
        Password usernameData = new Password(username);
        if (usernameData.containsWhitespace() || usernameData.containsNonAlphanumeric()) {
          errors.add("Username can only include letters and numbers.");
        }
      
        if (!usernameData.containsAlphabetical()) {
          errors.add("Username must include at least one letter.");
        }
      }
    }
    
    return errors;
  }
  
  public final static int MIN_PASSWORD_LENGTH = 8;
  public final static int MAX_PASSWORD_LENGTH = 30;
  
  /**
   * Main password validation rules. Uses the VT-Password package. See {@link #validatePassword(String, String)} for 
   * a complete list of the rules being applied.
   * @see https://code.google.com/p/vt-middleware/wiki/vtpassword
   */
  private static PasswordValidator passwordStrengthValidator;
  static {
    LengthRule lengthRule = new LengthRule(8, 30);                              // 8 to 30 chars
    WhitespaceRule whitespaceRule = new WhitespaceRule();                       // no whitespaces

    CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
    charRule.setNumberOfCharacteristics(3);                                     // 3 of the following:
    charRule.getRules().add(new DigitCharacterRule(1));                         // 1 digit
    charRule.getRules().add(new NonAlphanumericCharacterRule(1));               // 1 symbol
    charRule.getRules().add(new UppercaseCharacterRule(1));                     // 1 uppercase
    charRule.getRules().add(new LowercaseCharacterRule(1));                     // 1 lowercase
    
    AlphabeticalSequenceRule alphaSeqRule = new AlphabeticalSequenceRule();     // no abc, efg, ...
    NumericalSequenceRule numSeqRule = new NumericalSequenceRule(3, false);     // no 123, 567, ...
    QwertySequenceRule qwertySeqRule = new QwertySequenceRule();                // no qwerty, asdf, vbn...

    RepeatCharacterRegexRule repeatRule = new RepeatCharacterRegexRule(4);      // no aaaa, bbbb, etc

    List<Rule> ruleList = new ArrayList<Rule>();
    ruleList.add(lengthRule);
    ruleList.add(whitespaceRule);
    ruleList.add(charRule);
    ruleList.add(alphaSeqRule);
    ruleList.add(numSeqRule);
    ruleList.add(qwertySeqRule);
    ruleList.add(repeatRule);

    passwordStrengthValidator = new PasswordValidator(ruleList);
  }
  
  /**
   * Checks whether the given password is valid. A valid password must:
   * 
   * <ol>
   * <li>Be between {@link #MIN_PASSWORD_LENGTH} and {@link #MAX_PASSWORD_LENGTH} characters (inclusive) in length.</li>
   * <li>Not contain the <code>username</code>, either in forward or reverse order.</li>
   * <li>Contain only printable US-ASCII characters</li>
   * <li>Contain no whitespaces.</li>
   * <li>Contain at least 3 of the following: 1 digit, 1 symbol, 1 uppercase letter, 1 lowercase letter.</li>
   * <li>Contain no alphabetical sequences, e.g., abc, efg, klm.</li>
   * <li>Contain no numerical sequences of more than 3 digits, e.g., 123, 678.</li>
   * <li>Contain no keyboard sequences, e.g., qwerty</li>
   * <li>Contain no repeated characters or length 4 or more, e.g., aaaa</li>
   * </ol>
   * 
   * @param username
   * @param password
   * @return List of validation errors, if any. If the password is valid, returns an empty list.
   */
  public static List<String> validatePassword(final String username, final String password) {
    List<String> errors = new ArrayList<String>();
    
    if (isEmpty(password)) {
      errors.add("Password required.");
      
    } else {
    
      if (!isPrintableAscii(password)) {
        errors.add("Password must be in US-ASCII.");
        
      } else {
      
        if (username != null) {
          if (password.toLowerCase(Locale.ENGLISH).contains(username.toLowerCase(Locale.ENGLISH)) || 
              new StringBuilder(password.toLowerCase(Locale.ENGLISH)).reverse().toString().contains(username.toLowerCase(Locale.ENGLISH))) {
            errors.add("Password cannot contain username.");
            
          } else {
            RuleResult strength = passwordStrengthValidator.validate(new PasswordData(new Password(password)));
            if (!strength.isValid()) {
              errors.addAll(passwordStrengthValidator.getMessages(strength));
            }
          }
        }
      }
    }
    
    return errors;
  }
  
}
