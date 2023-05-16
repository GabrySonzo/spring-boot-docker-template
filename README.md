# spring-boot-docker-template

mvn clean package##


##
docker build --pull --rm -f "Dockerfile" -t docker-spring:latest "."

##
docker run --rm -it -p 8888:8888/tcp docker-spring:latest
