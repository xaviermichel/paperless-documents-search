var casper = require("casper").create();
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

// curl -XPOST 'https://www.franchecomte-g4-enligne.credit-agricole.fr/stb/entreeBam' --data "vitrine=O&largeur_ecran=1024&hauteur_ecran=768&origine=vitrine&situationTravail=BANCAIRE&canal=WEB&typeAuthentification=CLIC_ALLER&urlOrigine=http://www.ca-franchecomte.fr&matrice=true&CCPTE=NUMERO_DE_COMPTE&liberror=&tracking=0"
casper.thenOpen('https://www.franchecomte-g4-enligne.credit-agricole.fr/stb/entreeBam', {
		method: 'post',
		data: {
			'vitrine':'O',
			'largeur_ecran':1024,
			'hauteur_ecran':768,
			'origine':'vitrine',
			'situationTravail':'BANCAIRE',
			'canal':'WEB',
			'typeAuthentification':'CLIC_ALLER',
			'urlOrigine':'http://www.ca-franchecomte.fr',
			'matrice':true,
			'CCPTE': login.toString(),
			'liberror':'',
			'tracking':'O',
		}
		}
		, function() {});

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
	this.echo('Validation du formulaire de connection');
	this.clickLabel('Confirmer');
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
	this.echo('Going to Consultation page');
	this.clickLabel('Consultation');
});

casper.wait(SLEEP_TIME, function() {
	dumpPage(this);
});

casper.then(function() {
	this.echo('Recuperation des liens');
	links = this.evaluate(getLinks);
	lienFactureGlobale = links[0];
	lienFactureGlobale2 = links[1];
	this.echo('- Globale : ' + lienFactureGlobale);
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
});

casper.run();

