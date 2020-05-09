KUBECTL=kubectl --context minikube
ISTIOCTL=istioctl --context minikube

prepare-minikube:
	minikube addons enable registry
	minikube addons enable ingress
	grep -q "$$(minikube ip)" /etc/hosts || echo "Please add this line in /etc/hosts : $$(minikube ip) edm.local edm-admin.local edm-logs.local"

edm:
	# main
	$(KUBECTL) apply -f edm/edm-namespace.yaml
	$(KUBECTL) apply -f edm/edm-role.yaml
	$(KUBECTL) apply -f edm/edm-ingress.yaml
	# edm es
	$(KUBECTL) apply -f edm/edm-elasticsearch.yaml
	# edm apps
	$(KUBECTL) apply -f edm/edm-document-ingest-app-config.yaml
	$(KUBECTL) apply -f edm/edm-document-ingest-app.yaml
	$(KUBECTL) apply -f edm/edm-document-repository-app-config.yaml
	$(KUBECTL) apply -f edm/edm-document-repository-app.yaml
	$(KUBECTL) apply -f edm/edm-spring-boot-admin-app-config.yaml
	$(KUBECTL) apply -f edm/edm-spring-boot-admin-app.yaml
	$(KUBECTL) apply -f edm/edm-ui.yaml

istio:
	$(ISTIOCTL) manifest apply --set profile=demo
	$(KUBECTL) apply -f istio/

remove-istio:
	$(ISTIOCTL) manifest generate --set profile=demo | $(KUBECTL) delete -f -

elk:
	$(KUBECTL) apply -f elk/elk-namespace.yaml
	$(KUBECTL) apply -f ./elk/es-pvc.yaml
	$(KUBECTL) apply -f ./elk/es.yaml
	$(KUBECTL) apply -f ./elk/filebeat.yaml
	$(KUBECTL) apply -f ./elk/kibana.yaml
	$(KUBECTL) apply -f ./elk/curator.yaml
	$(KUBECTL) apply -f ./elk/elk-ingress.yaml

.PHONY: prepare-minikube edm istio remove-istio elk

