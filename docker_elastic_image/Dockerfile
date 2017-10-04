FROM docker.elastic.co/elasticsearch/elasticsearch:5.5.2

ENV ES_HOST http://127.0.0.1:9200

USER root

# for OCR
RUN set -x && \
    wget http://dl.fedoraproject.org/pub/epel/7/x86_64/e/epel-release-7-10.noarch.rpm && \
    rpm -ivh epel-release-7-10.noarch.rpm && \
    yum --enablerepo=epel install -y tesseract.x86_64 tesseract-langpack-fra.noarch

RUN set -x && \
    mkdir /edm_es_DATA && \
    chown -R elasticsearch:elasticsearch /edm_es_DATA


USER elasticsearch

COPY ./ /tmp/elastic_settings

RUN set -x && \
   curl 'https://artifacts.elastic.co/downloads/elasticsearch-plugins/ingest-attachment/ingest-attachment-5.5.2.zip' -o /tmp/ingest-attachment.zip && \
   bin/elasticsearch-plugin install --batch file:///tmp/ingest-attachment.zip

RUN set -x && \
    rm -v /usr/share/elasticsearch/config/elasticsearch.yml && \
    ln -s /tmp/elastic_settings/docker_elasticsearch.yml /usr/share/elasticsearch/config/elasticsearch.yml

RUN set -x && \
   bin/elasticsearch -d -p /tmp/es.pid && \
   LIMIT=60 ; counter=0 ; while [ $counter -lt $LIMIT -a -z "$(curl ${ES_HOST}/_cluster/health\?pretty=true | grep green)" ]; do echo "[$counter] waiting for elastic status green..." && sleep 1 && counter=$((counter + 1)) ; done && \
   curl "${ES_HOST}" && \
   curl -XPUT "${ES_HOST}/_ingest/pipeline/attachment" -d "@/tmp/elastic_settings/pipeline-attachment.json" && \
   curl -XPUT "${ES_HOST}/documents" -d "@/tmp/elastic_settings/documents.json" && \
   curl -XPUT "${ES_HOST}/documents/_mapping/category" -d "@/tmp/elastic_settings/documents/category.json" && \
   curl -XPUT "${ES_HOST}/documents/_mapping/source" -d "@/tmp/elastic_settings/documents/source.json" && \
   curl -XPUT "${ES_HOST}/documents/_mapping/document_file" -d "@/tmp/elastic_settings/documents/document_file.json" && \
   kill $(cat /tmp/es.pid) && \
   sleep 2

