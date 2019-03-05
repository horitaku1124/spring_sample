package container;


import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * https://github.com/spotify/docker-client
 */
@Ignore
public class Test1 {
  private static DockerClient docker;
  private static String containerId = null;
  private static Thread thread = null;
  private static boolean started = false;

  private TestRestTemplate restTemplate = new TestRestTemplate();

  @BeforeClass
  public static void setupServer() throws DockerException, InterruptedException {
    final String port = "8080";

    // or use the builder
    docker = new DefaultDockerClient("unix:///Users/user/Library/Containers/com.docker.docker/Data/docker.sock");

    final Map<String, List<PortBinding>> portBindings = new HashMap<>();
    List<PortBinding> hostPorts = new ArrayList<>();
    hostPorts.add(PortBinding.of("0.0.0.0", port));
    portBindings.put(port, hostPorts);

    final HostConfig hostConfig = HostConfig.builder()
        .appendBinds("/var/lib:/mnt/lib")
        .portBindings(portBindings)
        .build();

    final ContainerConfig containerConfig = ContainerConfig.builder()
        .image("spring_sample1:latest")
        .cmd("sh", "-c", "while :; do sleep 1; done")
        .hostConfig(hostConfig)
        .exposedPorts(port)
        .build();

    final ContainerCreation creation = docker.createContainer(containerConfig);
    containerId = creation.id();

    docker.startContainer(containerId);

    final String[] command = {"sh", "-c", "java -jar /root/SpringWebSample1-0.0.1-SNAPSHOT.jar"};
    final ExecCreation execCreation = docker.execCreate(
        containerId, command, DockerClient.ExecCreateParam.attachStdout(),
        DockerClient.ExecCreateParam.attachStderr());
    final LogStream output = docker.execStart(execCreation.id());


    thread = new Thread() {
      @Override
      public void run() {
        StringBuffer sb = new StringBuffer();
        output.forEachRemaining(s -> {
          ByteBuffer content = s.content();
          byte[] buffer = new byte[content.remaining()];
          content.get(buffer);
          String converted = new String(buffer, StandardCharsets.UTF_8);
          System.out.print(converted);
          sb.append(converted);
          if (sb.toString().contains("Tomcat started on port")) {
            started = true;
          }
        });
      }
    };
    thread.start();
    while (!started) {
      Thread.sleep(1000);
    }
    System.out.println("tomcat started.");
  }

  @Test
  public void test11() {
    System.out.println(" -- test11() --");
    String result = restTemplate.getForObject("http://localhost:8080/hello/world", String.class);
    assertThat(result, is("Hello world 2017"));
  }

  @AfterClass
  public static void teardown() throws DockerException, InterruptedException {
    if (containerId != null) {
      docker.killContainer(containerId);
      docker.removeContainer(containerId);
    }
    docker.close();
  }
}
