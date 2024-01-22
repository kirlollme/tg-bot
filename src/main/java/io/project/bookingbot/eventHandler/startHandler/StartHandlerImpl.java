package io.project.bookingbot.eventHandler.startHandler;


import com.vdurmont.emoji.EmojiParser;
import io.project.bookingbot.constant.StartMenuConstant;
import io.project.bookingbot.factory.MessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class StartHandlerImpl implements StartHandler{

    private final MessageFactory messageFactory;


    @Autowired
    public StartHandlerImpl(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    public SendMessage initDialog(long chatId, String name) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageFactory.createWelcomeMsg( name ));

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(StartMenuConstant.START_MENU_BOOKING);
        keyboardRows.add(row);


        row = new KeyboardRow();
        row.add(StartMenuConstant.START_MENU_OFFICE);
        keyboardRows.add(row);


        row = new KeyboardRow();
        row.add(StartMenuConstant.START_MENU_CRITERION);
        row.add(StartMenuConstant.START_MENU_CONTACTS);
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

}
