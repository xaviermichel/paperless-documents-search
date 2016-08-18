Vagrant.require_version ">= 1.6.0"
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
    config.vm.provider "virtualbox" do |v|
      v.memory = 2048
      v.cpus = 2
    end

    config.vm.box = "williamyeh/ubuntu-trusty64-docker"

    ## Provisionning with docker from vagrant

    config.vm.provision "shell", path: "bootstrap_dev_tools.sh"
    config.vm.provision :docker
    config.vm.provision "shell", privileged: false, path: "bootstrap_app.sh"

    config.vm.network "forwarded_port", guest: 9200, host: 9201
    config.vm.network "forwarded_port", guest: 9300, host: 9301
    config.vm.network "forwarded_port", guest: 8053, host: 9053
    config.vm.network "forwarded_port", guest: 8080, host: 9080
end
