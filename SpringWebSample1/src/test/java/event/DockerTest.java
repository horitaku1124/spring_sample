package event;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import static event.DockerEventer.DockerEventerOption;
import static event.DockerEventer.DockerEventerBuilder.command;
import org.junit.Test;

public class DockerTest {
    @Test
    public void test1() throws InterruptedException, DockerException, DockerCertificateException {
        DockerEventer eventer = command("docker run hello-world")
                .addStdoutReceiver(s -> System.out.print("S:" + s))
                .build();
        eventer.start();
        Thread.sleep(1000);
        eventer.removeWhenFinish();
    }
    @Test
    public void test2() throws InterruptedException, DockerException, DockerCertificateException {
        DockerEventer eventer = command("docker run hello-world")
                .addStdoutReceiver(s -> System.out.print("S2:" + s))
                .build();
        eventer.startAndWaitForBoot("get-started");
        eventer.removeWhenFinish();
    }
}
