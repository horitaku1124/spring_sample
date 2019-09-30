package event;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * https://github.com/spotify/docker-client/blob/master/docs/user_manual.md
 */
public class DockerEventer {
    private final DockerClient docker;
    protected ContainerCreation creation;
    private List<Consumer<String>> stdoutReceivers = new ArrayList<>();
    private Thread mainThread;
    private Thread commandThread;
    public List<String> commands;
    private static Map<String, List<PortBinding>> createSingleBind(String guestPort, String host, String hostPort) {
        return new HashMap<>() {{
            put(guestPort, asList(PortBinding.of(host, hostPort)));
        }};
    }

    public DockerEventer(ContainerConfig.Builder builder1) throws DockerCertificateException, DockerException, InterruptedException {
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
        int timeout = 1000;
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

        if (commands.size() > 0) {
            commandThread = new Thread(() -> {
                try {
                    for (String cmd: commands) {
                        System.out.println("cmd=" + cmd);
                        final String[] command = {"sh", "-c", cmd};
                        final ExecCreation execCreation;
                            execCreation = docker.execCreate(
                                    creation.id(), command, DockerClient.ExecCreateParam.attachStdout(),
                                    DockerClient.ExecCreateParam.attachStderr());

                        final LogStream output = docker.execStart(execCreation.id());
                        output.forEachRemaining(s -> {
                            int len = s.content().remaining();
                            byte[] bytes = new byte[len];
                            s.content().get(bytes);
                            String str = new String(bytes, 0, len, StandardCharsets.UTF_8);
                            stdoutReceivers.forEach(consumer -> {
                                consumer.accept(str);
                            });
                        });
                    }
                } catch (DockerException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            commandThread.start();
        }
    }

    public void removeWhenFinish() throws DockerException, InterruptedException {
        docker.stopContainer(creation.id(), 10);
        docker.removeContainer(creation.id());
        docker.close();
    }

    public static class DockerEventerOption {
        private ContainerConfig.Builder containerBuilder;
        private List<String> commands = new ArrayList<>();
        private List<Consumer<String>> stdoutReceivers = new ArrayList<>();

        public DockerEventerOption addStdoutReceiver(Consumer<String> receiver) {
            this.stdoutReceivers.add(receiver);
            return this;
        }

        public void addCommand(String command) {
            commands.add(command);
        }

        public DockerEventer build() throws InterruptedException, DockerException, DockerCertificateException {
            DockerEventer dockerEventer = new DockerEventer(containerBuilder);
            dockerEventer.stdoutReceivers.addAll(stdoutReceivers);
            dockerEventer.commands = commands;
            return dockerEventer;
        }
    }

    public static class DockerEventerBuilder {
        public static DockerEventerOption command(String dockerCommand) {
            String[] bow = dockerCommand.split(" +");
            ContainerConfig.Builder containerBuilder = ContainerConfig.builder();
            HostConfig.Builder hostBuilder = HostConfig.builder();
            DockerEventerOption option = new DockerEventerOption();
            switch (bow[0]) {
                case "docker":
                    String operation = bow[1];
                    switch (operation) {
                        case "run":
                            var mandatoryOptions = new ArrayList<String>();
                            boolean optionArea = true;
                            for (int i = 2;i < bow.length;i++) {
                                var op = bow[i];
                                if (optionArea && op.startsWith("-")) {
                                    // use TTY
                                    if (op.contains("t")) {

                                    }
                                    // Interactive
                                    if (op.contains("i")) {

                                    }
                                    // Volume mount
                                    if (op.contains("v")) {
                                        var volume = bow[++i];
                                        hostBuilder.appendBinds(volume);
                                    }
                                    // Publish port
                                    if (op.contains("p")) {
                                        var portBind = bow[++i];
                                        var ports = portBind.split(":");
                                        hostBuilder.portBindings(createSingleBind(ports[1], "0.0.0.0", ports[0]));
                                        containerBuilder.exposedPorts(ports[1]);
                                    }
                                } else {
                                    mandatoryOptions.add(op);
                                    optionArea = false;
                                }
                            }
                            containerBuilder.image(mandatoryOptions.get(0));
                            if (mandatoryOptions.size() > 1) {
                                var initialCommand = new String[mandatoryOptions.size() - 1];
                                for (int i = 0;i < initialCommand.length;i++) {
                                    initialCommand[i] = mandatoryOptions.get(i + 1);
                                }
                                containerBuilder.cmd("sh", "-c", "while :; do sleep 1; done");
                                option.addCommand(String.join(" ", initialCommand));
                            }
                            break;
                        default:
                            throw new RuntimeException("ng");
                    }
                    break;
                default:
                    throw new RuntimeException("ng");
            }
            containerBuilder.hostConfig(hostBuilder.build());
            option.containerBuilder = containerBuilder;
            return option;
        }
    }
}
