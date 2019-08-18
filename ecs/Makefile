WHOAMI=`whoami`
VPC_STACK_NAME=smac-ecs-cluster
DNS_SUFFIX=thanatopho.be

ACCOUNT_ID=$(shell aws accountid)

create-cluster:
	stackup $(VPC_STACK_NAME) up --template cluster-vpc.yml

delete-cluster:
	stackup $(VPC_STACK_NAME)-auth-service down
	stackup $(VPC_STACK_NAME)-team-service down
	stackup $(VPC_STACK_NAME)-postgres-service down
	stackup $(VPC_STACK_NAME) down

create-ecr:
	(cd ../auth-service && make create-ecr)
	(cd ../team-service && make create-ecr)
#	(cd ../web-portal && make create-ecr)

push:
	(cd ../auth-service && make push-ecr)
	(cd ../team-service && make push-ecr)
#	(cd ../web-portal && make push-ecr)

deploy-postgres:
	stackup $(VPC_STACK_NAME)-postgres-service up --template postgres.yml -p postgres.json \
	-o StackName=$(VPC_STACK_NAME)

deploy-auth:
	stackup $(VPC_STACK_NAME)-auth-service up --template service.yml -p auth.json \
	-o StackName=$(VPC_STACK_NAME) \
	-o DNSSuffix=$(DNS_SUFFIX) \
	-o ImageUrl=$(ACCOUNT_ID).dkr.ecr.ap-southeast-2.amazonaws.com/auth:latest

deploy-team:
	stackup $(VPC_STACK_NAME)-team-service up --template service.yml -p team.json \
	-o StackName=$(VPC_STACK_NAME) \
	-o DNSSuffix=$(DNS_SUFFIX) \
	-o ImageUrl=$(ACCOUNT_ID).dkr.ecr.ap-southeast-2.amazonaws.com/team:latest