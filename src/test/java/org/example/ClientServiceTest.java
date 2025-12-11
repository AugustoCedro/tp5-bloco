package org.example;

import net.jqwik.api.*;
import org.example.model.Client;
import org.example.service.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {

    ClientService service = new ClientService();

    @Provide
    Arbitrary<String> randomEmails() {

        Arbitrary<String> validEmails =
                Combinators.combine(
                        Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
                        Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
                        Arbitraries.of("com", "org", "net", "dev", "io")
                ).as((user, domain, tld) -> user + "@" + domain + "." + tld);

        Arbitrary<String> garbageEmails =
                Arbitraries.strings()
                        .withChars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&*()+=/?;:.,<>[]{}~ ")
                        .ofMinLength(0)
                        .ofMaxLength(30);

        Arbitrary<String> edgeCases = Arbitraries.of(
                null, "", " ", "a@", "@b.com", "a@b", "a@b.", "a@.com"
        );

        return Arbitraries.oneOf(validEmails, garbageEmails, edgeCases);
    }

    @Property
    void fuzzTestEmailValidation(@ForAll("randomEmails") String email) {

        int currentId = 1;

        try {
            boolean result = service.isEmailValid(email, currentId);

            Assertions.assertTrue(
                    email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"),
                    "Invalid email accepted: " + email
            );

            if ("existe@teste.com".equalsIgnoreCase(email)) {
                Assertions.assertTrue(result);
            }

        } catch (IllegalArgumentException e) {

            boolean validFormat =
                    email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

            assertFalse(validFormat, "Valid email rejected: " + email + " | Error: " + e.getMessage());
        }
    }

    //Simulacao de timeout
    @Test
    void simulateTimeoutTest() {
        ClientService service = new ClientService();
        assertTimeout(Duration.ofMillis(400), service::simulateNetworkCall);
    }
    //Simulacao de entrada maliciosa
    @Test
    void rejectMaliciousEntryTest() {
        ClientService service = new ClientService();
        Client c = new Client("'; DROP TABLE clients; --", "ataque@hack.com");

        assertThrows(IllegalArgumentException.class, () -> service.createClient(c));
    }

    @Test
    void simulateOverloadTest() {
        ClientService service = new ClientService();
        for (int i = 0; i < 1000; i++) {
            Client c = new Client("User" + i, "u" + i + "@email.com");
            service.createClient(c);
        }
        assertEquals(1010, service.getClients().size());
    }
}
