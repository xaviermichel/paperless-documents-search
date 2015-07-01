var casper = require("casper").create();

var login = new String(casper.cli.raw.get(0));
var pass = new String(casper.cli.raw.get(1));
var targetFile = casper.cli.raw.get(2);
var targetFile2 = casper.cli.raw.get(3);

var lienFactureGlobale = "";
var lienFactureDetails = "";

var SLEEP_TIME = 2000;

function getLinks() {
    var links = document.querySelectorAll('.facture a');
    return Array.prototype.map.call(links, function(e) {
        return e.getAttribute('href');
    });
}

casper.start();
casper.clear();
phantom.clearCookies();

casper.thenOpen('https://www.sfr.fr/cas/login', function() {});

casper.wait(SLEEP_TIME, function() {});

casper.then(function() {
    this.echo('Connection avec le numero ' + login);
    this.fill('#loginForm', {
        'username': login,
        'password': pass
    }, true);
});

casper.wait(SLEEP_TIME, function() {});

casper.thenOpen('https://www.sfr.fr/mon-espace-client/?sfrintid=EC_head_ec', function() {
    this.echo('Consultation de l\'espace client');
});

casper.wait(SLEEP_TIME, function() {});

casper.thenOpen('https://espace-client.sfr.fr/paiement-facture/facture-mobile/consultation#sfrclicid=EC_home_mob-abo_consult-facture', function() {
    this.echo('Recuperation des liens');
    links = this.evaluate(getLinks);
    lienFactureGlobale = links[0];
    lienFactureDetails = links[1];
    this.echo('- Globale : ' + lienFactureGlobale);
    this.echo('- Details : ' + lienFactureDetails);
});

casper.wait(SLEEP_TIME, function() {});

// téléchargement facture globale
casper.then(function() {
    this.echo('Telechargement facture globale dans ' + targetFile);
    this.download(lienFactureGlobale, targetFile);
});

casper.wait(SLEEP_TIME, function() {});

// téléchargement détails
casper.then(function() {
    this.echo('Telechargement facture details dans ' + targetFile2);
    this.download(lienFactureDetails, targetFile2);
});

casper.run();
