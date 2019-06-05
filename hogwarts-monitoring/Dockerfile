####
# This Dockerfile is used in order to build a container that runs the Quarkus application in native (no JVM) mode
#
# Before building the docker image run:
#
# mvn package -Pnative -Dnative-image.docker-build=true
#
# Then, build the image with:
#
# docker build -t karesti/hogwarts-monitoring .
#
# Then run the container using:
#
# docker run -i --rm -p 8081:8081 karesti/hogwarts-monitoring
#
###

FROM registry.fedoraproject.org/fedora-minimal
WORKDIR /work/
COPY target/*-runner /work/application
COPY src/main/resources/hp_characters.csv /work/hp_characters.csv
COPY src/main/resources/hp_spells.csv /work/hp_spells.csv
RUN chmod 775 /work
EXPOSE 8081
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]