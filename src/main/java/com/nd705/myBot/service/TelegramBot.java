package com.nd705.myBot.service;


import com.nd705.myBot.config.BotConfig;
import com.nd705.myBot.entity.Ads;
import com.nd705.myBot.entity.AdsRepository;
import com.nd705.myBot.entity.User;
import com.nd705.myBot.entity.UserRepository;
import com.nd705.myBot.service.parsrers.ParseService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdsRepository adsRepository;

    final BotConfig config;
    static final String HELP_TEXT = "This bot is created to demonstrate \n\n " +
            "You can execute commands from the main menu on the left or right\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /help to see this message again";
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config) {
        this.config = config;
        //Главное меню
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "активация и регистрация"));
        listofCommands.add(new BotCommand("/parse", "получение информации с сайтов"));
        listofCommands.add(new BotCommand("/wallet", "кошелек"));
        listofCommands.add(new BotCommand("/info", "Обо мне"));
        listofCommands.add(new BotCommand("/settings", "настройки"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list " + e.getMessage());
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

            //массовая рассылка
            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else {


                switch (messageText) {
                    case "/start":

                        registerUser(update.getMessage());
                        startCammandRecieved(chatId, update.getMessage().getChat().getFirstName());
                        break;

                    case "/parse":
                        chooseInfoToParse(chatId, "Выберите из списка:");

                        break;

                    case "/wallet":
                        prepareAndSendMessage(chatId, "Here will be wallet");
                        break;

                    case "/info":
                        prepareAndSendMessage(chatId, "Here will be information about me");
                        break;

                    //Parse
                    case "Курс обмена ЦБ, Кыргызстан":

                        prepareAndSendMessage(chatId, ParseService.parseKgBank());
                        break;

                    case "Погода":

                        prepareAndSendMessage(chatId, "тут будет погода");
                        break;

                    case "Прогноз северного сияния":

                        prepareAndSendMessage(chatId, "тут будет прогноз северного сияния");
                        break;


                    default:
                        prepareAndSendMessage(chatId, "Sorry, command wasn't recognized");
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(YES_BUTTON)) {
                String text = "You pressed YES button";
                executeEditMessageText(text, chatId, messageId);

            } else if (callbackData.equals(NO_BUTTON)) {
                String text = "You pressed NO button";
                executeEditMessageText(text, chatId, messageId);
            }

        }

    }


    private void register(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Do you really want to register?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>(); //ряды кнопок
        List<InlineKeyboardButton> rowInLine = new ArrayList<>(); //кнопки для одного ряда
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON); //id кнопки

        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON); //id кнопки

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);


    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
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


    private void startCammandRecieved(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + ":blush:");

        sendMessage(chatId, answer);
        log.info("Replied to user " + name);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setParseMode("HTML");

//        //клавиатура
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        // первый ряд
//        KeyboardRow row = new KeyboardRow();
//
//        row.add("weather");
//        row.add("get random joke");
//
//        keyboardRows.add(row);
//        // второй ряд
//        row = new KeyboardRow();
//        row.add("register");
//        row.add("check my data");
//        row.add("delete my data");
//
//        keyboardRows.add(row);
//
//        keyboardMarkup.setKeyboard(keyboardRows);
//
//        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void chooseInfoToParse(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
                //клавиатура
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        // первый ряд
        KeyboardRow row = new KeyboardRow();
        row.add("Курс обмена ЦБ, Кыргызстан");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Погода");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Прогноз северного сияния");
        keyboardRows.add(row);


        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
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


    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }
//    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds(){
        var ads = adsRepository.findAll();
        var users = userRepository.findAll();

        for(Ads ad: ads){
            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }
    }




}
