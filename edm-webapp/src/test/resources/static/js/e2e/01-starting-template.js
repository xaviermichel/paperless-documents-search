casper.test.begin('Global - elements in main page', 2, function suite(test) {

    casper.start(BASE_URL, function() {});

    casper.waitForText('Paperless Documents Search', function() {
        this.test.pass("Title 'Paperless Documents Search' is displayed");
        this.test.assertTitle("Paperless Documents Search", "page title is ok");
    });

    casper.run(function() {
        test.done();
    });
});
