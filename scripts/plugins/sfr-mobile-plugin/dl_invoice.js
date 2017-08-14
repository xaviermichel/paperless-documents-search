var casper = require("casper").create({
    viewportSize: {
       width: 1920,
       height: 1080
	}
});
var x = require('casper').selectXPath;

var login = new String(casper.cli.raw.get(0));
var pass = new String(casper.cli.raw.get(1));
var targetFile = casper.cli.raw.get(2);
var targetFile2 = casper.cli.raw.get(3);

var lienFactureGlobale = "";
var lienFactureDetails = "";

var SLEEP_TIME = 2000;

// debug
var debugNavigation = false;

var fs = require('fs');
var currentDebugIndex = 0;
function dumpPage(casperObject) {
	if (! debugNavigation) {
		return;
	}
	casperObject.echo("Debugging point " + currentDebugIndex, 'INFO');
	fs.write('debug/' + currentDebugIndex + '.html', casperObject.getPageContent());
	casperObject.captureSelector('debug/' + currentDebugIndex + '.png', 'html');
	currentDebugIndex++;
}
// end of debug


function getLinks() {
    var links = document.querySelectorAll('.sr-container-box-M .sr-container-content a');
    return Array.prototype.map.call(links, function(e) {
        return e.getAttribute('href');
    });
}

casper.start();
casper.clear();
phantom.clearCookies();

casper.thenOpen('https://www.sfr.fr/bounce?target=https://www.sfr.fr/sfr-et-moi/bounce.html&casforcetheme=mire-sfr-et-moi&mire_layer', function() {
	this.echo("Ouverture du portail de connextion SFR");
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
    this.echo('Connection avec le numero ' + login);
    this.fill('#loginForm', {
        'username': login,
        'password': pass
    }, true);
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.thenOpen('http://espace-client-red.sfr.fr/facture-mobile/consultation#sfrintid=EC_telecom_mob-abo_mob-factpaiement', function() {
	this.echo("Ouverture de la page espace client");
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
    this.echo('Recuperation des liens');
    links = this.evaluate(getLinks);
    lienFactureGlobale = links[0];
    lienFactureDetails = links[1];
    this.echo('- Globale : ' + lienFactureGlobale);
    this.echo('- Details : ' + lienFactureDetails);
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

// téléchargement facture globale
casper.then(function() {
    this.echo('Telechargement facture globale dans ' + targetFile);
    this.download(lienFactureGlobale, targetFile);
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

// téléchargement détails
casper.then(function() {
    this.echo('Telechargement facture details dans ' + targetFile2);
    this.download(lienFactureDetails, targetFile2);
});

casper.run();
