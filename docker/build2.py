import docker
import os, sys, signal

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
    'ls /mnt/spring_sample/SpringWebSample1/target/',
    'ls target/SpringWebSample1-0.0.1-SNAPSHOT.jar',
    'cp target/SpringWebSample1-0.0.1-SNAPSHOT.jar /root',
]



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


image = container.commit("spring_sample1")
print (image.id)

container.stop()
print(container.status)
container.remove()