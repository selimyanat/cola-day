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

create-kubernetes-namespace: build-docker-file
	echo 'Creating Kubernetes namespace'
	kubectl create namespace coladay

deploy-to-kubernetes: create-kubernetes-namespace
	echo 'Installing in kubernetes'
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