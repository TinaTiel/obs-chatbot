/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.error;

/**
 * Defines an exception designated by a unique ${@link Code}.
 */
public class AbstractCodedException extends RuntimeException {

  protected Code code;

  protected AbstractCodedException(Code code, String message, Throwable throwable) {
    super(message, throwable);
    this.code = code;
  }

  public Code getCode() {
    return code;
  }

}
