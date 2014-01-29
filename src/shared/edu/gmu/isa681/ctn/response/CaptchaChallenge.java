/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.response;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class CaptchaChallenge extends Response {

  private byte[] imageBytes;
  
  public CaptchaChallenge(StatusCode status, BufferedImage image) throws IOException {
    super(status);
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write( image, "jpg", baos );
    baos.flush();
    imageBytes = baos.toByteArray();
    baos.close();
  }
  
  public BufferedImage getImage() {
    try {
      return ImageIO.read(new ByteArrayInputStream(imageBytes));
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
