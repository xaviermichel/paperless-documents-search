KUBECTL=kubectl --context minikube
ISTIOCTL=istioctl --context minikube

prepare-minikube:
	minikube addons enable registry
	minikube addons enable ingress
	grep -q "$$(minikube ip)" /etc/hosts || echo "Please add this line in /etc/hosts : $$(minikube ip) edm.local edm-admin.local"

istio:
	$(ISTIOCTL) manifest apply --set profile=demo
	#$(KUBECTL) label --overwrite namespace edm istio-injection=enabled

edm:
	$(KUBECTL) apply -f edm-namespace.yaml
	$(KUBECTL) apply -f edm-role.yaml
	$(KUBECTL) apply -f edm-elasticsearch.yaml
	$(KUBECTL) apply -f edm-document-ingest-app.yaml
	$(KUBECTL) apply -f edm-document-repository-app.yaml
	$(KUBECTL) apply -f edm-ui.yaml
	$(KUBECTL) apply -f ingress.yaml
	$(KUBECTL) apply -f edm-spring-boot-admin-app-config.yaml
	$(KUBECTL) apply -f edm-spring-boot-admin-app.yaml

.PHONY: prepare-minikube istio edm

