package premium;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import user.UserEntity;
import user.UserService;

import java.time.LocalDateTime;

@Service
public class PremiumService {

    @Autowired
    private UserService userService;

    // 30 дней подписки
    public void activatePremium(Long userId) {
        UserEntity user = userService.getOrCreateUser(userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newDate = now.plusDays(30);

        user.setPremiumUntil(newDate);
        userService.save(user);
    }

    public boolean isPremium(Long userId) {
        UserEntity user = userService.getOrCreateUser(userId);

        return user.getPremiumUntil() != null &&
                user.getPremiumUntil().isAfter(LocalDateTime.now());
    }
}