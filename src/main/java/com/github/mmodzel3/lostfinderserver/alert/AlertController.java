package com.github.mmodzel3.lostfinderserver.alert;

import com.github.mmodzel3.lostfinderserver.notification.PushNotificationProcessingException;
import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
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
    ServerResponse addAlert(@AuthenticationPrincipal Authentication authentication,
                            @RequestBody UserAlert userAlert) {
        User user = (User) authentication.getPrincipal();

        try {
            alertService.addAlert(user, userAlert);
            return ServerResponse.OK;
        } catch (AlertAddPermissionException e) {
            return ServerResponse.INVALID_PERMISSION;
        } catch (PushNotificationProcessingException e) {
            return ServerResponse.OK;
        }
    }

    @PutMapping("/api/alerts/end")
    ServerResponse endAlert(@AuthenticationPrincipal Authentication authentication,
                   @RequestParam String alertId) {
        User user = (User) authentication.getPrincipal();

        try {
            alertService.endAlert(user, alertId);
            return ServerResponse.OK;
        } catch (AlertUpdatePermissionException e) {
            return ServerResponse.INVALID_PERMISSION;
        } catch (AlertDoesNotExistsException e) {
            return ServerResponse.NOT_FOUND;
        } catch (PushNotificationProcessingException e) {
            return ServerResponse.OK;
        }
    }
}
