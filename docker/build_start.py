import docker
import os, sys, signal

started = False
os.chdir("../")
print ("Mount Dir: %s" % os.getcwd())
pwd = os.getcwd()

client = docker.from_env()
container = client.containers.create('openjdk:8',
                                        command='/bin/bash',
                                         tty=True,
                                         stdin_open=True,
                                         auto_remove=False,
                                         volumes = {
                                             pwd: {'bind': '/mnt/spring_sample', 'mode': 'rw'}
                                         },
                                         ports = {'8080/tcp': [8080]}
                                         )

container.start()  
print('Container Started : {}'.format(container.status))
started = True
workdir = "/mnt/spring_sample/SpringWebSample1"


env = {
    "JAVA_HOME": "/docker-java-home",
}

commands = [
    'ln -s /mnt/spring_sample/_maven_home /root/.m2',
    'chmod +x mvnw',
    './mvnw -e install -Dmaven.test.skip=true',
    './mvnw spring-boot:run',
]


def func(signum, frame):
    started = False
    print('Shutdown forced =>', signum)
    container.stop()
    print(container.status)
    container.remove()

signal.signal(signal.SIGINT, func)

for cmd in commands:
    print('>' + cmd)
    log = container.exec_run(cmd,
                            workdir=workdir,
                            stdout=True,
                            stderr=True,
                            stream=True,
                            environment=env)

    for line in log[1]:
        print(line.decode('UTF-8'), end='')

if started:
    container.stop()
    print(container.status)
    container.remove()