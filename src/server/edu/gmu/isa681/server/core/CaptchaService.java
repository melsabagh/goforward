/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.server.core;
import java.awt.image.BufferedImage;
import java.util.Locale;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
 
enum CaptchaService {
  INSTANCE;
  
  /**
   * CAPTCHA Service provider. Currently uses JCaptcha. 
   * @see: http://jcaptcha.sourceforge.net/
   */
  private ImageCaptchaService captchaService = new DefaultManageableImageCaptchaService();
 
  /**
   * Returns a CAPTCHA image challenge and associates it with the given id.
   * @param id
   * @return BufferedImage of CAPTCHA challenge in a country/language neutral locale.
   */
  public BufferedImage getImageChallengeForId(String id) {
    return captchaService.getImageChallengeForID(id, Locale.ROOT);
  }
  
  /**
   * Validates the response for the CAPTCHA challenge associated with the given id.
   * @param id
   * @param response
   * @return <code>true</code> if response is correct, <code>false</code> otherwise.
   */
  public boolean validateResponseForId(String id, String response) {
    return captchaService.validateResponseForID(id, response);
  }
}