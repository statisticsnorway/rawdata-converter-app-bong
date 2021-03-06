.PHONY: default
default: | help

.PHONY: build-all
build-all: build-all-mvn build-docker ## Build all and create docker image (requires rawdata-converter-project)

.PHONY: build-all-mvn
build-all-mvn: ## Build all from parent (requires rawdata-converter-project)
	./mvnw -f ../pom.xml clean install

.PHONY: build-mvn
build-mvn: ## Build the project and install to you local maven repo
	./mvnw clean install

.PHONY: build-docker
build-docker: ## Build the docker image
	docker build -t rawdata-converter-app-bong:dev -f Dockerfile .

.PHONY: run-local
run-local: ## Run the app locally (without docker)
	java -Dcom.sun.management.jmxremote ${JAVA_OPTS} --enable-preview -Dmicronaut.environments=local-gcs -jar target/rawdata-converter-app-*.jar

.PHONY: release-dryrun
release-dryrun: ## Simulate a release in order to detect any issues
	./mvnw release:prepare release:perform -Darguments="-Dmaven.deploy.skip=true -Dmaven.javadoc.skip=true" -DdryRun=true

.PHONY: release
release: ## Release a new version. Update POMs and tag the new version in git. The pipeline deploys upon tag detection.
	./mvnw release:prepare release:perform -Darguments="-Dmaven.deploy.skip=true -Dmaven.javadoc.skip=true"

.PHONY: help
help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
