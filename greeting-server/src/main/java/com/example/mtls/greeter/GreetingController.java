package com.example.mtls.greeter;

import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/")
    public String time() {
        return "Hello the time is " + LocalDateTime.now();
    }

    @GetMapping("/greet/{name}")
    public Greeting greet(@PathVariable("name") String name)
    {
        return new Greeting( "hello " + name);
    }
}
