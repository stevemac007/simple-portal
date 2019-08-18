WHOAMI=`whoami`
VPC_STACK_NAME=smac-ecs-cluster
ACCOUNT_ID=$(shell aws accountid)

create-cluster:
	stackup $(VPC_STACK_NAME) up --template cluster-vpc.yml

delete-cluster:
	stackup $(VPC_STACK_NAME) down

push:
	(cd ../auth-service && make push-ecr)
	(cd ../team-service && make push-ecr)
#	(cd ../web-portal && make push-ecr)

deploy-postgres:
	stackup sm-ecs-postgres-service up --template postgres.yml -p postgres.json \
	-o StackName=$(VPC_STACK_NAME)

deploy-auth:
	stackup sm-ecs-auth-service up --template service.yml -p auth.json \
	-o StackName=$(VPC_STACK_NAME) \
	-o ImageUrl=$(ACCOUNT_ID).dkr.ecr.ap-southeast-2.amazonaws.com/auth:latest

deploy-team:
	stackup sm-ecs-team-service up --template service.yml -p team.json \
	-o StackName=$(VPC_STACK_NAME) \
	-o ImageUrl=$(ACCOUNT_ID).dkr.ecr.ap-southeast-2.amazonaws.com/team:latest