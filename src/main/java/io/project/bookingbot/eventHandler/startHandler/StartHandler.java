package io.project.bookingbot.eventHandler.startHandler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface StartHandler {
    SendMessage initDialog(long chatId, String name);
}
