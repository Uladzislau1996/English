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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import payment.NowPaymentsClient;
import premium.PremiumService;

import java.util.List;

@Component
public class EnglishTeacherBot extends TelegramLongPollingBot {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.bot-username}")
    private String botUsername;

    @Autowired
    private PremiumService premiumService;

    @Autowired
    private NowPaymentsClient payments;

    @Autowired
    private AIClient aiClient;

    @PostConstruct
    public void registerBot() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
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

        if (update.hasCallbackQuery()) {
            handleCallback(update);
            return;
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        String msg = update.getMessage().getText();

        if ("/start".equalsIgnoreCase(msg)) {
            send(chatId, "Hi! ✨ I am your English tutor bot.\n\n" +
                    "To talk to the AI you need an active subscription.\n" +
                    "Price: 1 USDT / month.\n" +
                    "Use /buy to get a payment link.");
            return;
        }

        if ("/buy".equalsIgnoreCase(msg)) {
            sendInvoiceLink(chatId, userId);
            return;
        }

        if (!premiumService.isPremium(userId)) {
            send(chatId, "You don't have an active subscription yet. Tap /buy to activate premium access.");
            return;
        }

        try {
            String aiResponse = aiClient.askAI(msg);
            send(chatId, aiResponse);
        } catch (Exception e) {
            send(chatId, "Sorry, I couldn't reach the AI right now: " + e.getMessage());
        }
    }

    private void handleCallback(Update update) {
        if (update.getCallbackQuery() == null) {
            return;
        }

        String data = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long userId = update.getCallbackQuery().getFrom().getId();

        if ("buy".equalsIgnoreCase(data)) {
            sendInvoiceLink(chatId, userId);
        }
    }

    private void sendInvoiceLink(Long chatId, Long userId) {
        try {
            String link = payments.createInvoice(userId);
            send(chatId, "Pay for premium: " + link);
        } catch (Exception e) {
            send(chatId, "Payment error: " + e.getMessage());
        }
    }

    private void send(Long chatId, String text) {
        SendMessage m = new SendMessage(chatId.toString(), text);
        try {
            execute(m);
        } catch (Exception ignored) {
        }
    }

    private void sendPayButton(Long chatId) {
        SendMessage m = new SendMessage(chatId.toString(), "Activate subscription ↓");
        InlineKeyboardMarkup kb = new InlineKeyboardMarkup();

        InlineKeyboardButton btn = new InlineKeyboardButton("Buy 1 USDT");
        btn.setCallbackData("buy");

        kb.setKeyboard(List.of(List.of(btn)));
        m.setReplyMarkup(kb);

        try {
            execute(m);
        } catch (Exception ignored) {
        }
    }
}
