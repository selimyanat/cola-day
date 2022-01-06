## Makefile for coladay application
SHELL=/bin/bash

build:
	echo 'Building application...'
	./mvnw clean verify
	docker build --tag selimyanat/coladay:latest .

run-in-memory:
	echo 'Starting application ...'
	./mvnw spring-boot:run

create-kubernetes-namespace:
	echo 'Creating Kubernetes namespace'
	kubectl create namespace coladay

deploy-to-kubernetes:
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