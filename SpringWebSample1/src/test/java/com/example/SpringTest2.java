package com.example;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.controler.HelloController;
import com.example.entities.MyDataEntity;
import com.example.entities.SampleTableEntity;
import com.example.repositories.MyDataRepository;
import com.example.repositories.SampleTableRepository;

@RunWith(SpringJUnit4ClassRunner.class)
public class SpringTest2 {

  private MockMvc mvc;

  @InjectMocks
  private HelloController target;
  @Mock
  private SampleTableRepository repository;
  @Mock
  private MyDataRepository myData;
  
  @Before
  public void before() throws Exception {
    mvc = MockMvcBuilders.standaloneSetup(target).build();
  }
  
  @Test
  public void testGet__Ok() throws Exception {
    when(repository.findAll()).thenReturn(new ArrayList<SampleTableEntity>());
    when(myData.findAll()).thenReturn(new ArrayList<MyDataEntity>());

    mvc.perform(get("/hello/world"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello world 2017"));
    

    mvc.perform(get("/hello/world2"))
        .andExpect(status().isOk())
        .andExpect(content().string("HW2 table1=0 table2=0"));
  }

}
