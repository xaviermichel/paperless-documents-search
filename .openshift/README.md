
# Deploying on openshift

## Create the server

1. Choose custom card
2. Start from git repo https://github.com/xaviermichel/simple-data-search.git

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

## fix build problem !

The version of node is very old and I have some problem. To fix it, you have to
```bash
cd edm-webapp
npm install
bower install
gulp minify-code
```

You can fix thisi, and submit pull request :)

## Custom your conf !

Override configuration in `.openshift/application.properties`


## Stay up to date !

```bash
cd simpledatasearch # openshift repo
git remote add upstream -m master https://github.com/xaviermichel/simple-data-search.git
git fetch upstream
git rebase upstream/develop
git push -f origin
```

