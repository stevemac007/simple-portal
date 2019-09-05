export JWT_AUTH_TOKEN=$(shell cat jwt.tmp)
BUILD_VERSION := $(shell date +%Y-%m-%d-%H%M%S)

cibuild:
	@echo "Building version '$(BUILD_VERSION)'"
	(cd auth-service && gradle bootJar)
	(cd team-service && gradle bootJar)
	docker-compose build

cipublish:
	@echo "Building version '$(BUILD_VERSION)'"
	(cd auth-service && make push-ci-ecr)
	(cd team-service && make push-ci-ecr)

deps:
	pip install -U httpie-jwt-auth

clean:
	(cd auth-service && gradle clean)
	(cd team-service && gradle clean)

start:
	(cd auth-service && gradle bootJar)
	(cd team-service && gradle bootJar)
	docker-compose build
	docker-compose up

user-login:
	http POST http://localhost:8080/auth/signin username=user password=password -o jwtpacket.tmp
	@cat jwtpacket.tmp | jq .token -r > jwt.tmp
	@cat jwtpacket.tmp

admin-login:
	http POST http://localhost:8080/auth/signin username=admin password=password -o jwtpacket.tmp
	@cat jwtpacket.tmp | jq .token -r > jwt.tmp
	@cat jwtpacket.tmp

get-login:
	http GET http://localhost:8080/me --auth-type=jwt

get-demo:
	http GET http://localhost:8081/vehicles --auth-type=jwt

post-demo1:
	http POST http://localhost:8081/vehicles --auth-type=jwt < demo/v1.json

post-demo2:
	http POST http://localhost:8081/vehicles --auth-type=jwt < demo/v2.json

delete-demo22:
	http DELETE http://localhost:8081/vehicles/22 --auth-type=jwt < demo/v2.json

post-lots:
	for x in `seq 1 10`; do \
	http POST http://localhost:8081/vehicles --auth-type=jwt < demo/v1.json ; \
	http POST http://localhost:8081/vehicles --auth-type=jwt < demo/v2.json ; \
	done

get-page2:
	http GET "http://localhost:8081/vehicles?page=1" --auth-type=jwt
