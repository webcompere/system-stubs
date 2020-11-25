package uk.org.webcompere.systemstubs.stream.output;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * A composite output which directs to multiple targets. It reads text from only the first. It is, itself
 * an {@link OutputStream}
 */
public class MultiplexOutput extends OutputStream implements Output<MultiplexOutput> {
    private Output<?>[] outputs;

    /**
     * Construct with a variable number of outputs to multiplex to
     * @param first the first output
     * @param others additional outputs
     */
    public MultiplexOutput(Output<?> first, Output<?>... others) {
        outputs = Stream.concat(Stream.of(first), Arrays.stream(others))
            .toArray(Output[]::new);
    }

    @Override
    public void write(int b) throws IOException {
        for (Output<?> output : outputs) {
            output.getOutputStream().write(b);
        }
    }

    @Override
    public String getText() {
        return outputs[0].getText();
    }

    @Override
    public void clear() {
        for (Output<?> output : outputs) {
            output.clear();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            closeOutput();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void closeOutput() throws Exception {
        for (Output<?> output : outputs) {
            output.closeOutput();
        }
    }

    @Override
    public MultiplexOutput getOutputStream() {
        return this;
    }
}
