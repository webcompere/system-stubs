package uk.org.webcompere.systemstubs.jupiter.examples;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

@ExtendWith(SystemStubsExtension.class)
public class SpringAppWithDynamicPropertiesTest {
    private static final WireMockServer wireMock = new WireMockServer(Options.DYNAMIC_PORT);

    // sets the environment before Spring even starts
    @SystemStub
    private static EnvironmentVariables environmentVariables;

    @SystemStub
    private static SystemOut systemOut = new SystemOut(tapAndOutput());

    @BeforeAll
    static void beforeAll() {
        wireMock.start();

        wireMock.stubFor(get(urlEqualTo("/foo"))
            .willReturn(aResponse().withBody("from wiremock").withStatus(200)));

        // this is setting an environment variable to override a Spring
        // property `server.url`. We could similarly use the `SystemProperties`
        // object to set `server.url` - but this approach is slightly more challenging,
        // so it a more interesting example.
        // Only this technique can set environment variables before ANY spring resources
        // start up. This could, therefore, be used to override system settings like
        // https_proxy, picked up by SDKs in use in the spring application
        environmentVariables.set("SERVER_URL", wireMock.baseUrl());
    }

    @AfterAll
    static void afterAll() {
        wireMock.stop();
    }

    // pretend spring app that expects server.url property
    // but can have it overridden by an environment variable
    @RestController
    public static class RestApi {
        private String baseUrl;
        private RestTemplate restTemplate = new RestTemplate();

        public RestApi(@Value("${server.url}") String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @GetMapping("/")
        public ResponseEntity<String> getSlash() {
            return restTemplate.getForEntity(baseUrl + "/foo", String.class);
        }
    }

    @SpringBootApplication
    public static class App {
        public static void main(String[] args) {
            SpringApplication.run(App.class, args);
        }
    }

    @Nested
    @SpringBootTest(classes = {RestApi.class, App.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    class InnerSpringTest {
        @LocalServerPort
        private int serverPort;

        @Test
        void canReachServiceThatTalksToWiremock() {
            given()
                .baseUri("http://localhost:" + serverPort)
            .when()
                .get("/")
            .then()
                .statusCode(200)
                .body(is("from wiremock"));

        }

        @Test
        void canSeeLoggingOutputInSystemOutObject() {
            assertThat(systemOut.getText())
                .contains(":: Spring Boot ::               (v2.7.15)");
        }
    }
}
