package io.project.bookingbot.factory;

import com.vdurmont.emoji.EmojiParser;
import io.project.bookingbot.constant.ContactConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static io.project.bookingbot.constant.ContactConstant.*;
import static io.project.bookingbot.constant.CriterionBooking.*;

@Component
@Slf4j
public class MessageFactoryImpl implements MessageFactory{
    public String createWelcomeMsg( String name){

        String answer = EmojiParser.parseToUnicode("Здравствуйте , " + name + ", добро пожаловать, в наш бот, здесь вы можете забронировать кабинет для работы!" + " :blush:");
        log.info("Replied to user " + name);
        return answer;
    }
    public String parserToUnicode(String text){
        return EmojiParser.parseToUnicode(text);
    }
    @Override
    public String createContactsMsg() {
        return CONTACT_PLACE + "\n" + "тел. " + CONTACT_NUMBER;
    }

    @Override
    public String createHtmlRulesMsg(String page) {
        if (page.equals(PAGE_1)) return RENTAL_RULES_FORMATTED;
        else if ( page.equals(PAGE_2)) {
            return CANCEL_BOOKING_FORMATTED;
        }
        else {
            return RULES_USING_FORMATTED;
        }

    }
}
