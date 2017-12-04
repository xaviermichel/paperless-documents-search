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

var passArray = pass.split('').map(function (e) {
	return e;
});

var lienFactureGlobale = "";
var lienFactureGlobale2 = "";

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
	var links = document.querySelectorAll('#panneau1 .ca-table td a');
	return Array.prototype.map.call(links, function(e) {
		var href = e.getAttribute('href');
		var extractUrlRegex = /ouvreTelechargement(.*)majDateConsultation/g;
		var res = href.match(extractUrlRegex)[0];
		var url = 'https://www.franchecomte-g4-enligne.credit-agricole.fr/stb/' + res.replace("ouvreTelechargement('", "").replace("');majDateConsultation", "") + '&typeaction=telechargement';
		return url;
	});
}

casper.start();
casper.clear();
phantom.clearCookies();

casper.thenOpen('https://www.ca-franchecomte.fr/', function() {
	this.echo("Ouverture du site du CA");
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.thenClick(x('//span[contains(text(),"Accéder à mes comptes")]'), function() {
    this.echo('Ouverture page de connexion');
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
	this.echo("Remplissage du numéro de compte");
	this.sendKeys('input[name=CCPTE]', login.toString());
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.eachThen(passArray, function(numberToClick) {
	//this.echo('Clicking on ' + numberToClick.data);
	this.click(x('//a[contains(text(),"' + numberToClick.data  + '")]'));
	this.wait(SLEEP_TIME, function() {});
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
	this.echo('Validation du formulaire de connexion');
	this.clickLabel('Confirmer');
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
	this.echo('Navigation vers la page e-documents');
	this.clickLabel('E-Documents', 'a');
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
	this.echo('Recuperation des liens');
	links = this.evaluate(getLinks);
	lienFactureGlobale = links[0];
	lienFactureGlobale2 = links[1];
	this.echo('- Compte 1 : ' + lienFactureGlobale);
	this.echo('- Compte 2 : ' + lienFactureGlobale);
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
	this.echo('Telechargement facture globale dans ' + targetFile);
	this.download(lienFactureGlobale, targetFile);
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
	if (targetFile2 == '') {
		this.echo("Le lien globale2 est vide, on considere un seul document et on ignore et on ne telecharge rien d'autre");
	} else {
		this.echo('Telechargement facture globale 2 dans ' + targetFile2);
		this.download(lienFactureGlobale2, targetFile2);
	}
	this.echo('Va attendre 60 secondes avant de continuer...');
});

casper.run();

