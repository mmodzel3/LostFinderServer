package com.github.mmodzel3.lostfinderserver.alert;

import com.github.mmodzel3.lostfinderserver.notification.PushNotificationProcessingException;
import com.github.mmodzel3.lostfinderserver.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class AlertController {
    private final AlertService alertService;

    AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/api/alerts")
    List<Alert> getAllActiveAlerts() {
        return alertService.getAllActiveAlerts();
    }

    @PostMapping("/api/alerts/add")
    Alert addAlert(@AuthenticationPrincipal Authentication authentication,
                   @RequestBody UserAlert userAlert) throws PushNotificationProcessingException {
        User user = (User) authentication.getPrincipal();

        return alertService.addAlert(user, userAlert);
    }

    @PutMapping("/api/alerts/end")
    Alert endAlert(@AuthenticationPrincipal Authentication authentication,
                   @RequestParam String alertId)
            throws PushNotificationProcessingException, AlertDoesNotExistsException, AlertUpdatePermissionException {
        User user = (User) authentication.getPrincipal();

        return alertService.endAlert(user, alertId);
    }
}
