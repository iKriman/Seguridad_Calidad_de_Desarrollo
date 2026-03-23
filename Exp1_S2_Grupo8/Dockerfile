FROM mysql/mysql-server:latest

ENV MYSQL_ROOT_PASSWORD=password
ENV MYSQL_DATABASE=veterinaria_db
ENV MYSQL_USER=veterinaria
ENV MYSQL_PASSWORD=admin123

COPY ./create.sql /docker-entrypoinr-initdb.d/