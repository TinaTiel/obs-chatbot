package com.tinatiel.obschatbot.data.load;

public interface DataLoader {
  void loadObsSettings();
  void loadTwitchChatSettings();
  void loadTwitchAuthSettings();
  void loadSystemSettings();
}
