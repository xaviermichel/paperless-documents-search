var casper = require("casper").create();

var login = new String(casper.cli.raw.get(0));
var pass = new String(casper.cli.raw.get(1));
var targetFileInstructions = casper.cli.raw.get(2);
var targetFilePdf = casper.cli.raw.get(3);

var lienFactureGlobale = "";

var SLEEP_TIME = 3000;

function getLink() {
    var link = document.getElementById('020_0');
    return link.getAttribute('onclick').replace(/^document\.location\.href='(.*)'$/g, '$1');
}

casper.start();
casper.clear();
phantom.clearCookies();


casper.thenOpen('https://www.citya.com/i-citya/locataire', function() {});

casper.wait(SLEEP_TIME, function() {});

casper.then(function() {
    this.echo('Connection avec le numero ' + login);

    this.sendKeys('input[name=login]', casper.cli.raw.get(0));
    this.sendKeys('input[name=mdp]', casper.cli.raw.get(1));
    this.click('button[type=submit]');
});

casper.wait(SLEEP_TIME, function() {});

casper.thenOpen('https://extranet.ics.fr/V3b/icitya/mesDocuments.php?iiii=a5aTlw&mmmm=a5aUmw&llll=ZpaUlQ&xxxx=b5plusanA', function() {
    this.echo('Recuperation du lien');
    lienFactureGlobale = this.evaluate(getLink);
    this.echo('- Globale : ' + lienFactureGlobale);

    // à cause d'une limitation on va écrire le lien de téléchargement,
    // et c'est le script appelant qui récupérera le fichier
    var cookies = this.evaluate(function() {
        return document.cookie;
    });
    this.echo('- Cookies : ' + cookies);

    var downloadCommand = 'curl "' + lienFactureGlobale + '" -H "Cookie: ' + cookies + '" -o ' + targetFilePdf + ' -H "DNT: 1" -H "Accept-Encoding: gzip, deflate, sdch" -H "Accept-Language: fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4" -H "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36" -H "Connection: keep-alive"';
    var fs = require('fs');
    fs.write(targetFileInstructions, downloadCommand, 'w');
});

casper.run();
