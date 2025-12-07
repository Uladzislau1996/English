package telegram;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private final EnglishTeacherBot englishTeacherBot;

    public BotInitializer(EnglishTeacherBot englishTeacherBot) {
        this.englishTeacherBot = englishTeacherBot;
    }

    @PostConstruct
    public void init() throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(englishTeacherBot);
    }
}