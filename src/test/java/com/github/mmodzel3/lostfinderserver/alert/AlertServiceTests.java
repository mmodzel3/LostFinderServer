package com.github.mmodzel3.lostfinderserver.alert;

import com.github.mmodzel3.lostfinderserver.notification.PushNotificationProcessingException;
import com.github.mmodzel3.lostfinderserver.notification.PushNotificationService;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AlertServiceTests extends AlertTestsAbstract {
    private static final int ONE_ELEMENT_LIST_SIZE = 1;
    private static final int TWO_ELEMENT_LIST_SIZE = 2;

    private static final String NOT_EXISTING_ALERT_ID = "9999999999999-$";

    private static final String USER2_EMAIL = "email@email2.com";
    private static final String USER2_NAME = "user2";

    @MockBean
    private PushNotificationService pushNotificationService;

    @Autowired
    @InjectMocks
    private AlertService alertService;

    private Alert testAlert;

    @BeforeEach
    void setUp() {
        createTestUser();
        testAlert = createTestActiveAlert(testUser);
    }

    @AfterEach
    void tearDown() {
        deleteAllAlerts();
        deleteAllUsers();
    }

    @Test
    void whenGetAllActiveAlertsThenGotAll() {
        createTestNonActiveAlert(testUser);

        List<Alert> alertList = alertService.getAllActiveAlerts();
        assertEquals(ONE_ELEMENT_LIST_SIZE, alertList.size());
    }

    @Test
    void whenAddAlertThenItIsAdded() throws PushNotificationProcessingException, AlertAddPermissionException {
        UserAlert userAlert = buildTestUserAlert();

        alertService.addAlert(testUser, userAlert);

        List<Alert> alertList = alertRepository.findAll();
        assertEquals(TWO_ELEMENT_LIST_SIZE, alertList.size());
    }

    @Test
    void whenAddAlertWithWrongRoleThenAlertAddPermissionException() {
        UserAlert userAlert = buildTestUserAlert(AlertType.GATHER);

        assertThrows(AlertAddPermissionException.class, () -> alertService.addAlert(testUser, userAlert));
    }

    @Test
    void whenEndAlertThenItsStatusIsChanged()
            throws PushNotificationProcessingException, AlertDoesNotExistsException, AlertUpdatePermissionException {
        alertService.endAlert(testUser, testAlert.getId());

        Optional<Alert> possibleAlert = alertRepository.findById(testAlert.getId());
        assertTrue(possibleAlert.isPresent());
        assertNotNull(possibleAlert.get());
    }

    @Test
    void whenEndAlertThatDoesNotExistThenAlertDoesNotExistsException() {
        assertThrows(AlertDoesNotExistsException.class, () -> alertService.endAlert(testUser, NOT_EXISTING_ALERT_ID));
    }

    @Test
    void whenEndAlertForNotTheSameUserThenAlertUpdatePermissionException() {
        User user = buildTestUser(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);

        assertThrows(AlertUpdatePermissionException.class, () -> alertService.endAlert(user, testAlert.getId()));
    }

    @Test
    void whenEndAlertUsingManagerUserThenAlertIsEnded() throws PushNotificationProcessingException, AlertUpdatePermissionException, AlertDoesNotExistsException {
        User user = buildTestUser(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.MANAGER);

        Alert alert = alertService.endAlert(user, testAlert.getId());
        assertNotNull(alert.getEndDate());
    }

    @Test
    void whenEndAlertUsingOwnerUserThenAlertIsEnded() throws PushNotificationProcessingException, AlertUpdatePermissionException, AlertDoesNotExistsException {
        User user = buildTestUser(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.OWNER);

        Alert alert = alertService.endAlert(user, testAlert.getId());
        assertNotNull(alert.getEndDate());
    }
}
