package com.github.mmodzel3.lostfinderserver.alert;

import com.github.mmodzel3.lostfinderserver.notification.PushNotification;
import com.github.mmodzel3.lostfinderserver.notification.PushNotificationProcessingException;
import com.github.mmodzel3.lostfinderserver.notification.PushNotificationService;
import com.github.mmodzel3.lostfinderserver.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
class AlertService {
    static final String ALERT_NOTIFICATION_TYPE = "alert";

    private final AlertRepository alertRepository;
    private final PushNotificationService pushNotificationService;

    AlertService(AlertRepository alertRepository, PushNotificationService pushNotificationService) {
        this.alertRepository = alertRepository;
        this.pushNotificationService = pushNotificationService;
    }

    List<Alert> getAllActiveAlerts() {
        return alertRepository.findAllByEndDateNull();
    }

    Alert addAlert(User user, UserAlert userAlert) throws PushNotificationProcessingException, AlertAddPermissionException {
        if (!userAlert.getType().checkCreatePermission(user.getRole())) {
            throw new AlertAddPermissionException();
        }

        LocalDateTime receivedDate = LocalDateTime.now();

        Alert alert = Alert.builder()
                .user(user)
                .type(userAlert.getType())
                .location(userAlert.getLocation())
                .range(userAlert.getRange())
                .description(userAlert.getDescription())
                .sendDate(userAlert.getSendDate())
                .receivedDate(receivedDate)
                .lastUpdateDate(receivedDate)
                .build();

        alertRepository.save(alert);
        notifyUsersWithAlert(alert);

        return alert;
    }

    Alert endAlert(User user, String alertId)
            throws PushNotificationProcessingException, AlertDoesNotExistsException, AlertUpdatePermissionException {
        Optional<Alert> possibleAlert = alertRepository.findById(alertId);

        Alert alert = possibleAlert.orElseThrow(AlertDoesNotExistsException::new);
        if (alert.getEndDate() != null) {
            return alert;
        }

        if (user.getId().equals(alert.getUser().getId()) || user.isManager()) {
            LocalDateTime endDate = LocalDateTime.now();

            alert.setEndDate(endDate);
            alert.setLastUpdateDate(endDate);

            alertRepository.save(alert);
            notifyUsersWithAlert(alert);

            return alert;
        } else {
            throw new AlertUpdatePermissionException();
        }
    }

    private void notifyUsersWithAlert(Alert alert) throws PushNotificationProcessingException {
        PushNotification notification = PushNotification.builder()
                .type(ALERT_NOTIFICATION_TYPE)
                .data(alert)
                .build();

        pushNotificationService.sendNotificationToAllUsers(notification);
    }
}
