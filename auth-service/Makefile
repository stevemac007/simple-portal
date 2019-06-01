PROJECT_ID=$(shell gcloud config list project --format=flattened | awk 'FNR == 1 {print $$2}')
export JWT_AUTH_TOKEN=$(shell cat jwt.tmp)

run:
	gradle bootRun


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

get-users:
	http GET http://localhost:8080/users --auth-type=jwt


post-demo1:
	http POST http://localhost:8080/users --auth-type=jwt < demo/v1.json

package:
	gradle bootJar
	docker build -t gcr.io/$(PROJECT_ID)/auth .

push: package
	-gcloud auth configure-docker
	docker push gcr.io/$(PROJECT_ID)/auth

deploy:
	kubectl set image deployment/auth auth=gcr.io/$(PROJECT_ID)/auth:latest
	kubectl rollout status deployment auth

all: base build package
