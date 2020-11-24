package com.example.lib;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("Test the Greeter")
class GreeterTest {

  @Test
  void testGreeting() {
    var greeter = new Greeter();
    var greeting = greeter.greet("adib");
    Assertions.assertThat(greeting).isEqualTo("hello adib");
  }

}
