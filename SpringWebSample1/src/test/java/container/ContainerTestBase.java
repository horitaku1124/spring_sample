package container;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogMessage;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ExecCreation;
import com.spotify.docker.client.messages.PortBinding;
import org.junit.AfterClass;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

abstract public class ContainerTestBase {
  private static Thread thread = null;
  protected static DockerClient docker;
  protected static ContainerCreation creation = null;
  private static boolean started = false;

  @AfterClass
  public static void teardown() throws DockerException, InterruptedException {
    System.out.println(" ---- new teardown() ---- ");
    if (creation.id() != null) {
      docker.killContainer(creation.id());
      docker.removeContainer(creation.id());
    }
    docker.close();
  }

  static LogStream startContainer(ContainerConfig containerConfig, String... commands) throws DockerException, InterruptedException {
    // or use the builder
    docker = new DefaultDockerClient("unix:///Users/user/Library/Containers/com.docker.docker/Data/docker.sock");

    creation = docker.createContainer(containerConfig);

    docker.startContainer(creation.id());

    for (String command1: commands) {
      final String[] command = {"sh", "-c", command1};
      final ExecCreation execCreation = docker.execCreate(
          creation.id(), command, DockerClient.ExecCreateParam.attachStdout(),
          DockerClient.ExecCreateParam.attachStderr());
      final LogStream output = docker.execStart(execCreation.id());
      return output;
    }
    return null;
  }

  static String logToString(LogMessage logMessage) {
    ByteBuffer content = logMessage.content();
    byte[] buffer = new byte[content.remaining()];
    content.get(buffer);
    return new String(buffer, StandardCharsets.UTF_8);
  }

  static Map<String, List<PortBinding>> createSingleBind(String guestPort, String host, String hostPort) {
    Map<String, List<PortBinding>> portBindings = new HashMap<String, List<PortBinding>>() {{
      put(guestPort, asList(PortBinding.of(host, hostPort)));
    }};
    return portBindings;
  }

  static void waitFor(LogStream output, String str) throws InterruptedException {
    thread = new Thread() {
      @Override
      public void run() {
        StringBuffer sb = new StringBuffer();
        output.forEachRemaining(s -> {
          String converted = ContainerTestBase.logToString(s);
          System.out.print(converted);
          sb.append(converted);
          if (sb.toString().contains(str)) {
            started = true;
          }
        });
      }
    };
    thread.start();
    int limit = 120;
    while (limit-- > 0) {
      Thread.sleep(500);
      if (started) {
        return;
      }
    }
    throw new RuntimeException("timeout");
  }
}
