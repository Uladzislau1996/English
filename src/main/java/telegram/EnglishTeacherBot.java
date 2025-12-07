package telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import payment.NowPaymentsClient;
import premium.PremiumService;
import java.util.List;

@Component
public class EnglishTeacherBot extends TelegramLongPollingBot {

    @Autowired
    private PremiumService premiumService;

    @Autowired
    private NowPaymentsClient payments;

    @Override
    public String getBotToken() {
        return "TELEGRAM_BOT_TOKEN";
    }

    @Override
    public String getBotUsername() {
        return "YourTutorBot";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {

            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();
            String msg = update.getMessage().getText();

            if (msg.equals("/start")) {
                send(chatId, "Привет! ✨ Я твой English-tutor bot.\n\n" +
                        "Чтобы получить доступ к AI — оформи подписку.\n" +
                        "Цена: 1 USDT / месяц");

//                sendPayButton(chatId);
                return;
            }

            if (msg.equals("/buy")) {
                try {
                    String link = payments.createInvoice(userId);
                    send(chatId, "Оплатить подписку: " + link);
                } catch (Exception e) {
                    send(chatId, "Ошибка: " + e.getMessage());
                }
                return;
            }

            // Проверка подписки
//            if (!premiumService.isPremium(userId)) {
//                send(chatId, "У тебя нет подписки.\nНажми /buy");
//                return;
//            }

            // тут идёт логика общения с AI (ты уже её писал)
            send(chatId, "Твой AI-ответ здесь...");
        }
    }

    private void send(Long chatId, String text) {
        SendMessage m = new SendMessage(chatId.toString(), text);
        try {
            execute(m);
        } catch (Exception ignored) {}
    }

    private void sendPayButton(Long chatId) {
        SendMessage m = new SendMessage(chatId.toString(), "Оформить подписку ↓");
        InlineKeyboardMarkup kb = new InlineKeyboardMarkup();

        InlineKeyboardButton btn = new InlineKeyboardButton("Buy 1 USDT");
        btn.setCallbackData("buy");

        kb.setKeyboard(List.of(List.of(btn)));
        m.setReplyMarkup(kb);

        try { execute(m); } catch (Exception ignored) {}
    }
}