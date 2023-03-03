package com.nd705.myBot.service;


import com.nd705.myBot.config.BotConfig;
import com.nd705.myBot.entity.Ads;
import com.nd705.myBot.entity.AdsRepository;
import com.nd705.myBot.entity.User;
import com.nd705.myBot.entity.UserRepository;
import com.nd705.myBot.entity.weather.City;
import com.nd705.myBot.entity.weather.Weather;
import com.nd705.myBot.service.parsrers.ParseBankService;
import com.nd705.myBot.service.parsrers.ParseWeatherService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdsRepository adsRepository;




    final BotConfig config;

    static final String ABOUT_ME_TEXT = "Привет, меня зовут Андрей \n\n" +
            "Я начинающий Java разработчик, а это pet проект:\n" +
            "мой личный бот для парсинга нужной мне информации\n" +
            "и прочих экспериментов\n\n" +
            "Более подробная информация обо мне ниже:\n\n";

    static final String EDUCATIONAL_MASTER = "*2011*\n" +
            "*СПбГЭТУ\"ЛЭТИ\"*\n" +
            "Магистр\n" +
            "_Автоматизация технологических комплексов средствами компьютеризированных электроприводных систем_\n\n" +
            "*2009*\n " +
            "*СПбГЭТУ\"ЛЭТИ\"*\n" +
            "Бакалавр\n" +
            "_Электротехника, электромеханика и электротехнологии_\n";
    static final String EXPERIENCE_WEST =
            "*июнь 2022 \\- сентябрь 2022*\n" +
            "*ООО Вест Инжиниринг*\n" +
            "_Ведущий инженер проектов_\n\n" +
            "Выполнение работ в рамках проекта «Система контроля и управления противопожарной защитой»:\n" +
            "\\> разработка документов стадии технического проекта;\n" +
            "\\> разработка/корректировка ТЗ, структурных схем, эксплуатационной документации;\n" +
            "\\> работы по согласованию отдельных документов с заказчиком \\(устранение замечаний, корректировка\\);\n" +
            "\\> разработка исходных данных для отдела проектирования \\(в части основных технических решений \\- структурных схем, спецификаций\\);\n" +
            "\\> участие в подборе оборудования и предложение различных технических решений\n\n";
    static final String EXPERIENCE_ES =
            "*июль 2020 \\- июнь 2022*\n" +
            "*ООО ПО Энергосистема*\n" +
            "_Ведущий инженер АСУ_\n\n" +
            "Основные обязанности:\n" +
            "\\> разработка спецификаций и технических описаний раздела АСУ коммерческого предложения;\n" +
            "\\> разработка конфигураций оборудования АСУ, программного обеспечения ПЛК и сенсорных панелей оператора;\n" +
            "\\>  разработка проектной и рабочей документации АСУ\\.\n\n"+
            "Выполненные проекты:\n" +
            "\\> разработка и внедрение программного обеспечения системы мониторинга НКУ на базе ПЛК Modicon M251 и сенсорной графической панели Magelis GTU с интеграцией устройств нижнего уровня \\(мониторинг и дистанционное управление\\): Micrologic, ПЧ ATV630, устройства защиты двигателя TesysT и TesysU, Acti9 SmartLink;\n" +
            "\\> интеграция программного обеспечения EcoStruxure Power Monitoring Expert: подключение устройств нижнего уровня со стандартными драйверами, конфигурирование драйверов для нестандартных устройств, отрисовка однолинейных схем, настройка экранных панелей, диаграмм и отчетов;\n" +
            "\\> конфигурация ПЛК PhoenixContact для сбора данных c модулей ввода/вывода и внешних устройств по протоколу Modbus RTU и передачи на верхний уровень по стандартам МЭК 61850\\-8\\-1 \\(MMS\\), МЭК 60870\\-5\\-104\\.\n\n";

    static final String EXPERIENCE_ELM =
            "*январь 2017 \\- июль 2020*\n" +
            "*Электронмаш*\n" +
            "_Ведущий инженер АСУ_\n\n" +
            "Основные обязанности:\n" +
            "\\> разработка программного обеспечения и человеко\\-машинного интерфейса для систем мониторинга и управления СОПТ, НКУ, КТП, КРУ на базе ПЛК и сенсорной графической панели;\n" +
            "\\> конфигурирование устройств МИП, МП РЗА в части коммуникационных настроек для интеграции на средний и верхний уровни по протоколам Modbus RTU/TCP и стандартам МЭК 61850\\-8\\-1 \\(MMS, GOOSE\\), МЭК 60870\\-5\\-104;\n" +
            "\\> проведение ПНР, гарантийных и диагностических работ на объектах Ленэнерго, Газпром, ЗапСибНефтехим, Роспан, Вологдаэнерго\\.\n\n"+
            "Выполненные проекты:\n" +
            "\\> конфигурирование устройств для передачи данных на средний уровень по протоколам Modbus RTU/TCP, МЭК 61850, МЭК 60870\\-104: ЭНИП, ЭНКС, ЭНМВ производства Энергосервис, ПЛК Wago PFC200, терминалы РЗА ABB REF542, REF620, счетчики электроэнергии СЭТ;\n" +
            "\\> конфигурирование контроллеров среднего уровня ARIS CS\\-M, ARIS MT200 для сбора данных и передачи на верхний уровень по стандартам МЭК 61850\\-8\\-1 \\(MMS\\), МЭК 60870\\-5\\-104;\n" +
            "\\> подготовка данных и оборудования для интеграции в SCADA систему верхнего уровня\\.\n\n";
    static final String EXPERIENCE_EA_RIVS =
            "*октябрь 2010 \\- июнь 2014*\n" +
            "*август 2016 \\- декабрь 2016*\n" +
            "*ООО \"Электроавтоматика\"*\n" +
            "_Инженер АСУ ТП_\n\n" +
            "*июнь 2014 \\- август 2016*\n" +
            "*СП ЗАО ИВС*\n" +
            "_Инженер\\-программист \\(Аналитический центр\\)_\n\n" +
            "Разработка программного обеспечения для ПЛК и HMI, SCADA для автоматизированных систем управления технологическими процессами\\. Основные направления \\- производство фанеры, котельные установки, насосные станции\\.\n";

    static final String COURSE_NETOLOGY =
            "*декабрь 2021 \\- июль 2022*\n" +
                    "*Нетология*\n" +
                    "_Java\\-разработчик_\n\n" +
                    "\\- основы Java;\n" +
                    "\\- алгоритмы и структуры данных;\n" +
                    "\\- Collections;\n" +
                    "\\- Git;\n" +
                    "\\- Maven/Gradle;\n" +
                    "\\- работа с файлами \\(CSV, XML, JSON\\);\n" +
                    "\\- лямбда и streams;\n" +
                    "\\- шаблоны проектирования;\n" +
                    "\\- тестирование \\(JUnit, Mockito\\);\n" +
                    "\\- основы web \\(HTTP и т\\.п\\.\\)\\.\n";
    static final String COURSE_UDEMY =
            "*сентябрь 2022 \\- по н\\.\\.*\n" +
                    "*Udemy*\n" +
                    "_Spring for begginers_\n\n" +
                    "\\- Spring IoC, DI;\n" +
                    "\\- AOP;\n" +
                    "\\- JDBC, JPA, Hibernate;\n" +
                    "\\- Spring MVC;\n" +
                    "\\- Spring REST;\n" +
                    "\\- Spring Security \\(maintences\\);\n" +
                    "\\- Spring Boot \\(REST, JPA\\).";
    static final String COURSE_JBA =
            "*сентябрь 2021 \\- январь 2021*\n" +
                    "*JetBrains Academy*\n" +
                    "_Java backend developer_\n\n" +
                    "\\- OOP;\n" +
                    "\\- REST;\n" +
                    "\\- Spring Boot;\n" +
                    "\\- Exceptions;\n" +
                    "\\- JSON;\n" +
                    "\\- SQL;\n";
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";
    static final int nWeatherLines = 168;
    static final int forcastDays = 7;
    static final int forcastHoursInt = 6;
    static Map<String, City> cities = new HashMap<>();



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


        {
            cities.put("Danang", new City("Danang", 16.07, 108.22));
            cities.put("Vyborg", new City("Выборг", 60.71, 28.75));
            cities.put("Lahta", new City("Лахтинский разлив", 60.00, 30.18));
            cities.put("SPb", new City("Санкт Петербург", 59.95, 30.34));
            cities.put("Siverskii", new City("Сиверский", 59.36, 30.10));
            cities.put("Vuoksa", new City("Вуокса", 60.88, 29.83));
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

    private Weather[] weather = new Weather[nWeatherLines];
    @Override
    public void onUpdateReceived(Update update) {
        StringBuilder forecast;


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

                    case "/parse", "В меню":
                        chooseInfoToParse(chatId, "Выберите из списка:");

                        break;

                    case "/wallet":
                        prepareAndSendMessage(chatId, "Here will be wallet");
                        break;

                    case "/info":
                        chooseResume(chatId, ABOUT_ME_TEXT);
                        break;

                    //Parse
                    case "Курс обмена ЦБ, Кыргызстан":

                        prepareAndSendMessage(chatId, ParseBankService.parseKgBank());
                        break;
                    //Parse / погода
                    case "Погода":
                        chooseCity(chatId, "Выберите город:");
                        break;

                    case "Да Нанг":
                        chooseTable(chatId, getCityWeather(weather, "Danang", true));
                        break;

                    case "Санкт Петербург":
                        chooseTable(chatId, getCityWeather(weather, "SPb", true));
                        break;

                    case "Выборг":
                        chooseTable(chatId, getCityWeather(weather, "Vyborg", true));
                        break;

                    case "Лахта":
                        chooseTable(chatId, getCityWeather(weather, "Lahta", true));
                        break;

                    case "Сиверский":
                        chooseTable(chatId, getCityWeather(weather, "Siverskii", true));
                        break;

                    case "Вуокса":
                        chooseTable(chatId, getCityWeather(weather, "Vuoksa", true));
                        break;

                    case "Подробнее":
                        chooseBack(chatId, getWeatherTable(weather));
                        break;

                    case "Назад":
                        chooseCity(chatId, "Выберите город:");
                        break;


                    case "Прогноз северного сияния":

                        prepareAndSendMessage(chatId, "тут будет прогноз северного сияния");
                        break;


                    case "Образование":
                        chooseResume(chatId, EDUCATIONAL_MASTER);
                        break;
                    case "Опыт работы":
                        prepareAndSendMessage(chatId, EXPERIENCE_WEST);
                        prepareAndSendMessage(chatId, EXPERIENCE_ES);
                        prepareAndSendMessage(chatId, EXPERIENCE_ELM);
                        chooseResume(chatId,EXPERIENCE_EA_RIVS );
                        break;
                    case "Курсы":
                        prepareAndSendMessage(chatId, COURSE_UDEMY);
                        prepareAndSendMessage(chatId, COURSE_JBA);
                        chooseResume(chatId,COURSE_NETOLOGY );
                        break;
                    case "Контакты":
                        prepareAndSendMessage(chatId, "https://github.com/and705");
                        prepareAndSendMessage(chatId, "https://www.linkedin.com/in/nd705/");
                        prepareAndSendMessage(chatId, "aandreev0705@gmail.com");
                        chooseResume(chatId,"");
                        break;



                    default:
                        prepareAndSendMessage(chatId, "Sorry, command wasn't recognized");
                }
            }
        } //else if (update.hasCallbackQuery()) {
