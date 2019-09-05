# GCP Project ID
# PROJECT_ID=$(shell gcloud config list project --format=flattened | awk 'FNR == 1 {print $$2}')

# AWS Account ID
ACCOUNT_ID=$(shell aws sts get-caller-identity --query Account --output text)

WHOAMI=`whoami`
PROJECT_NAME=auth

HOST=$(PROJECT_NAME).thanatopho.be
#HOST=localhost:8080

export JWT_AUTH_TOKEN=$(shell cat jwt.tmp)

run: package
	gradle bootRun


user-login:
	http POST http://$(HOST)/auth/signin username=user password=password -o jwtpacket.tmp
	@cat jwtpacket.tmp | jq .token -r > jwt.tmp
	@cat jwtpacket.tmp

admin-login:
	http POST http://$(HOST)/auth/signin username=admin password=password -o jwtpacket.tmp
	@cat jwtpacket.tmp | jq .token -r > jwt.tmp
	@cat jwtpacket.tmp

demo-login:
	http POST http://$(HOST)/auth/signin username=user3 password=daspassword -o jwtpacket.tmp
	@cat jwtpacket.tmp | jq .token -r > jwt.tmp
	@cat jwtpacket.tmp

get-login:
	http GET http://$(HOST)/me --auth-type=jwt

get-users:
	http GET http://$(HOST)/users --auth-type=jwt

get-user1:
	http GET http://$(HOST)/users/1 --auth-type=jwt

post-create-user:
	http POST http://$(HOST)/users --auth-type=jwt < demo/v1.json

post-reset-password:
	http POST http://$(HOST)/users/resetpassword/3 --auth-type=jwt

post-change-password:
	http POST http://$(HOST)/users/changepassword/3 --auth-type=jwt < demo/pwdchange.json





package:
	gradle bootJar
	docker build -t $(PROJECT_NAME) .

push: package
	-gcloud auth configure-docker
	docker tag auth:latest gcr.io/$(PROJECT_ID)/$(PROJECT_NAME):latest
	docker push gcr.io/$(PROJECT_ID)/$(PROJECT_NAME)

create-ecr:
	aws ecr create-repository --repository-name $(PROJECT_NAME)

push-ecr: package
	docker tag auth:latest $(ACCOUNT_ID).dkr.ecr.ap-southeast-2.amazonaws.com/$(PROJECT_NAME):latest
	docker push $(ACCOUNT_ID).dkr.ecr.ap-southeast-2.amazonaws.com/$(PROJECT_NAME):latest

deploy:
	kubectl set image deployment/auth auth=gcr.io/$(PROJECT_ID)/$(PROJECT_NAME):latest
	kubectl rollout status deployment auth

all: base build package
