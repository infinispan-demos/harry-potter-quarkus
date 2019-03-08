# test fedora-minimal
FROM centos:7
WORKDIR /work/
COPY target/*-runner /work/application
COPY src/main/resources/hp_characters.csv /work/hp_characters.csv
COPY src/main/resources/hp_spells.csv /work/hp_spells.csv
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]