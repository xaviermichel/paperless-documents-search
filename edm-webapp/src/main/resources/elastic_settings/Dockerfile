FROM elasticsearch:2.3

COPY ./ /tmp/elastic_settings

ENV ES_HOST http://127.0.0.1:9200

RUN set -x \
  && plugin install -b mapper-attachments

RUN set -x && \
   mkdir /edm_es_DATA && \
   chown -R elasticsearch:elasticsearch /edm_es_DATA

RUN set -x && \
   rm -v /usr/share/elasticsearch/config/elasticsearch.yml && \
   ln -s /tmp/elastic_settings/docker_elasticsearch.yml /usr/share/elasticsearch/config/elasticsearch.yml && \
   chown -R elasticsearch:elasticsearch /usr/share/elasticsearch/config/elasticsearch.yml

RUN set -x && \
  /docker-entrypoint.sh elasticsearch -d -p /tmp/es.pid && \
  LIMIT=60 ; counter=0 ; while [ $counter -lt $LIMIT -a -z "$(curl ${ES_HOST}/_cluster/health\?pretty=true | grep green)" ]; do echo "[$counter] waiting for elastic status green..." && sleep 1 && counter=$((counter + 1)) ; done && \
  curl "${ES_HOST}" && \
  curl -XPUT "${ES_HOST}/documents" -d "@/tmp/elastic_settings/documents.json" && \
  curl -XPUT "${ES_HOST}/documents/_mapping/category" -d "@/tmp/elastic_settings/documents/category.json" && \
  curl -XPUT "${ES_HOST}/documents/_mapping/source" -d "@/tmp/elastic_settings/documents/source.json" && \
  curl -XPUT "${ES_HOST}/documents/_mapping/document_file" -d "@/tmp/elastic_settings/documents/document_file.json" && \
  kill $(cat /tmp/es.pid) && \
  sleep 2
