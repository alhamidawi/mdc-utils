# MDC Utils Library

**MDC Utils** is a lightweight Java library designed to simplify the management of MDC (Mapped Diagnostic Context) key-value pairs, which are commonly used in logging frameworks that support SLF4J's MDC API. This library provides an intuitive and resource-safe way to add, manage, and clean up MDC contexts, ensuring contextual logging in your applications is both structured and easy to maintain.

---

## Features
- **Simplified MDC Management**: Manage MDC key-value pairs with utility methods and fluent builders.
- **Resource Safety**: Automatically revert MDC changes when resources are closed.
- **Batch Key-Value Management**: Support for managing multiple key-value pairs collectively.
- **Integration with SLF4J**: Specifically designed for MDC use cases with SLF4J-compatible logging frameworks.

---

## Use Cases
The library is useful for managing contextual logging, such as:
- Tracking user sessions (e.g., user IDs, session IDs).
- Enriching logs with dynamic metadata (e.g., request IDs, correlation IDs).
- Automatically cleaning up MDC context after processing ends.

---

## Requirements
- **Java**: Requires Java 17 or later.
- **SLF4J MDC API**: Compatible with any SLF4J-compliant logging framework.

---

## Installation

Add the following dependency to your `pom.xml` if using Maven:

```xml
<dependency>
    <groupId>io.github.alhamidawi</groupId>
    <artifactId>mdc-utils</artifactId>
    <version>VERSION</version>
</dependency>
```

For Gradle users:

```groovy
implementation 'io.github.alhamidawi:mdc-utils:VERSION'
```

---

## Getting Started

### Adding a Single Key-Value Pair to MDC
To add and manage a single key-value pair inside the MDC context:

```java
try (var resource = MdcExtras.with("key", "value")) {
    // Perform operations with MDC context
    // The key-value pair is available in the MDC context
}
// Automatically reverts MDC state when the block exits
```

### Adding Multiple Key-Value Pairs Using a Builder
To add and manage multiple MDC key-value pairs:

```java
try (var resources = MdcExtras.builder()
        .add("userId", 12345)
        .add("requestId", "abcd1234")
        .build()) {
    // MDC contains userId and requestId
}
// Automatically reverts all MDC state after the block exits
```

---

## Example: Contextual Logging
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.alhamidawi.mdcutils.MdcExtras;

public class Example {
    private static final Logger logger = LoggerFactory.getLogger(Example.class);

    public static void main(String[] args) {
        try (var mdc = MdcExtras.builder()
                .add("userId", "user123")
                .add("sessionId", "session456")
                .build()) {
            logger.info("Processing request with contextual information.");
        }
        // MDC context is cleaned up automatically here.
    }
}
```

Example output in logs (assuming the MDC configuration is set up):

```text
INFO [main] user123 session456 - Processing request with contextual information.
```

## Advantages
- **Resource Management**: Automatic handling of MDC cleanup; no need to manually manage the context.
- **Thread Safety**: Works seamlessly with MDC's thread-local storage.
- **Error-Safe**: Utilizes `try-with-resources` for clean and reliable resource management.
- **Fluent API**: Provides an intuitive and easy-to-read code structure.

---

## Contributing
Contributions are always welcome! Feel free to open issues or submit pull requests on the [GitHub repository](https://github.com/alhamidawi/mdc-utils).

---

## License
This library is licensed under the **Apache License 2.0**. See the `LICENSE` file for more details.