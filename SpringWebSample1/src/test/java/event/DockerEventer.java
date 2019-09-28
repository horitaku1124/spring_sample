package event;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * https://github.com/spotify/docker-client/blob/master/docs/user_manual.md
 */
public class DockerEventer {
    private final DockerClient docker;
    protected ContainerCreation creation;
    private List<Consumer<String>> stdoutReceivers = new ArrayList<>();
    private Thread mainThread;
    public DockerEventer(ContainerConfig.Builder builder1) throws DockerCertificateException, DockerException, InterruptedException {


//        HostConfig hostConfig = HostConfig.builder()
//                .appendBinds("/var/lib:/mnt/lib")
//                .portBindings(createSingleBind(port, "0.0.0.0", hostPort))
//                .build();

//        ContainerConfig containerConfig = ContainerConfig.builder()
//                .image("spring_sample1:latest")
//                .cmd("sh", "-c", "while :; do sleep 1; done")
//                .hostConfig(hostConfig)
//                .exposedPorts(port)
//                .build();

        DefaultDockerClient.Builder builder = DefaultDockerClient.fromEnv();
        docker = builder.build();
        creation = docker.createContainer(builder1.build());
    }
    public void startAndWaitForBoot(String pattern) throws DockerException, InterruptedException {
        Pattern pattern1 = Pattern.compile(pattern);
        final boolean[] started = {false};
        this.stdoutReceivers.add(s -> {
            if (pattern1.matcher(s).find()) {
                System.out.println(" booted");
                started[0] = true;
            }
        });
        start();
        int timeout = 100;
        while(mainThread.isAlive()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (started[0]) {
                break;
            }
            timeout--;
            if (timeout < 0) {
                throw new RuntimeException("timeout to start");
            }
        }
    }

    public void start() throws DockerException, InterruptedException {
        docker.startContainer(creation.id());
        if (stdoutReceivers.isEmpty()) {
            return;
        }
        mainThread = new Thread(() -> {
            try (LogStream stream = docker.logs(creation.id(), DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr())) {
                stream.forEachRemaining(s -> {
                    int len = s.content().remaining();
                    byte[] bytes = new byte[len];
                    s.content().get(bytes);
                    String str = new String(bytes, 0, len, StandardCharsets.UTF_8);
                    stdoutReceivers.forEach(consumer -> {
                        consumer.accept(str);
                    });
                });
            } catch (InterruptedException | DockerException e) {
                e.printStackTrace();
            }
        });
        mainThread.start();
    }

    public void removeWhenFinish() throws DockerException, InterruptedException {
        docker.stopContainer(creation.id(), 10);
        docker.removeContainer(creation.id());
        docker.close();
    }

    public static class DockerEventerOption {
        private ContainerConfig.Builder builder1;
        private List<Consumer<String>> stdoutReceivers = new ArrayList<>();

        public DockerEventerOption addStdoutReceiver(Consumer<String> receiver) {
            this.stdoutReceivers.add(receiver);
            return this;
        }
        public DockerEventer build() throws InterruptedException, DockerException, DockerCertificateException {
            DockerEventer dockerEventer = new DockerEventer(builder1);
            dockerEventer.stdoutReceivers.addAll(stdoutReceivers);
            return dockerEventer;
        }
    }

    public static class DockerEventerBuilder {
        public static DockerEventerOption command(String dockerCommand) {
            String[] bow = dockerCommand.split(" +");
            ContainerConfig.Builder builder1 = ContainerConfig.builder();
            switch (bow[0]) {
                case "docker":
                    String operation = bow[1];
//                String option = bow[2];
                    String image = bow[2];
                    builder1.image(image);
//                String command = bow[4];
                    break;
                default:
                    throw new RuntimeException("ng");
            }
            DockerEventerOption option = new DockerEventerOption();
            option.builder1 = builder1;
            return option;
        }
    }
}
