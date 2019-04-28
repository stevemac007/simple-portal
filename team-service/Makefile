PROJECT_ID=$(shell gcloud config list project --format=flattened | awk 'FNR == 1 {print $$2}')

run:
	gradle bootRun

package:
	gradle bootJar
	docker build -t gcr.io/$(PROJECT_ID)/team .

push: package
	-gcloud auth configure-docker
	docker push gcr.io/$(PROJECT_ID)/team

deploy: push
	kubectl set image deployment/team team=gcr.io/$(PROJECT_ID)/team:latest
	kubectl rollout status deployment team

all: base build package
