FROM jenkins/jenkins:centos7
USER root
#RUN apt update && \
#    apt install make pip -y && \
#    pip3 install docker-compose

RUN yum install make python3 -y && \ 
#    curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py 
    curl -L "https://github.com/docker/compose/releases/download/v2.0.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose
  


USER jenkins
#RUN  python3 get-pip.py && \
#     pip install docker-compose
 
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
ENV CASC_JENKINS_CONFIG /var/jenkins_home/
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
COPY seedjob.groovy /usr/local/seedjob.groovy 
COPY my_key.pem /var/jenkins_home/.ssh/my_key.pem
COPY casc.yaml /var/jenkins_home/casc.yaml


