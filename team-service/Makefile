PROJECT_ID=$(shell gcloud config list project --format=flattened | awk 'FNR == 1 {print $$2}')
ACCOUNT_ID=$(shell aws sts get-caller-identity --query Account --output text)

WHOAMI=$(shell whoami)
PROJECT_NAME=team

run: package
	gradle bootRun

package:
	gradle bootJar
	docker build -t $(PROJECT_NAME) .
	docker tag $(PROJECT_NAME):latest $(ACCOUNT_ID).dkr.ecr.ap-southeast-2.amazonaws.com/$(PROJECT_NAME):latest
	docker tag $(PROJECT_NAME):latest gcr.io/$(PROJECT_ID)/$(PROJECT_NAME):latest

push: package
	-gcloud auth configure-docker
	docker push gcr.io/$(PROJECT_ID)/$(PROJECT_NAME)

deploy: push
	kubectl set image deployment/team team=gcr.io/$(PROJECT_ID)/$(PROJECT_NAME):latest
	kubectl rollout status deployment team

all: base build package


create-ecr:
	aws ecr create-repository --repository-name $(PROJECT_NAME)

push-ecr: package
	docker push $(ACCOUNT_ID).dkr.ecr.ap-southeast-2.amazonaws.com/$(PROJECT_NAME):latest