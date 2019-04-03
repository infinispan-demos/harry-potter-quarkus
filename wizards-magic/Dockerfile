FROM centos:7
WORKDIR /work/
COPY target/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8081
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]