/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.error;

public class UnexpectedException extends AbstractCodedException {

  public UnexpectedException(Code code, String message, Throwable throwable) {
    super(code, message, throwable);
  }

  public UnexpectedException(String message, Throwable throwable) {
    super(Code.UNEXPECTED, message, throwable);
  }

  public UnexpectedException(String message) {
    super(Code.UNEXPECTED, message, null);
  }
}
