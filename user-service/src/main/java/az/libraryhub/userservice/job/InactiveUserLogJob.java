package az.libraryhub.userservice.job;

import az.libraryhub.userservice.entity.User;
import az.libraryhub.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InactiveUserLogJob {

    private static final Logger logger = LoggerFactory.getLogger(InactiveUserLogJob.class);

    private final UserRepository userRepository;

    public InactiveUserLogJob(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void logInactiveUsers() {
        logger.info("Deaktiv istifadəçi yoxlanışı başladı");

        List<User> allUsers = userRepository.findAll();
        long inactiveCount = allUsers.stream().filter(u -> !u.getIsActive()).count();

        logger.info("Ümumi istifadəçi sayı: {}, Deaktiv: {}", allUsers.size(), inactiveCount);

        allUsers.stream()
                .filter(u -> !u.getIsActive())
                .forEach(u -> logger.info("Deaktiv istifadəçi: username={}, id={}", u.getUsername(), u.getId()));
    }
}
