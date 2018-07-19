package com.juicelabs.icd.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream capture;
    private ServletOutputStream output;
    private PrintWriter writer;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        capture = new ByteArrayOutputStream(response.getBufferSize());
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (writer != null) {
            throw new IllegalStateException(
                    "getWriter() has already been called on this response.");
        }

        if (output == null) {
            output = new WrappedOutputStream();
        }

        return output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (output != null) {
            throw new IllegalStateException(
                    "getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(capture,
                                                            "UTF-8"));
        }

        return writer;
    }

    @Override
    public void addHeader(String name, String value) {
        if (!"Content-Length".equalsIgnoreCase(name)) {
            super.addHeader(name, value);
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();

        if (writer != null) {
            writer.flush();
        }
        if (output != null) {
            output.flush();
        }
    }

    public byte[] getCaptureAsBytes() throws IOException {
        if (writer != null) {
            writer.close();
        }

        if (output != null) {
            output.close();
        }

        return capture.toByteArray();
    }

    public String getCaptureAsString() throws IOException {
        return new String(getCaptureAsBytes(), "UTF-8");
    }

    /**
     * Actually writes the response to the client.
     */
    public void commitResponse(String transformedBody) throws IOException {
        getResponse().setContentLength(transformedBody.length());
        getResponse().getOutputStream().print(transformedBody);
    }

    /**
     * Actually writes the response to the client.
     */
    public void commitResponse(byte[] bytes) throws IOException {
        getResponse().getOutputStream().write(bytes);
    }

    private class WrappedOutputStream extends ServletOutputStream {
        @Override
        public void write(int b) throws IOException {
            capture.write(b);
        }

        @Override
        public void flush() throws IOException {
            capture.flush();
        }

        @Override
        public void close() throws IOException {
            capture.close();
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener arg0) {
            // noop
        }
    }
}
