package event;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Eventer {
    static class Commander {
        final String[] commands;
        final List<Consumer<String>> stdoutReceivers;
        final List<Consumer<String>> stdErrorReceivers;
        final List<Consumer> finishListener;
        final File workDir;
        public Commander(CommanderOption option) {
            this.commands = option.commands;
            this.stdoutReceivers = option.stdoutReceivers;
            this.stdErrorReceivers = option.stdErrorReceivers;
            this.finishListener = option.finishListener;
            this.workDir = option.workDir;
        }
        private Thread mainThread = null;
        Process child;
        private boolean keepDoing = false;
        public void start() {
            start(true);
        }
        public void startAndWaitForBoot(String pattern) {
            Pattern pattern1 = Pattern.compile(pattern);
            final boolean[] started = {false};
            this.stdoutReceivers.add(s -> {
                if (pattern1.matcher(s).find()) {
                    System.out.println(" booted");
                    started[0] = true;
                }
            });
            start(true);
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

        public void halt() {
            keepDoing = false;
            child.destroy();
        }

        public void start(final boolean callFinish) {
            keepDoing = true;
            final List<Consumer<String>> stdoutReceivers = this.stdoutReceivers;
            final List<Consumer<String>> stdErrorReceivers = this.stdErrorReceivers;
            final List<Consumer> finishListener = this.finishListener;
            final File workDir = this.workDir;
            mainThread = new Thread(() -> {
                try {
                    Runtime rt = Runtime.getRuntime();
                    child = workDir == null ? rt.exec(commands) : rt.exec(commands, null, workDir);
                    BufferedReader stdin = new BufferedReader(new InputStreamReader(child.getInputStream()));
                    BufferedReader stderr = new BufferedReader(new InputStreamReader(child.getErrorStream()));

                    while(child.isAlive() && keepDoing) {
                        if(stdin.ready()) {
                            String line = stdin.readLine();
                            for (Consumer<String> a: stdoutReceivers) {
                                a.accept(line);
                            }
                        }
                        if(stderr.ready()) {
                            String line = stderr.readLine();
                            for (Consumer<String> a: stdErrorReceivers) {
                                a.accept(line);
                            }
                        }
                        Thread.sleep(100);
                    }
                    if (callFinish) {
                        for (Consumer a: finishListener) {
                            a.accept(null);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            mainThread.start();
        }
        public void startAndWait() {
            start(false);
            while(mainThread.isAlive()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            for (Consumer a: finishListener) {
                a.accept(null);
            }
        }
        static class CommanderBuilder {
            public static CommanderOption commands(String... comm) {
                CommanderOption co = new CommanderOption();
                co.commands = comm;
                return co;
            }
        }
        static class CommanderOption {
            private String[] commands;
            private List<Consumer<String>> stdoutReceivers = new ArrayList<>();
            private List<Consumer<String>> stdErrorReceivers = new ArrayList<>();
            private List<Consumer> finishListener = new ArrayList<>();
            private File workDir;

            public CommanderOption addStdoutReceiver(Consumer<String> receiver) {
                this.stdoutReceivers.add(receiver);
                return this;
            }
            public CommanderOption addStdErrorReceiver(Consumer<String> stdErrorReceiver) {
                this.stdErrorReceivers.add(stdErrorReceiver);
                return this;
            }
            public CommanderOption addFinishListener(Consumer finishListener) {
                this.finishListener.add(finishListener);
                return this;
            }
            public CommanderOption setWorkDir(String path) {
                workDir = new File(path);
                return this;
            }
            public Commander build() {
                Commander commander = new Commander(this);
                return commander;
            }
        }
    }
}
