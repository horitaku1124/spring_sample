package container;


import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * https://github.com/spotify/docker-client
 */
@Ignore
public class Test1 extends ContainerTestBase {
  private final static String port = "8080";
  private final static String hostPort = "8180";

  private TestRestTemplate restTemplate = new TestRestTemplate();

  @BeforeClass
  public static void setupServer() throws DockerException, InterruptedException {
    HostConfig hostConfig = HostConfig.builder()
        .appendBinds("/var/lib:/mnt/lib")
        .portBindings(createSingleBind(port, "0.0.0.0", hostPort))
        .build();

    ContainerConfig containerConfig = ContainerConfig.builder()
        .image("spring_sample1:latest")
        .cmd("sh", "-c", "while :; do sleep 1; done")
        .hostConfig(hostConfig)
        .exposedPorts(port)
        .build();
    LogStream output = startContainer(containerConfig, "java -jar /root/SpringWebSample1-0.0.1-SNAPSHOT.jar");
    waitFor(output, "Tomcat started on port");

    System.out.println("tomcat started.");
  }

  @Test
  public void test11() {
    System.out.println(" -- test11() --");
    String result = restTemplate.getForObject("http://localhost:" + hostPort + "/hello/world", String.class);
    assertThat(result, is("Hello world 2017"));
  }
}
