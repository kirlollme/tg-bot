package io.project.bookingbot.factory;

public interface MessageFactory {
    public String createWelcomeMsg( String name);

    String createContactsMsg();
    String parserToUnicode(String text);

    String createHtmlRulesMsg(String page);
}
