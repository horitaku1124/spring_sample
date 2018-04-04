package com.example.controler;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomController {
  private String message = "Welcome Spring";
  
  /**
   * shows welcome message.
   * 
   * @param model data
   * @return
   */
  @RequestMapping("/welcome")
  public String welcome(Map<String, Object> model) {
    model.put("message", this.message);
    return "welcome";
  }
}
