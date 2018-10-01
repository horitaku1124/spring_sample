package com.example.controler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.example.repositories.SampleTableRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomController {
  private String message = "Welcome Spring";

  @Autowired
  private SampleTableRepository sampleTable;
  
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

  @RequestMapping("/welcome2")
  public String welcome2(Map<String, Object> model) {
    model.put("all_count", "All: " + sampleTable.findAll().size());
    return "welcome2";
  }
}
