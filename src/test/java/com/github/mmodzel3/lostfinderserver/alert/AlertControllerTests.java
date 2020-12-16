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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlertControllerTests extends AlertTestsAbstract {
    private static final int ZERO_ELEMENT_LIST_SIZE = 0;
    private static final int ONE_ELEMENT_LIST_SIZE = 1;
    private static final int TWO_ELEMENT_LIST_SIZE = 2;

    private static final String NOT_EXISTING_ALERT_ID = "9999999999.-$";

    private static final String USER2_EMAIL = "email@email2.com";
    private static final String USER2_NAME = "user2";

    @LocalServerPort
    int port;

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

        Alert[] alerts = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .extract()
                .as(Alert[].class);

        assertEquals(ONE_ELEMENT_LIST_SIZE, alerts.length);
    }

    @Test
    void whenAddAlertThenItIsAdded() {
        UserAlert userAlert = buildTestUserAlert();

        given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .body(userAlert)
                .post("/api/alerts/add")
                .then()
                .statusCode(200);

        List<Alert> alertList = alertService.getAllActiveAlerts();
        assertEquals(TWO_ELEMENT_LIST_SIZE, alertList.size());
    }

    @Test
    void whenEndAlertWithWrongRoleThenAlertAddPermissionException() {
        changeTestUserRole(UserRole.USER);

        UserAlert userAlert = buildTestUserAlert(AlertType.GATHER);

        given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .body(userAlert)
                .post("/api/alerts/add")
                .then()
                .statusCode(500);
    }

    @Test
    void whenEndAlertThenItStatusIsChanged() {
        given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .param("alertId", testAlert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(200);

        List<Alert> alerts = alertService.getAllActiveAlerts();
        assertEquals(ZERO_ELEMENT_LIST_SIZE, alerts.size());
    }

    @Test
    void whenEndAlertThatDoesNotExistThenAlertDoesNotExistsException() {
        given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .param("alertId", NOT_EXISTING_ALERT_ID)
                .put("/api/alerts/end")
                .then()
                .statusCode(500);
    }

    @Test
    void whenEndAlertForNotTheSameUserThenAlertUpdatePermissionException() {
        changeTestUserRole(UserRole.USER);

        User user = buildTestUser(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        Alert alert = buildTestAlert(user);

        given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .param("alertId", alert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(500);
    }

    @Test
    void whenEndAlertUsingManagerUserThenAlertIsEnded() {
        changeTestUserRole(UserRole.MANAGER);

        Alert alert = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .param("alertId", testAlert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(200)
                .extract()
                .as(Alert.class);

        assertNotNull(alert.getEndDate());
    }

    @Test
    void whenEndAlertUsingOwnerUserThenAlertIsEnded() {
        changeTestUserRole(UserRole.OWNER);

        Alert alert = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .param("alertId", testAlert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(200)
                .extract()
                .as(Alert.class);

        assertNotNull(alert.getEndDate());
    }

    @Test
    void whenEndAlertThatWasEndedThenItIsNotUpdated() {
        LocalDateTime dateTime = LocalDateTime.now().minusMonths(1);

        testAlert.setEndDate(dateTime);
        alertRepository.save(testAlert);

        Alert alert = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .param("alertId", testAlert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(200)
                .extract()
                .as(Alert.class);

        assertEquals(dateTime.getMonth(), alert.getEndDate().getMonth());
    }
}
