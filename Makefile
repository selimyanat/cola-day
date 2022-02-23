## Makefile for coladay application
SHELL=/bin/bash

build-docker-file:
	echo 'Building docker file...'
	./mvnw clean verify -DskipTests
	docker build --tag selimyanat/coladay:latest .

run-in-docker-compose: build-docker-file
	echo 'Running application in docker-compose...'
	docker-compose down
	docker-compose up

build-bitnami-chart:
	helm repo add bitnami https://charts.bitnami.com/bitnami
	helm dependency build ./infrastructure/coladay-chart

create-kubernetes-namespace:
	echo 'Creating Kubernetes namespace if not exists...'
	kubectl config use-context docker-desktop
	kubectl get namespace | grep -q "^coladay" || kubectl create namespace coladay

deploy-to-kubernetes: create-kubernetes-namespace
	echo 'Deploying coladay in kubernetes...'
	kubectl config use-context docker-desktop
	helm lint ./infrastructure/coladay-chart
	helm upgrade --values ./infrastructure/coladay-chart/values.yaml coladay \
	./infrastructure/coladay-chart \
	--install --atomic  \
	--namespace coladay
	#--dry-run

uninstall-from-kubernetes:
	echo 'Uninstalling from kubernetes'
	helm uninstall coladay --namespace coladay