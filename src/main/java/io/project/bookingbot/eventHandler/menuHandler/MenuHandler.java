package io.project.bookingbot.eventHandler.menuHandler;

import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface MenuHandler {
    SendMessage getPhoto();

    SendMessage getOffices();
    InlineKeyboardMarkup getButtonsForCriteriaBooking(String page);

    SendMessage getCriterionBooking(long chatId, String page);

    SendMessage getContacts(long chatId);

    SendLocation getLocation(long chatId);
    InlineKeyboardMarkup getReplyMarkup();

}
