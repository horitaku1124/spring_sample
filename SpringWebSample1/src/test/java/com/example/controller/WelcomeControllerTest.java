package com.example.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import javafx.scene.web.WebEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.gargoylesoftware.htmlunit.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.*;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
    MockMvc mvc = MockMvcBuilders.webAppContextSetup(context)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))
        .alwaysDo(log())
        .build();
//    driver = MockMvcHtmlUnitDriverBuilder.mockMvcSetup(mvc).build();
//    HtmlPage page = this.webClient.getPage("/sboot/vehicle.html");
//    assertThat(page.getBody().getTextContent()).isEqualTo("Honda Civic");

  }
  
  @Test
  public void testController() throws Exception {
//    driver.get("http://localhost/welcome");
//
//    String text = driver.findElement(By.id("myMessage")).getText();
//    assertThat(text, is("Message: Welcome Spring"));
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