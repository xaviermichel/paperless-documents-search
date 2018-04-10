var casper = require("casper").create();
var x = require('casper').selectXPath;

var login = new String(casper.cli.raw.get(0));
var pass = new String(casper.cli.raw.get(1));
var targetFile = casper.cli.raw.get(2);

var lienFactureGlobale = "";

var SLEEP_TIME = 2000;

function getLinks() {
    var links = document.querySelectorAll('div.ec-lastBill-amount > a');
    return Array.prototype.map.call(links, function(e) {
        return e.getAttribute('href');
    });
}

casper.start();
casper.clear();
phantom.clearCookies();

casper.thenOpen('https://id.orange.fr/auth_user/bin/auth_user.cgi?service=nextecare&return_url=https://espaceclientv3.orange.fr/', function() {});

casper.wait(SLEEP_TIME, function() {});

casper.then(function() {
    this.echo('Connection avec le compte : ' + login);
    this.fill('#AuthentForm', {
        'credential': login,
        'password': pass
    }, true);
});

casper.wait(SLEEP_TIME, function() {});

casper.thenClick(x('//h3[contains(text(),"Factures")]'), function() {
    this.echo('Consultation de l\'espace client');
});

casper.wait(SLEEP_TIME, function() {});

casper.then(function() {
    this.echo('Recuperation du lien');
    links = this.evaluate(getLinks);
    lienFactureGlobale = links[0];
    this.echo('- Globale : ' + lienFactureGlobale);
});

casper.wait(SLEEP_TIME, function() {});

// téléchargement facture globale
casper.then(function() {
    this.echo('Telechargement facture globale dans ' + targetFile);
    this.download(lienFactureGlobale, targetFile);
});

casper.run();
