package telegram;

import ai.AIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class EnglishTeacherBot extends TelegramLongPollingBot {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.bot-username}")
    private String botUsername;

    @Autowired
    private AIClient aiClient;

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

        if (msg.equalsIgnoreCase("start") || msg.equalsIgnoreCase("/start")) {
            send(chatId,
                    "Hi! I'm Alex, your English-speaking friend. 😊\n\n" +
                            "I will talk with you just like a real person and I will also correct your English gently.\n" +
                            "Just send me any message — let's practice together!"
            );
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
