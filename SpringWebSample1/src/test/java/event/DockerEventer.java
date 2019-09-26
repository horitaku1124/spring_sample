package event;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;

import java.nio.charset.StandardCharsets;

/**
 * https://github.com/spotify/docker-client/blob/master/docs/user_manual.md
 */
public class DockerEventer {
    protected DockerClient docker;
    protected ContainerCreation creation = null;
    public DockerEventer(String dockerCommand) throws DockerCertificateException, DockerException, InterruptedException {
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

        docker.startContainer(creation.id());

        final String logs;
        try (LogStream stream = docker.logs(creation.id(), DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr())) {
//            logs = stream.readFully();
            stream.forEachRemaining(s -> {
                int len = s.content().remaining();
                byte[] bytes = new byte[len];
                s.content().get(bytes);
                String str = new String(bytes, 0, len, StandardCharsets.UTF_8);
                System.out.print("Buff:" + str);
            });
        }
//        System.out.println(logs);
    }

    public void removeWhenFinish() throws DockerException, InterruptedException {
        docker.stopContainer(creation.id(), 10);
        docker.removeContainer(creation.id());
    }
}
