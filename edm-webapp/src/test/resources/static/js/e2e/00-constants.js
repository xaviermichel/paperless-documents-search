var BASE_URL = 'http://127.0.0.1:8053';

var x = require('casper').selectXPath;

casper.options.viewportSize = {
    width: 800,
    height: 600
};

// display errors
casper.on("page.error", function(msg, trace) {
    this.echo("Error: " + msg, "ERROR");
});

// page dump and screenshot on error
casper.test.on('fail', function(failure) {
    var fs = require('fs');
    var debugFileName = "casper-fail-" + failure.file.split('/').reverse()[0] + "-" + failure.line;
    casper.echo("Capture debug informations (" + debugFileName + ") for error: " + JSON.stringify(failure), "ERROR");
    fs.write(debugFileName + '.html', casper.getPageContent());
    casper.captureSelector(debugFileName + '.png', 'body');
});
