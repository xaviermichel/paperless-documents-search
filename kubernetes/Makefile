KUBECTL=kubectl --context minikube
ISTIOCTL=istioctl --context minikube

prepare-minikube:
	minikube addons enable registry
	minikube addons enable ingress
	grep -q "$$(minikube ip)" /etc/hosts || echo "Please add this line in /etc/hosts : $$(minikube ip) edm.local edm-admin.local"

edm:
	$(KUBECTL) apply -f edm/edm-namespace.yaml
	$(KUBECTL) apply -f edm/edm-role.yaml
	$(KUBECTL) apply -f edm/edm-elasticsearch.yaml
	$(KUBECTL) apply -f edm/edm-document-ingest-app.yaml
	$(KUBECTL) apply -f edm/edm-document-repository-app.yaml
	$(KUBECTL) apply -f edm/edm-ui.yaml
	$(KUBECTL) apply -f edm/edm-spring-boot-admin-app-config.yaml
	$(KUBECTL) apply -f edm/edm-spring-boot-admin-app.yaml
	$(KUBECTL) apply -f edm/edm-ingress.yaml

istio:
	$(ISTIOCTL) manifest apply --set profile=demo
	$(KUBECTL) apply -f istio/

elk:
	echo "TODO"

.PHONY: prepare-minikube edm istio elk

