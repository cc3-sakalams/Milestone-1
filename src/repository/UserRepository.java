package repository;

import java.util.Optional;
import model.User;

public interface UserRepository extends Repository<User, String> {
    Optional<User> findByEmail(String email);
}
