package uk.org.webcompere.systemstubs.stream.alt;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static java.lang.System.lineSeparator;
import static org.assertj.core.api.Assertions.assertThat;

class TextAltStreamTest {
    @Test
    void textCanBeReadFromStream() {
        TextAltStream stream = new TextAltStream("Hello" + lineSeparator() + "World" + lineSeparator());
        Scanner scanner = new Scanner(stream);
        assertThat(scanner.nextLine()).isEqualTo("Hello");
        assertThat(scanner.nextLine()).isEqualTo("World");
    }
}
