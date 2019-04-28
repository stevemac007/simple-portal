PROJECT_ID=$(shell gcloud config list project --format=flattened | awk 'FNR == 1 {print $$2}')

CLUSTER_NAME=my-istio-cluster
CLUSTER_VERSION=1.12

GCLOUD_USER=$(shell gcloud config get-value core/account)
GKE_CLUSTER=$(shell kubectl config get-clusters | grep my-istio-cluster)

DNS_SERVICE_ACCOUNT_NAME=dns-service-account
FILE_NAME=$(DNS_SERVICE_ACCOUNT_NAME).json

DNS_ZONE_NAME=gcp
DNS_ZONE=gcp.whitesquaresoft.com

GOOGLE_APPLICATION_CREDENTIALS=$(DNS_SERVICE_ACCOUNT_NAME).json

JWT_AUTH_TOKEN=$(shell cat .jwt)

create-cluster:
	gcloud container --project "$(PROJECT_ID)" clusters create "$(CLUSTER_NAME)" --zone "$(ZONE)" --machine-type "n1-standard-1" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.read_only","https://www.googleapis.com/auth/logging.write","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management.readonly","https://www.googleapis.com/auth/trace.append" --num-nodes "4" --network "default" --enable-cloud-logging --enable-cloud-monitoring --cluster-version=$(CLUSTER_VERSION)
	kubectl create clusterrolebinding cluster-admin-binding --clusterrole=cluster-admin --user=$(GCLOUD_USER)

delete-cluster:
	gcloud container clusters delete "$(CLUSTER_NAME)" --zone "$(ZONE)"

setup-service-account:
	#gcloud iam service-accounts create $(DNS_SERVICE_ACCOUNT_NAME)
	gcloud projects add-iam-policy-binding $(PROJECT_ID) --member "serviceAccount:$(DNS_SERVICE_ACCOUNT_NAME)@$(PROJECT_ID).iam.gserviceaccount.com" --role "roles/owner"
	gcloud iam service-accounts keys create $(FILE_NAME) --iam-account $(DNS_SERVICE_ACCOUNT_NAME)@$(PROJECT_ID).iam.gserviceaccount.com

delete-service-account:
	gcloud iam service-accounts delete $(DNS_SERVICE_ACCOUNT_NAME)@$(PROJECT_ID).iam.gserviceaccount.com


deploy:
	kubectl apply -f postgres.yml
	-sed -e 's~<PROJECT_ID>~$(PROJECT_ID)~g' deployment.yml | kubectl apply -f -


get-stuff:
	kubectl get pods,svc,ingress,ServiceAccount,deployment
	gcloud dns record-sets list -z $(DNS_ZONE_NAME)

dry-run:
	external-dns --registry txt --txt-owner-id $(CLUSTER_NAME) --provider google --google-project $(PROJECT_ID) --source service --source ingress --once --dry-run

apply-dns:
	external-dns --registry txt --txt-owner-id $(CLUSTER_NAME) --provider google --google-project $(PROJECT_ID) --source service --source ingress --once

apply-dns-poll:
	external-dns --registry txt --txt-owner-id $(CLUSTER_NAME) --provider google --google-project $(PROJECT_ID) --source service --source ingress

user-login:
	http POST http://auth.gcp.whitesquaresoft.com/auth/signin username=user password=password -o .jwtpacket
	cat .jwtpacket | jq .token -r > .jwt

admin-login:
	http POST http://auth.gcp.whitesquaresoft.com/auth/signin username=admin password=password -o .jwtpacket
	cat .jwtpacket | jq .token -r > .jwt

get-login:
	http GET http://team.gcp.whitesquaresoft.com/me --auth-type=jwt

get-demo:
	http GET http://team.gcp.whitesquaresoft.com/vehicles --auth-type=jwt

post-demo1:
	http POST http://team.gcp.whitesquaresoft.com/vehicles --auth-type=jwt < v1.json

post-demo2:
	http POST http://team.gcp.whitesquaresoft.com/vehicles --auth-type=jwt < v2.json

delete-demo22:
	http DELETE http://team.gcp.whitesquaresoft.com/vehicles/22 --auth-type=jwt < v2.json

post-lots:
	for x in `seq 1 10`; do \
	http POST http://team.gcp.whitesquaresoft.com/vehicles --auth-type=jwt < v1.json ; \
	http POST http://team.gcp.whitesquaresoft.com/vehicles --auth-type=jwt < v2.json ; \
	done

get-page2:
	http GET "http://team.gcp.whitesquaresoft.com/vehicles?page=1" --auth-type=jwt
