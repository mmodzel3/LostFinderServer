package com.github.mmodzel3.lostfinderserver.alert;

import com.github.mmodzel3.lostfinderserver.user.AuthenticatedUserTestsAbstract;
import com.github.mmodzel3.lostfinderserver.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class AlertTestsAbstract extends AuthenticatedUserTestsAbstract {
    protected static String TEST_ALERT_TITLE = "title";
    protected static String TEST_ALERT_DESCRIPTION = "description";
    protected static String TEST_ALERT_TYPE = "type";

    @Autowired
    protected AlertRepository alertRepository;

    Alert createTestActiveAlert(User user) {
        Alert alert = buildTestAlert(user);

        alertRepository.save(alert);

        return alert;
    }

    Alert createTestNonActiveAlert(User user) {
        Alert alert = buildTestAlert(user);
        alert.setEndDate(LocalDateTime.now());

        alertRepository.save(alert);

        return alert;
    }

    void deleteAllAlerts() {
        alertRepository.deleteAll();
    }

    Alert buildTestAlert(User user) {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        return Alert.builder()
                .title(TEST_ALERT_TITLE)
                .description(TEST_ALERT_DESCRIPTION)
                .type(TEST_ALERT_TYPE)
                .user(user)
                .showNotificationAtStart(false)
                .showNotificationAtEnd(true)
                .sendDate(yesterday)
                .receivedDate(yesterday)
                .lastUpdateDate(yesterday)
                .build();
    }

    UserAlert buildTestUserAlert() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        return UserAlert.builder()
                .title(TEST_ALERT_TITLE)
                .description(TEST_ALERT_DESCRIPTION)
                .type(TEST_ALERT_TYPE)
                .showNotificationAtStart(false)
                .showNotificationAtEnd(true)
                .sendDate(yesterday)
                .build();
    }
}
