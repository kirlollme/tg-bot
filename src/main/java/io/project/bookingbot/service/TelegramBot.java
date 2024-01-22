package io.project.bookingbot.service;


import com.vdurmont.emoji.EmojiParser;
import io.project.bookingbot.config.BotConfig;
import io.project.bookingbot.eventHandler.menuHandler.MenuHandler;
import io.project.bookingbot.eventHandler.startHandler.StartHandler;
import io.project.bookingbot.factory.MessageFactory;
import io.project.bookingbot.model.Ads;
import io.project.bookingbot.model.AdsRepository;
import io.project.bookingbot.model.User;
import io.project.bookingbot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static io.project.bookingbot.constant.ButtonRequestConstant.ROUTE;
import static io.project.bookingbot.constant.CriterionBooking.*;
import static io.project.bookingbot.constant.StartMenuConstant.*;

@Slf4j
@Component
@Scope("singleton")
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private MessageFactory messageFactory;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdsRepository adsRepository;

    private final MenuHandler menuHandler;
    private final StartHandler startHandler;
    final BotConfig config;

    static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities.\n\n" +
            "You can execute commands from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see this message again";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    static final String ERROR_TEXT = "Error occurred: ";

    @Autowired
    public TelegramBot(MenuHandler menuHandler, StartHandler startHandler, BotConfig config) {
        this.menuHandler = menuHandler;
        this.startHandler = startHandler;
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/mydata", "get your data stored"));
        listofCommands.add(new BotCommand("/deletedata", "delete my data"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        listofCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if(messageText.contains("/send")) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user: users){
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            }

            else {
                try {


                    switch (messageText) {
                        case "/start":
                            execute(startHandler.initDialog(chatId, update.getMessage().getChat().getFirstName()));
                            break;

                        case START_MENU_CONTACTS:
                            execute(menuHandler.getContacts(chatId));
                            break;
//                            SendPhoto sendPhoto = new SendPhoto();
//                            sendPhoto.setChatId(String.valueOf(chatId));
//                            sendPhoto.setPhoto(new InputFile("https://ibb.co/FnBtwtj"));
//                            sendPhoto.setCaption("test");
                            //execute(sendPhoto);
                        case START_MENU_CRITERION:
                            execute(menuHandler.getCriterionBooking( chatId,PAGE_1));
                            break;
                        default:

                            prepareAndSendMessage(chatId, "Sorry, command was not recognized");

                    }
                } catch (TelegramApiException e){
                    throw new RuntimeException(e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(ROUTE) || callbackData.equals(ROUTE + " ")) {
                try {
                    EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
                    editMessageReplyMarkup.setChatId(String.valueOf(chatId));
                    editMessageReplyMarkup.setMessageId(messageId);
                    editMessageReplyMarkup.setReplyMarkup(menuHandler.getReplyMarkup());
                    execute(editMessageReplyMarkup);
                    execute(menuHandler.getLocation(chatId));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            if (callbackData.equals("DELETE")) {
                try {
                    DeleteMessage deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(String.valueOf(chatId));
                    deleteMessage.setMessageId(messageId);
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            if (callbackData.equals(PAGE_1) || callbackData.equals(PAGE_2) || callbackData.equals(PAGE_3)) {
                try {
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setMessageId(messageId);
                    editMessageText.setChatId(String.valueOf(chatId));
                    editMessageText.setText(messageFactory.createHtmlRulesMsg(callbackData));
                    editMessageText.setParseMode("Markdown");
                    editMessageText.setReplyMarkup(menuHandler.getButtonsForCriteriaBooking(callbackData));
                    execute(editMessageText);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

    private void registerUser(Message msg) {

        if(userRepository.findById(msg.getChatId()).isEmpty()){

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }

    private void startCommandReceived(long chatId, String name) {


        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :blush:");
        log.info("Replied to user " + name);


        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("weather");
        row.add("get random joke");

        keyboardRows.add(row);

        row = new KeyboardRow();

        row.add("register");
        row.add("check my data");
        row.add("delete my data");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }


    private void executeEditMessageText(String text, long chatId, long messageId){
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds(){

        var ads = adsRepository.findAll();
        var users = userRepository.findAll();

        for(Ads ad: ads) {
            for (User user: users){
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }

    }
}
