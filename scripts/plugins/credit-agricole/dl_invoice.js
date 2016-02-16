var casper = require("casper").create();
var x = require('casper').selectXPath;

var login = new String(casper.cli.raw.get(0));
var pass = new String(casper.cli.raw.get(1));
var targetFile = casper.cli.raw.get(2);

var passArray = pass.split('').map(function (e) {
	return e;
});

var lienFactureGlobale = "";

var SLEEP_TIME = 2000;

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

casper.wait(SLEEP_TIME, function() {});


casper.eachThen(passArray, function(numberToClick) {
	//this.echo('Clicking on ' + numberToClick.data);
	this.click(x('//a[contains(text(),"' + numberToClick.data  + '")]'));
	this.wait(SLEEP_TIME, function() {});
});

casper.wait(SLEEP_TIME, function() {});

casper.then(function() {
	this.echo('Validation du formulaire de connection');
	this.clickLabel('Confirmer');
});

casper.wait(SLEEP_TIME, function() {});

casper.then(function() {
	this.echo('Going to Consultation page');
	this.clickLabel('Consultation');
});

casper.wait(SLEEP_TIME, function() {});

casper.then(function() {
	this.echo('Recuperation des liens');
	links = this.evaluate(getLinks);
	lienFactureGlobale = links[0];
	this.echo('- Globale : ' + lienFactureGlobale);
});

casper.wait(SLEEP_TIME, function() {});

casper.then(function() {
	this.echo('Telechargement facture globale dans ' + targetFile);
	this.download(lienFactureGlobale, targetFile);
});

casper.run();
