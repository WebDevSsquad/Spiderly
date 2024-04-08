# JavaDoc Documentation

> [!TIP]
> Have a look at this **[JavaDoc documentation tutorial](https://www.tutorialspoint.com/java/java_documentation.htm)** for more information

## How to document classes

- Following is a simple example where the lines inside `/*…*/` are Java multi-line comments. Similarly, the line which preceeds `//` is Java single-line comment.

- The format of a documentation block is as the following:
  ```java
  /**
  * The HelloWorld program implements an application that
  * simply displays "Hello World!" to the standard output.
  *
  * @author  Zara Ali
  * @version 1.0
  * @since   2014-03-31 
  */
  public class HelloWorld {
  
     public static void main(String[] args) {
        // Prints Hello, World! on standard output.
        System.out.println("Hello World!");
     }
  }
  ```

- You can include required HTML tags inside the description part. For instance, the following example makes use of `<h1>....</h1>` for heading and `<p>` has been used for creating paragraph break −
  ```java
  /**
  * <h1>Hello, World!</h1>
  * The HelloWorld program implements an application that
  * simply displays "Hello World!" to the standard output.
  * <p>
  * Giving proper comments in your program makes it more
  * user friendly and it is assumed as a high quality code.
  * 
  *
  * @author  Zara Ali
  * @version 1.0
  * @since   2014-03-31 
  */
  public class HelloWorld {

     public static void main(String[] args) {
        // Prints Hello, World! on standard output.
        System.out.println("Hello World!");
     }
  }
  ```


### Tags
- `@param`: Describes a method parameter. It specifies the name of the parameter followed by a description of its purpose and any constraints.
  ```java
  /**
  * Calculates the sum of two integers.
  *
  * @param a The first integer.
  * @param b The second integer.
  * @return The sum of the two integers.
  */
  public int calculateSum(int a, int b) {
    return a + b;
  }
  ```

- `@return`: Describes the return value of a method. It provides information about the type of value returned and its significance.
  ```java
  /**
  * Calculates the sum of two integers.
  *
  * @param a The first integer.
  * @param b The second integer.
  * @return The sum of the two integers.
  */
  public int calculateSum(int a, int b) {
    return a + b;
  }
  ```

- `@throws`: Describes an exception that a method may throw. It specifies the type of exception followed by a description of when and why the exception might be thrown.
  ```java
  /**
   * Divides two numbers.
   *
   * @param dividend The number to be divided.
   * @param divisor  The number to divide by.
   * @return The result of the division.
   * @throws ArithmeticException if the divisor is zero.
   */
  public double divide(double dividend, double divisor) {
      if (divisor == 0) {
          throw new ArithmeticException("Division by zero");
      }
      return dividend / divisor;
  }
  ```

- `@see`: Provides a reference to another class, method, or field. It can be used to link to related documentation.
  ```java
  /**
   * Returns the square of the given number.
   *
   * @param num The number to square.
   * @return The square of the given number.
   * @see Math#pow(double, double)
   */
  public double square(double num) {
      return Math.pow(num, 2);
  }
  ```

- `@code`: Used to indicate that a specific portion of the documentation should be displayed in a monospaced font, typically used for code snippets.
  ```java
  /**
   * Returns the square of the given number.
   *
   * @param num The number to square.
   * @return The square of the given number.
   * The value of {@code num} squared is returned.
   */
  public double square(double num) {
      return num * num;
  }
  ```

- `@deprecated`: Marks a method or class as deprecated, indicating that it should no longer be used. It includes an optional description of why the element is deprecated and what to use instead.
  ```java
  /**
   * @deprecated Use {@link #square(double)} instead.
   */
  @Deprecated
  public double squareDeprecated(double num) {
      return num * num;
  }
  ```

### Additional tags

- `@author`: Specifies the author(s) of a class or method.
  ```java
  /**
   * Represents a person with a name and age.
   *
   * @author John Doe
   */
  public class Person {
      // Class implementation
  }
  ```

- `@version`: Specifies the version number of a class or method.
  ```java
  /**
   * Represents a product in the inventory.
   *
   * @version 1.0
   */
  public class Product {
      // Class implementation
  }
  ```

- `@since`: Indicates the version of the Java platform or library in which the element was introduced.
  ```java
  /**
   * Provides utility methods for working with strings.
   *
   * @since 1.5
   */
  public class StringUtils {
      // Class implementation
  }
  ```

- `@link`: Provides a hyperlink to another JavaDoc element, such as a class, method, or field.
  ```java
  /**
   * Returns the square of the given number.
   *
   * @param num The number to square.
   * @return The square of the given number.
   * @link Math#pow(double, double)
   */
  public double square(double num) {
      return Math.pow(num, 2);
  }
  ```

- `@inheritDoc`: Inherit documentation from the overridden or implemented method or class.
  ```java
  /**
   * {@inheritDoc}
   */
  @Override
  public void foo() {
      // Method implementation
  }
  ```

- `@linkplain`: Similar to `@link`, but doesn't use any special formatting for the link.
  ```java
  /**
   * Returns the square of the given number.
   *
   * @param num The number to square.
   * @return The square of the given number.
   * {@linkplain Math#pow(double, double)}
   */
  public double square(double num) {
      return Math.pow(num, 2);
  }
  ```

- `@literal`: Allows you to include literal text that should not be interpreted as a JavaDoc tag.
  ```java
  /**
   * Represents a {@literal <div>} element in HTML.
   */
  public class DivElement {
      // Class implementation
  }
  ```

- `@hidden`: Marks a class, method, or field as being hidden from the JavaDoc output.
  ```java
  Copy code
  /**
   * @hidden
   * Utility class for internal use only.
   */
  public class InternalUtils {
      // Class implementation
  }
  ```