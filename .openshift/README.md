# Deploying on openshift

## Create the server
1. Choose "do it yourself" card
2. Leave "source code" empty or it will timeout on initialization !

## Configure openshift
1. Generate ssh key
```bash
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```

2. Copy the public key in openshift web interface

3. Configure your local (add in ~/.ssh/config)
```
Host simpledatasearch-xaviermichel.rhcloud.com
    Hostname        simpledatasearch-xaviermichel.rhcloud.com
    IdentityFile    ~/.ssh/id_openshift
    # ProxyCommand /usr/bin/corkscrew proxy.fr 3128 %h %p
```

## Link your repository to paperless-documents-search !
Clean old data :
```bash
git clone https://github.com/xaviermichel/paperless-documents-search.git
git remote add openshift ssh://some123hash@simpledatasearch-xaviermichel.rhcloud.com/~/git/simpledatasearch.git/
git push -f openshift
```

## Custom your conf !
Override configuration in `.openshift/application.properties`