//            String callbackData = update.getCallbackQuery().getData();
//            long messageId = update.getCallbackQuery().getMessage().getMessageId();
//            long chatId = update.getCallbackQuery().getMessage().getChatId();
//
//            if (callbackData.equals(YES_BUTTON)) {
//                String text = "You pressed YES button";
//                executeEditMessageText(text, chatId, messageId);
//
//            } else if (callbackData.equals(NO_BUTTON)) {
//                String text = "You pressed NO button";
//                executeEditMessageText(text, chatId, messageId);
//            }
//
//        }

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
        message.setParseMode("MarkdownV2");

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

    private String getCityWeather(Weather[] weather,  String city, boolean emogi){
        weather = ParseWeatherService.getWeatherFromOpenMeteo(cities.get(city).getLatitude(), cities.get(city).getLongitude());
        StringBuilder forecast = new StringBuilder("Прогноз погоды в " + cities.get(city).getCityName() + "\n\n");
        if (emogi){
            forecast.append(ParseWeatherService.getWeatherEmoji(weather, forcastDays, forcastHoursInt));
        } else {
            forecast.append(ParseWeatherService.getWeatherTableFull(weather, forcastDays, forcastHoursInt));
        }
        this.weather = weather;
        return forecast.toString();
    }

    private String getWeatherTable(Weather[] weather){
         StringBuilder forecast = new StringBuilder();
         forecast.append(ParseWeatherService.getWeatherTableFull(weather, forcastDays, forcastHoursInt));
        return forecast.toString();
    }

    private void chooseInfoToParse(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setParseMode("MarkdownV2");
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
        keyboardMarkup.setOneTimeKeyboard(true);

        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }

    private void chooseCity(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setParseMode("MarkdownV2");
        //клавиатура
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        // первый ряд
        KeyboardRow row = new KeyboardRow();
        row.add("Да Нанг");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Санкт Петербург");
        row.add("Москва");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Сиверский");
        row.add("Лахта");
        row.add("Выборг");
        row.add("Вуокса");
        keyboardRows.add(row);


        row = new KeyboardRow();
        row.add("В меню");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setOneTimeKeyboard(true);

        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }

    private void chooseBack(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setParseMode("MarkdownV2");
        //клавиатура
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        // первый ряд
        KeyboardRow row = new KeyboardRow();
        row.add("Назад");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setOneTimeKeyboard(true);

        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }
    private void chooseTable(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setParseMode("MarkdownV2");
        //клавиатура
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        // первый ряд
        KeyboardRow row = new KeyboardRow();
        row.add("Подробнее");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setOneTimeKeyboard(true);

        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }


    private void chooseResume(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setParseMode("MarkdownV2");
        //клавиатура
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        // первый ряд
        KeyboardRow row = new KeyboardRow();
        row.add("Образование");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Опыт работы");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Курсы");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Контакты");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("В начало");
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
