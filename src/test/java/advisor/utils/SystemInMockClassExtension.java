package advisor.utils;

import lombok.SneakyThrows;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SystemInMockClassExtension extends TextFromStandardInputStream implements
        BeforeAllCallback, AfterAllCallback {

    public SystemInMockClassExtension() {
        super("");
    }

    @SneakyThrows
    @Override
    public void beforeAll(ExtensionContext context)  {
        super.before();
    }

    @Override
    public void afterAll(ExtensionContext context)  {
        super.after();
    }
}

