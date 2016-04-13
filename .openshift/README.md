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

## Link your repository to simple-data-search !
Clean old data : 
```bash
cd simpledatasearch # openshift repo
rm -fr * .openshift
git commit -a -m "init: cleaning"
```

## Fix build problem !
The version of node is very old and I had some problem for deploying app. 
To fix it, I build locally js resources. You have to
```bash
cd edm-webapp
npm install
bower install
gulp minify-code
```
... and force add to static ressources :
```bash
git add -f src/main/resources/static/bower_inc src/main/resources/static/build
git commit -a -m "chore: add static resources"
```
You can now push your work !
```bash
git push
```
You can fix this, and submit pull request :)

## Custom your conf !
Override configuration in `.openshift/application.properties`
