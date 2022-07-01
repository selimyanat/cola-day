## Makefile for coladay application
SHELL=/bin/bash

build-docker-file:
	command -v docker >/dev/null 2>&1 || { echo >&2 "I require docker but it's not installed.Aborting.";}
	echo 'Building docker file...'
	./mvnw clean verify -DskipTests
	docker build --tag selimyanat/coladay:latest .

run-in-docker-compose:build-docker-file
	command -v docker-compose >/dev/null 2>&1 || { echo >&2 "I require docker-compose but it's not installed.Aborting.";}
	echo 'Running application in docker-compose...'
	docker build --tag coladay/prom/prometheus:v2.31.1 ./infrastructure/docker-compose/prometheus
	docker build --tag coladay/grafana/grafana:8.2.3 ./infrastructure/docker-compose/grafana
	docker-compose down
	docker-compose up

build-bitnami-chart:verify-deployment-requirements
	helm repo add bitnami https://charts.bitnami.com/bitnami
	helm dependency build ./infrastructure/coladay-chart

create-kubernetes-namespace:
	echo 'Creating Kubernetes namespace if not exists...'
	kubectl config use-context docker-desktop
	kubectl get namespace | grep -q "^coladay" || kubectl create namespace coladay

deploy-to-kubernetes: build-bitnami-chart create-kubernetes-namespace
	echo 'Deploying coladay in kubernetes...'
	kubectl config use-context docker-desktop
	helm lint ./infrastructure/coladay-chart
	helm upgrade --values ./infrastructure/coladay-chart/values.yaml coladay \
	./infrastructure/coladay-chart \
	--install --atomic  \
	--namespace coladay
	#--dry-run

uninstall-from-kubernetes: verify-deployment-requirements
	echo 'Uninstalling from kubernetes'
	helm uninstall coladay --namespace coladay

uninstall-docker-compose-containers:
	echo 'Stopping all containers'
	docker-compose down

verify-deployment-requirements:
	command -v helm >/dev/null 2>&1 || { echo >&2 "I require helm but it's not installed.Aborting.";}
	command -v kubectl >/dev/null 2>&1 || { echo >&2 "I require kubectl but it's not installed. Aborting.";}
