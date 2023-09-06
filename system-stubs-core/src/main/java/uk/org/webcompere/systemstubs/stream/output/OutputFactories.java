package uk.org.webcompere.systemstubs.stream.output;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.stream.Stream;

import static uk.org.webcompere.systemstubs.stream.output.Output.fromStream;

/**
 * Common output scenarios, creating an output factory or output.
 */
public class OutputFactories {

    /**
     * Construct an output made of multiple others with no dependency on the previous output stream
     * @param first the first output to multiplex
     * @param others the others
     * @return an {@link Output} which multiplexes
     */
    public static Output<MultiplexOutput> ofMultiple(Output<?> first, Output<?>... others) {
        return new MultiplexOutput(first, others);
    }

    /**
     * Construct an output made of multiple others with no dependency on the previous output stream
     * @param first the first output to multiplex
     * @param others the others
     * @return an {@link Output} which multiplexes
     */
    public static OutputFactory<MultiplexOutput> ofMultiple(OutputFactory<?> first,
                                                         OutputFactory<?>... others) {
        return original -> {
            Output<?>[] constructed = new Output<?>[others.length];
            for (int i = 0; i < others.length; i++) {
                constructed[i] = others[i].apply(original);
            }
            return new MultiplexOutput(first.apply(original), constructed);
        };
    }

    /**
     * Construct a multiplexed output made of the provided outputs, adding the original stream on the end.
     * @param first the first output to multiplex
     * @param others the others
     * @return an {@link OutputFactory} which produces a multiplexed output, which includes the previous setting for
     *      the <code>System.out</code> or <code>System.err</code> allowing a tap alongside the original
     */
    public static OutputFactory<MultiplexOutput> ofMultiplePlusOriginal(OutputFactory<?> first,
                                                                     OutputFactory<?>... others) {
        return ofMultiple(first, Stream.concat(Arrays.stream(others), Stream.of(Output::fromStream))
                .toArray(OutputFactory<?>[]::new));
    }

    /**
     * Construct a multiplexed output made of the provided outputs, adding the original stream on the end.
     * @param first the first output to multiplex
     * @param others the others
     * @return an {@link OutputFactory} which produces a multiplexed output, which includes the previous setting for
     *      the <code>System.out</code> or <code>System.err</code> allowing a tap alongside the original
     */
    public static OutputFactory<MultiplexOutput> ofMultiplePlusOriginal(Output<?> first,
                                                                     Output<?>... others) {
        return original ->
            new MultiplexOutput(first, Stream.concat(Arrays.stream(others), Stream.of(fromStream(original)))
                .toArray(Output[]::new));
    }


    /**
     * Tap an output while still using the original output
     * @return an {@link OutputFactory} which performs a tap
     */
    public static OutputFactory<MultiplexOutput> tapAndOutput() {
        return ofMultiplePlusOriginal(new TapStream());
    }

    /**
     * Write to file when the output is active, closing it when it's deactivated
     * @param file the target file for writing to
     */
    public static OutputFactory<FileOutputStream> writeToFile(File file) {
        return original -> Output.fromCloseableStream(new FileOutputStream(file));
    }
}
