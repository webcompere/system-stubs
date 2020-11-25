package uk.org.webcompere.systemstubs.stream.output;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import java.io.File;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.stream.SystemStreamBase.wrap;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.*;

/**
 * Also examples for how to both tap and allow System output
 */
class OutputFactoriesTest {
    @Test
    void whenTapAndConsoleThenTapWorks() throws Exception {
        SystemOut systemOut = new SystemOut(tapAndOutput());
        systemOut.execute(() -> System.out.println("I write to the console and the tap"));
        assertThat(systemOut.getLines()).containsExactly("I write to the console and the tap");
    }

    @Test
    void whenTapAndConsoleThenConsoleWorks() throws Exception {
        SystemOut pretendToBeConsole = new SystemOut();
        pretendToBeConsole.execute(() -> {
            // here we're pretending that the outer SystemOut is the ACTUAL system out
            // it's SystemOut all the way down!
            SystemOut systemOut = new SystemOut(tapAndOutput());
            systemOut.execute(() -> System.out.println("I write to the console and the tap"));
            assertThat(systemOut.getLines()).containsExactly("I write to the console and the tap");
        });

        assertThat(pretendToBeConsole.getLines()).containsExactly("I write to the console and the tap");
    }

    @Test
    void canDirectSystemOutToFile(@TempDir File tempDir) throws Exception {
        File target = new File(tempDir, "file");
        new SystemOut(writeToFile(target))
            .execute(() -> {
               System.out.println("This is going into a file");
            });

        assertThat(target).hasContent("This is going into a file" + System.lineSeparator());
    }

    @Test
    void canDirectSystemOutToFileAndConsole(@TempDir File tempDir) throws Exception {
        File target = new File(tempDir, "file");
        new SystemOut(ofMultiplePlusOriginal(writeToFile(target)))
            .execute(() -> {
                System.out.println("This is going into a file");
            });

        assertThat(target).hasContent("This is going into a file" + System.lineSeparator());
    }

    @Test
    void canDirectSystemOutToFileAndConsoleAndTap(@TempDir File tempDir) throws Exception {
        File target = new File(tempDir, "file");
        SystemOut systemOut = new SystemOut(ofMultiplePlusOriginal(new TapStream().factoryOfSelf(), writeToFile(target)));
        systemOut.execute(() -> {
                System.out.println("This is going into a file");
            });

        assertThat(target).hasContent("This is going into a file" + System.lineSeparator());
        assertThat(systemOut.getLines()).containsExactly("This is going into a file");
    }

    @Test
    void canComposeMultiplex() throws Exception {
        TapStream tapStream1 = new TapStream();
        TapStream tapStream2 = new TapStream();
        Output<MultiplexOutput> multiplexOutput = ofMultiple(tapStream1, tapStream2);

        PrintStream printer = wrap(multiplexOutput.getOutputStream());
        printer.println("Foo");

        assertThat(multiplexOutput.getLines()).containsExactly("Foo");
        assertThat(tapStream1.getLines()).containsExactly("Foo");
        assertThat(tapStream2.getLines()).containsExactly("Foo");
    }

    @Test
    void canComposeMultiplexOfFilesClosedByOutputClose(@TempDir File tempDir) throws Exception {
        File file1 = new File(tempDir, "file1");
        File file2 = new File(tempDir, "file2");
        Output<MultiplexOutput> multiplexOutput = ofMultiple(writeToFile(file1), writeToFile(file2))
            .apply(null);

        PrintStream printer = wrap(multiplexOutput.getOutputStream());
        printer.println("Foo");

        // close it as though it's a stream
        multiplexOutput.getOutputStream().closeOutput();

        assertThat(file1).hasContent("Foo" + System.lineSeparator());
        assertThat(file2).hasContent("Foo" + System.lineSeparator());
    }

    @Test
    void canComposeMultiplexOfFilesClosedByOutputStreamClose(@TempDir File tempDir) throws Exception {
        File file1 = new File(tempDir, "file1");
        File file2 = new File(tempDir, "file2");
        Output<MultiplexOutput> multiplexOutput = ofMultiple(writeToFile(file1), writeToFile(file2))
            .apply(null);

        PrintStream printer = wrap(multiplexOutput.getOutputStream());
        printer.println("Foo");

        // close it as though it's a stream
        multiplexOutput.getOutputStream().close();

        assertThat(file1).hasContent("Foo" + System.lineSeparator());
        assertThat(file2).hasContent("Foo" + System.lineSeparator());
    }
}
