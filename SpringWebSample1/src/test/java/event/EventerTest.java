package event;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static event.Eventer.Commander.CommanderBuilder.commands;

public class EventerTest {
    Eventer.Commander commander;
    @Before
    public void setup() {
        commander = commands("java", "-jar", "target/SpringWebSample1-0.0.1-SNAPSHOT.jar")
            .addStdoutReceiver(System.out::println)
            .build();
        commander.startAndWaitForBoot("Tomcat started");
    }
    @Test
    public void test1() {
        System.out.println("test1()");
    }
    @Test
    public void test2() {
        System.out.println("test2()");
    }

    @After
    public void cleanup() {
        commander.halt();
    }
}
