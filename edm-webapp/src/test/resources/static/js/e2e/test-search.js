casper.test.begin('Search - search is auto-submitted', 4, function suite(test) {

    casper.start(BASE_URL, function() {});

    casper.then(function() {
        this.fill('#searchForm', {
            'pattern': 'demo'
        });
    });

    casper.waitForText("demo_pdf", function() {
        this.test.pass("Search has been submitted and 'demo_pdf' document has been found");
    });

    casper.waitForText("demo_txt", function() {
        this.test.pass("Search has been submitted and 'demo_txt' document has been found");
    });

    casper.waitForText("demo_doc", function() {
        this.test.pass("Search has been submitted and 'demo_doc' document has been found");
    });

    casper.then(function() {
        this.test.assertSelectorHasText('.spec-doc-found-count', '3 documents trouvés en');
    });

    casper.run(function() {
        test.done();
    });
});
