all: build

build:
	./mvnw clean package

format:
	./mvnw com.coveo:fmt-maven-plugin:format

test:
	./mvnw test

postman-test:
	APIURL=http://localhost:8080/api ./api/run-api-tests.sh

run:
	./mvnw spring-boot:run -Dfmt.skip=true -Dcheckstyle.skip -Dpmd.skip=true -Dspotbugs.skip=true
