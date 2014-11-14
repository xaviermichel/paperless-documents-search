var casper = require("casper").create();

var login = new String(casper.cli.raw.get(0));
var pass = new String(casper.cli.raw.get(1));
var targetFile = casper.cli.raw.get(2);

var lienFactureGlobale="";

var SLEEP_TIME = 1000;

function getLinks() {
    var links = document.querySelectorAll('#part1 > tbody > tr:nth-child(1) > td.liens > a');
    return Array.prototype.map.call(links, function(e) {
        return e.getAttribute('href');
    });
}

casper.start();
casper.clear();
phantom.clearCookies();

casper.thenOpen('https://www.sfr.fr/cas/login', function() {
});

casper.wait(SLEEP_TIME, function() {
});

casper.then(function() {
	this.echo('Connection avec le numero ' + login);
    this.fill('#loginForm', { 
		'username': login,
		'password': pass
	}, true);
});

casper.wait(SLEEP_TIME, function() {
});

casper.thenOpen('https://www.sfr.fr/mon-espace-client/?sfrintid=EC_head_ec', function() {
	this.echo('Consultation de l\'espace client');
});

casper.wait(SLEEP_TIME, function() {
});

casper.thenOpen('https://espace-client.sfr.fr/paiement-facture/facture-mobile/consultation#sfrclicid=EC_home_mob-abo_consult-facture', function() {
    this.echo('Recuperation des liens');
	links = this.evaluate(getLinks);
    lienFactureGlobale = links[0];
	this.echo('- Globale : ' + lienFactureGlobale);
});

casper.wait(SLEEP_TIME, function() {
});

// téléchargement facture globale
casper.then(function() {
	this.echo('Telechargement facture globale dans ' + targetFile);
    this.download(lienFactureGlobale, targetFile);
});

casper.run();
