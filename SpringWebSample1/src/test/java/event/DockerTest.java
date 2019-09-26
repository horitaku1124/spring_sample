package event;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.Test;

public class DockerTest {
    @Test
    public void test1() throws InterruptedException, DockerException, DockerCertificateException {
        DockerEventer dockerEventer = new DockerEventer("docker run hello-world");
        dockerEventer.removeWhenFinish();
    }
}
