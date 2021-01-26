package com.github.mmodzel3.lostfinderserver.alert;

import com.github.mmodzel3.lostfinderserver.notification.PushNotificationService;
import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
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
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

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
    void whengetAllAlertsThenGotAll() {
        createTestNonActiveAlert(testUser);

        Alert[] alerts = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
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
                .header(AUTHORIZATION, authorizationHeader)
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .body(userAlert)
                .post("/api/alerts/add")
                .then()
                .statusCode(200);

        List<Alert> alertList = alertRepository.findAll();
        assertEquals(TWO_ELEMENT_LIST_SIZE, alertList.size());
    }

    @Test
    void whenEndAlertWithWrongRoleThenGotInvalidPermission() {
        changeTestUserRole(UserRole.USER);

        UserAlert userAlert = buildTestUserAlert(AlertType.GATHER);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .body(userAlert)
                .post("/api/alerts/add")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenEndAlertThenItStatusIsChanged() {
        given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .param("alertId", testAlert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(200);

        List<Alert> alerts = alertService.getAllActiveAlerts();
        assertEquals(ZERO_ELEMENT_LIST_SIZE, alerts.size());
    }

    @Test
    void whenEndAlertThatDoesNotExistThenGotNotFound() {
        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .param("alertId", NOT_EXISTING_ALERT_ID)
                .put("/api/alerts/end")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.NOT_FOUND, response);
    }

    @Test
    void whenEndAlertForNotTheSameUserThenGotInvalidPermission() {
        changeTestUserRole(UserRole.USER);

        User user = buildTestUser(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        Alert alert = buildTestAlert(user);

        alertRepository.save(alert);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .param("alertId", alert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenEndAlertUsingManagerUserThenAlertIsEnded() {
        changeTestUserRole(UserRole.MANAGER);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .param("alertId", testAlert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<Alert> possibleAlert = alertRepository.findAll().stream()
                .filter(a -> a.getId().equals(testAlert.getId()))
                .findFirst();

        assertTrue(possibleAlert.isPresent());
        assertEquals(ServerResponse.OK, response);
        assertNotNull(possibleAlert.get().getEndDate());
    }

    @Test
    void whenEndAlertUsingOwnerUserThenAlertIsEnded() {
        changeTestUserRole(UserRole.OWNER);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .param("alertId", testAlert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<Alert> possibleAlert = alertRepository.findAll().stream()
                .filter(a -> a.getId().equals(testAlert.getId()))
                .findFirst();

        assertTrue(possibleAlert.isPresent());
        assertEquals(ServerResponse.OK, response);
        assertNotNull(possibleAlert.get().getEndDate());
    }

    @Test
    void whenEndAlertThatWasEndedThenItIsNotUpdated() {
        LocalDateTime dateTime = LocalDateTime.now().minusMonths(1);

        testAlert.setEndDate(dateTime);
        alertRepository.save(testAlert);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .param("alertId", testAlert.getId())
                .put("/api/alerts/end")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<Alert> possibleAlert = alertRepository.findAll().stream()
                .filter(a -> a.getId().equals(testAlert.getId()))
                .findFirst();

        assertTrue(possibleAlert.isPresent());
        assertEquals(ServerResponse.OK, response);
        assertNotNull(possibleAlert.get().getEndDate());
        assertEquals(dateTime.getMonth(), possibleAlert.get().getEndDate().getMonth());
    }
}
