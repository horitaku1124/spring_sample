package com.example.controler;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.example.repositories.SampleTableRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WelcomeController {
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

  @RequestMapping(value = "/uploadForm", method = RequestMethod.GET)
  public String uploadForm(Map<String, Object> model) {
    return "uploadForm";
  }
  @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
  public String upload(Map<String, Object> model,
                       @RequestParam("file") MultipartFile file,
                       @RequestParam("comment") String comment,
                       RedirectAttributes attr) throws IOException {
    if (file.isEmpty()) {
      attr.addFlashAttribute("message", "File is empty");
      return "redirect:uploadForm";
    }
    byte[] data = file.getBytes();
//    String body = new String(data, 0, data.length);
    model.put("file_size", data.length);
    model.put("comment", comment);
    System.out.println("comment=" + comment);
    return "uploadFile";
  }
}
