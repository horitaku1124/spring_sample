package com.example.controler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
	@RequestMapping("/world")
	public String world() {
		return "Hello world 2017";
	}
}