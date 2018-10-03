package com.example.controller;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

@Sql("/db-schema.sql")
@Sql("/db-test-data.sql")
public class WelcomeControllerTest {
  @Autowired
  private WebApplicationContext context;
  private WebDriver driver;

  @Autowired
  private WebClient webClient;

  @Before
  public void setup() {
  }
  
  @Test
  public void testController() throws Exception {
    HtmlPage page = this.webClient.getPage("http://localhost/welcome");
    String text = page.getElementById("myMessage").getTextContent();
    assertThat(text, is("Message: Welcome Spring"));
  }
  
  @Test
  public void testController2() throws Exception {
    HtmlPage page = this.webClient.getPage("http://localhost/welcome2");
    String text = page.getElementById("myMessage2").getTextContent();
    assertThat(text, is("All: 1"));
  }
}