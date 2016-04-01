casper.test.begin('Global - elements in main page', 2, function suite(test) {

    casper.start(BASE_URL, function() {});

    casper.waitForText('Simple Data Search', function() {
        this.test.pass("Title 'Simple Data Search' is displayed");
        this.test.assertTitle("Simple Data Search", "page title is ok");
    });

    casper.run(function() {
        test.done();
    });
});
