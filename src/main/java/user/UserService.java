package user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    public UserEntity getOrCreateUser(Long id) {
        return repo.findById(id).orElseGet(() -> {
            UserEntity u = new UserEntity();
            u.setUserId(id);
            u.setPremiumUntil(null);
            return repo.save(u);
        });
    }

    public void save(UserEntity user) {
        repo.save(user);
    }
}