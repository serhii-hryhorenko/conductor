package ua.edu.ukma.conductor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DefaultTestConfiguration {
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void afterEach() {
        Mockito.validateMockitoUsage();
    }
}
