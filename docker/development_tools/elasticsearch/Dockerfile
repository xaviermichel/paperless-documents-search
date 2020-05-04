FROM docker.elastic.co/elasticsearch/elasticsearch:6.8.8

ENV ES_HOST http://127.0.0.1:9200

USER elasticsearch

COPY --chown=elasticsearch:elasticsearch ./ /tmp/elastic_settings
COPY --chown=elasticsearch:elasticsearch ./docker_elasticsearch.yml /usr/share/elasticsearch/config/elasticsearch.yml

RUN set -x && \
   bin/elasticsearch -d -p /tmp/es.pid && \
   LIMIT=60 ; counter=0 ; while [ $counter -lt $LIMIT -a -z "$(curl ${ES_HOST}/_cluster/health\?pretty=true | grep green)" ]; do echo "[$counter] waiting for elastic status green..." && sleep 1 && counter=$((counter + 1)) ; done && \
   curl "${ES_HOST}" && \
   curl -XPUT -H "Content-Type: application/json" "${ES_HOST}/category" -d "@/tmp/elastic_settings/documents.json" && \
   curl -XPUT -H "Content-Type: application/json" "${ES_HOST}/category/_mapping/category" -d "@/tmp/elastic_settings/documents/category.json" && \
   curl -XPUT -H "Content-Type: application/json" "${ES_HOST}/source" -d "@/tmp/elastic_settings/documents.json" && \
   curl -XPUT -H "Content-Type: application/json" "${ES_HOST}/source/_mapping/source" -d "@/tmp/elastic_settings/documents/source.json" && \
   curl -XPUT -H "Content-Type: application/json" "${ES_HOST}/document_file" -d "@/tmp/elastic_settings/documents.json" && \
   curl -XPUT -H "Content-Type: application/json" "${ES_HOST}/document_file/_mapping/document_file" -d "@/tmp/elastic_settings/documents/document_file.json" && \
   kill $(cat /tmp/es.pid) && \
   sleep 2

