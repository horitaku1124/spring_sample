import docker
import os, sys


os.chdir("../")
print ("Current working dir : %s" % os.getcwd())
pwd = os.getcwd()

client = docker.from_env()
container = client.containers.create('openjdk:8',
                                        command='/bin/bash',
                                         tty=True,
                                         stdin_open=True,
                                         auto_remove=False,
                                         volumes = {
                                             pwd: {'bind': '/mnt/spring_sample', 'mode': 'rw'}
                                         })

container.start()  
print('Container Started : {}'.format(container.status))

print('1')
log = container.exec_run('ln -s /mnt/spring_sample/_maven_home /root/.m2',
                                  stdout=True,
                                  stderr=True,
                                  stream=True)

for line in log[1]:
    print(line)


print('2')
log = container.exec_run('chmod +x mvnw',
                                  workdir="/mnt/spring_sample/SpringWebSample1",
                                  stdout=True,
                                  stderr=True,
                                  stream=True)

for line in log[1]:
    print(line)

print('3')
log = container.exec_run('chmod +x /mnt/spring_sample/docker/build.sh',
                                  workdir="/mnt/spring_sample/SpringWebSample1",
                                  stdout=True,
                                  stderr=True,
                                  stream=True)

for line in log[1]:
    print(line)

env = {
    "JAVA_HOME": "/docker-java-home",
}


print('7')
log = container.exec_run('./mvnw -e install -Dmaven.test.skip=true',
                                  workdir="/mnt/spring_sample/SpringWebSample1",
                                  stdout=True,
                                  stderr=True,
                                  stream=True,
                                  environment = env)

for line in log[1]:
    print(str(line))

container.stop()
print(container.status)
container.remove()