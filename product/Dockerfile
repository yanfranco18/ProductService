FROM openjdk:11.0-oracle
VOLUME /tmp
EXPOSE 8093
ADD ./target/product-0.0.1-SNAPSHOT.jar service-product.jar
ENTRYPOINT ["java","-jar","/service-product.jar"]