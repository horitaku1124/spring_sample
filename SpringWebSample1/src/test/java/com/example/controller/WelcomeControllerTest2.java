package com.example.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WelcomeControllerTest2 {

    @Autowired
    private TestRestTemplate restTemplate;
    @Test
    public void exampleTest() {
        String body = this.restTemplate.getForObject("/hello/world", String.class);
        assertThat(body, is("Hello world 2017"));
    }
}
