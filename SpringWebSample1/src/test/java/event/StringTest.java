package event;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringTest {
    @Test
    public void test1() {
        String str = "docker run hello-world";
        String[] bow = str.split(" ");
        assertThat(bow.length, is(3));
        assertThat(bow[0], is("docker"));
        assertThat(bow[1], is("run"));
        assertThat(bow[2], is("hello-world"));

        str = "docker   run    hello-world";
        bow = str.split(" +");
        assertThat(bow.length, is(3));
        assertThat(bow[0], is("docker"));
        assertThat(bow[1], is("run"));
        assertThat(bow[2], is("hello-world"));

        str = "docker-compose";
        bow = str.split(" +");
        assertThat(bow.length, is(1));
        assertThat(bow[0], is("docker-compose"));
    }
}
