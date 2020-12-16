package com.github.mmodzel3.lostfinderserver.alert;

import com.github.mmodzel3.lostfinderserver.user.AuthenticatedUserTestsAbstract;
import com.github.mmodzel3.lostfinderserver.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class AlertTestsAbstract extends AuthenticatedUserTestsAbstract {
    protected static String TEST_ALERT_DESCRIPTION = "description";
    protected static AlertType TEST_ALERT_TYPE = AlertType.HELP;
    protected static Double TEST_ALERT_RANGE = 120.0;

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
                .description(TEST_ALERT_DESCRIPTION)
                .user(user)
                .type(TEST_ALERT_TYPE)
                .sendDate(yesterday)
                .receivedDate(yesterday)
                .lastUpdateDate(yesterday)
                .build();
    }

    UserAlert buildTestUserAlert(AlertType alertType) {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        return UserAlert.builder()
                .description(TEST_ALERT_DESCRIPTION)
                .type(alertType)
                .sendDate(yesterday)
                .range(TEST_ALERT_RANGE)
                .build();
    }

    UserAlert buildTestUserAlert() {
        return buildTestUserAlert(TEST_ALERT_TYPE);
    }
}
