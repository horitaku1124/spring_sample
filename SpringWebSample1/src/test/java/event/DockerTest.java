package event;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.Test;

import static event.DockerEventer.DockerEventerBuilder.command;

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
    @Test
    public void test3() throws InterruptedException, DockerException, DockerCertificateException {
        DockerEventer eventer = command("docker run -it hello-world")
                .addStdoutReceiver(s -> System.out.print("S3:" + s))
                .build();
        eventer.startAndWaitForBoot("get-started");
        eventer.removeWhenFinish();
    }
    @Test
    public void test4() throws InterruptedException, DockerException, DockerCertificateException {
        DockerEventer eventer = command("docker run -it -p 8180:8080 spring_sample1 java -jar /root/SpringWebSample1-0.0.1-SNAPSHOT.jar")
                .addStdoutReceiver(s -> System.out.print("S4:" + s))
                .build();
        eventer.startAndWaitForBoot("Tomcat started on port");
        Thread.sleep(20 * 1000);
        eventer.removeWhenFinish();
    }
}
