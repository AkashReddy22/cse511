FROM ubuntu:22.04
LABEL author="Kethan Gaddam, Akash Reddy Maligireddy, Aishwarya Pullabhatla, Harshitha"

#install java 8
RUN apt-get update && \
	apt-get install software-properties-common -y && \
    add-apt-repository ppa:openjdk-r/ppa -y && \
    apt-get update && \
    apt-get install openjdk-8-jdk -y

ARG SBT_VERSION
ENV SBT_VERSION ${SBT_VERSION:-1.8.2}
ENV SCALA_VERSION	2.12.15
ENV SCALA_HOME		/usr/local/scala
ENV SPARK_HOME /usr/local/spark
ENV SPARK_VERSION	3.3.2
ENV SPARK_REPO spark-3.3.2-bin-hadoop2
ENV PATH		$SPARK_HOME/bin:$SCALA_HOME/bin:$SBT_HOME/bin:$PATH

#install scala
RUN apt-get install -y wget && \
	wget http://www.scala-lang.org/files/archive/scala-$SCALA_VERSION.deb && \
	apt-get remove scala-library scala && \
	dpkg -i  scala-$SCALA_VERSION.deb && \
	rm scala-$SCALA_VERSION.deb 

#install apache-spark
RUN wget http://downloads.apache.org/spark/spark-$SPARK_VERSION/$SPARK_REPO.tgz && \
	tar xvf $SPARK_REPO.tgz && \
	mv $SPARK_REPO /usr/local/spark && \
	rm $SPARK_REPO.tgz

#some util's 
RUN apt-get install -y vim
RUN apt-get install -y curl

# Install software
RUN apt-get update && apt-get install -y unzip

# install sbt
RUN \
  curl -fsL "https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.tgz" | tar xfz - -C /usr/share && \
  chown -R root:root /usr/share/sbt && \
  chmod -R 755 /usr/share/sbt && \
  ln -s /usr/share/sbt/bin/sbt /usr/local/bin/sbt

#Install Git
RUN apt-get install -y git

WORKDIR /root

RUN git clone https://github.com/AkashReddy22/cse511.git /root/cse511
WORKDIR /root/cse511

#RUN sbt assembly 

#RUN spark-submit target/scala-2.12/CSE511-assembly-0.1.0.jar result/output rangequery src/resources/arealm10000.csv -93.63173,33.0183,-93.359203,33.219456 rangejoinquery src/resources/arealm10000.csv src/resources/zcta10000.csv distancequery src/resources/arealm10000.csv -88.331492,32.324142 1 distancejoinquery src/resources/arealm10000.csv src/resources/arealm10000.csv 0.1

ENTRYPOINT ["/bin/bash"]
