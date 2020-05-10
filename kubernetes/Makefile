KUBECTL=kubectl --context minikube
ISTIOCTL=istioctl --context minikube

prepare-minikube:
	minikube start \
		--kubernetes-version=v1.18.1 \
		--extra-config=kubelet.authentication-token-webhook=true \
		--extra-config=kubelet.authorization-mode=Webhook \
		--extra-config=scheduler.address=0.0.0.0 \
		--extra-config=controller-manager.address=0.0.0.0
	minikube addons disable metrics-server
	minikube addons enable registry
	minikube addons enable ingress
	grep -q "$$(minikube ip)" /etc/hosts || echo "Please add this line in /etc/hosts : $$(minikube ip) edm.local edm-admin.local edm-logs.local edm-prometheus.local edm-grafana.local edm-alertmanager.local"

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

prometheus:
	$(KUBECTL) apply -f ./prometheus/manifests/setup
	until $(KUBECTL) get servicemonitors --all-namespaces ; do date; sleep 1; echo ""; done
	$(KUBECTL) create -f ./prometheus/manifests/
	$(KUBECTL) apply -f ./prometheus/prom-ingress.yaml
	$(KUBECTL) apply -f ./prometheus/edm-service-monitor.yaml
	# https://grafana.com/grafana/dashboards/4701

.PHONY: prepare-minikube edm istio remove-istio elk prometheus

