package com.example.controler;

import com.example.entity.MemberEntity;
import com.example.repository.MemberRepository;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RestController
@RequestMapping("/hello")
public class HelloController {
  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private MemberRepository memberRepository;

    
  @RequestMapping("/world")
  public String world() {
    return "Hello world 2019";
  }
  @RequestMapping("/world2")
  public String world2() {
      StringBuilder title = new StringBuilder("Hello world 2019 - 2");
      Iterable<MemberEntity> all = memberRepository.findAll();
      for (MemberEntity member: all) {
          title.append(member.getName()).append("<br>");
      }
      return title.toString();
  }
    @RequestMapping("/world3")
    public String world3() {
        RedisClient redisClient = new RedisClient(
                RedisURI.create("redis://127.0.0.1:6379"));
        RedisConnection<String, String> connection = redisClient.connect();

        String value = connection.get("my_key_1");

        connection.close();
        redisClient.shutdown();
        return value;
    }

}