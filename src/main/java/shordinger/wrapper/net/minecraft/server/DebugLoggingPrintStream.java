package shordinger.wrapper.net.minecraft.server;

import java.io.OutputStream;

import shordinger.wrapper.net.minecraft.util.LoggingPrintStream;

public class DebugLoggingPrintStream extends LoggingPrintStream {

    public DebugLoggingPrintStream(String domainIn, OutputStream outStream) {
        super(domainIn, outStream);
    }

    protected void logString(String string) {
        StackTraceElement[] astacktraceelement = Thread.currentThread()
            .getStackTrace();
        StackTraceElement stacktraceelement = astacktraceelement[Math.min(3, astacktraceelement.length)];
        LOGGER.info(
            "[{}]@.({}:{}): {}",
            this.domain,
            stacktraceelement.getFileName(),
            Integer.valueOf(stacktraceelement.getLineNumber()),
            string);
    }
}
