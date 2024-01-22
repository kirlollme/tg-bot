package io.project.bookingbot.eventHandler.menuHandler;


import io.project.bookingbot.factory.MessageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static io.project.bookingbot.constant.ButtonRequestConstant.ROUTE;
import static io.project.bookingbot.constant.CriterionBooking.*;

@Component
public class MenuHandlerImpl implements MenuHandler{

    private final MessageFactory messageFactory;
    private boolean flag = false;

    @Autowired
    public MenuHandlerImpl(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    public SendMessage getPhoto() {

        return null;
    }

    @Override
    public SendMessage getOffices() {

        return null;
    }

    @Override
    public SendMessage getCriterionBooking(long chatId, String page) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(messageFactory.createHtmlRulesMsg(page));
        sendMessage.setParseMode("Markdown");

//        } else if (page == 2 ) {
//            InlineKeyboardButton button = new InlineKeyboardButton();
//            button.setText(messageFactory.parserToUnicode(":arrow_left:"));
//            button.setCallbackData(PAGE_2);
//            InlineKeyboardButton button2 = new InlineKeyboardButton();
//            button2.setText("→");
//            button2.setCallbackData(PAGE_2);
//            List<List<InlineKeyboardButton>> list = new ArrayList<>();
//            list.add(List.of(button));
//            inlineKeyboardMarkup.setKeyboard(list);
//        }
        sendMessage.setReplyMarkup(getButtonsForCriteriaBooking(page));

        return sendMessage;
    }
    public InlineKeyboardMarkup getButtonsForCriteriaBooking(String page){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        if (page.equals(PAGE_1)) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(messageFactory.parserToUnicode(":arrow_right:"));
            button.setCallbackData(PAGE_2);
            List<List<InlineKeyboardButton>> list = new ArrayList<>();
            list.add(List.of(button));
            inlineKeyboardMarkup.setKeyboard(list);
        } else if (page.equals(PAGE_2)) {
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            button1.setText(messageFactory.parserToUnicode(":arrow_left:"));
            button1.setCallbackData(PAGE_1);
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText(messageFactory.parserToUnicode(":arrow_right:"));
            button2.setCallbackData(PAGE_3);
            List<List<InlineKeyboardButton>> list = new ArrayList<>();
            list.add(List.of(button1,button2));
            inlineKeyboardMarkup.setKeyboard(list);
        }
        else {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(messageFactory.parserToUnicode(":arrow_left:"));
            button.setCallbackData(PAGE_2);
            List<List<InlineKeyboardButton>> list = new ArrayList<>();
            list.add(List.of(button));
            inlineKeyboardMarkup.setKeyboard(list);
        }
        return inlineKeyboardMarkup;
    }

    @Override
    public SendMessage getContacts(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageFactory.createContactsMsg());

        message.setReplyMarkup(getReplyMarkup());
        return message;
    }

    @Override
    public InlineKeyboardMarkup getReplyMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Наш сайт");
        button.setUrl("https://spb-nv.com/");
        InlineKeyboardButton buttonMap = new InlineKeyboardButton();
        if (flag){
            buttonMap.setText(ROUTE);
            buttonMap.setCallbackData(ROUTE);
        }
        else {
            buttonMap.setText(ROUTE+" ");
            buttonMap.setCallbackData(ROUTE+" ");
        }
        flag = !flag;

        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(List.of(button,buttonMap));
        inlineKeyboardMarkup.setKeyboard(list);
        return inlineKeyboardMarkup;
    }


    public SendLocation getLocation(long chatId) {
        SendLocation sendLocation = new SendLocation();
        sendLocation.setChatId(String.valueOf(chatId));
        sendLocation.setLatitude(59.961969);
        sendLocation.setLongitude(30.348138);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Убрать карту");
        button.setCallbackData("DELETE");
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(List.of(button));
        inlineKeyboardMarkup.setKeyboard(list);
        sendLocation.setReplyMarkup(inlineKeyboardMarkup);
        return sendLocation;
    }
}
