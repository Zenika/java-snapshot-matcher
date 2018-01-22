# Snapshot testing with Java
_Avoid serial assertions when testing serializable objects._

## Simple usage
```java
@Test
public void test() {
    String s = "StringToTest";

    assertThat(s, matchesSnapshot());
}
```
On the first execution of this test, it will generate a snapshot file in resources folder. This file looks like this:
```json
"StringToTest"
```
This file **must be committed**.
Next executions of this test will read said file and compare with actual value.

Assertion will fail if serialized values are different.

We use [Jackson](https://github.com/FasterXML/jackson) to handle serialization/deserialization to JSON format.
This library therefore can handle any object that is serializable by Jackson.

## Limitations
- Only one snapshot assertion per test method
