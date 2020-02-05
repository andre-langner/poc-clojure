FROM clojure
RUN mkdir -p /usr/src/authorizer
WORKDIR /usr/src/authorizer
COPY project.clj /usr/src/authorizer/
RUN lein deps
COPY . /usr/src/authorizer
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" authorizer.jar
CMD ["java", "-jar", "authorizer.jar"]