package user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private Long userId;
    private LocalDateTime premiumUntil;

    public UserEntity() {
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getPremiumUntil() {
        return premiumUntil;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPremiumUntil(LocalDateTime premiumUntil) {
        this.premiumUntil = premiumUntil;
    }
}
