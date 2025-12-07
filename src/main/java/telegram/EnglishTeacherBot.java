package telegram;

import ai.AIClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EnglishTeacherBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(EnglishTeacherBot.class);

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.bot-username}")
    private String botUsername;

    @Autowired
    private AIClient aiClient;

    @PostConstruct
    public void registerBot() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(this);
        } catch (TelegramApiRequestException e) {
            if (e.getErrorCode() == 404) {
                log.warn("Telegram returned 404 while clearing webhook; continuing with long polling", e);
            } else {
                throw e;
            }
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String msg = update.getMessage().getText();

        if ("/start".equalsIgnoreCase(msg)) {
            send(chatId, "Hi! ✨ I am your English tutor bot.\n\n" +
                    "Chat with me anytime for free — just send a message and I will correct and explain it for you.");
            return;
        }

        String aiResponse = aiClient.askAI(msg);
        send(chatId, aiResponse);
    }

    private void send(Long chatId, String text) {
        SendMessage m = new SendMessage(chatId.toString(), text);
        try {
            execute(m);
        } catch (Exception ignored) {
        }
    }

}
