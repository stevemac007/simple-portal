PROJECT_ID=$(shell gcloud config list project --format=flattened | awk 'FNR == 1 {print $$2}')

run:
	gradle bootRun

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
