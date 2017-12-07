casper.test.begin('Search - file type filter should works', 2, function suite(test) {

    casper.start(BASE_URL, function() {});

    casper.then(function() {
        this.fill('#searchForm', {
            'pattern': 'demo'
        });
    });

    casper.wait(3000, function() {
        this.test.assertElementCount('a.lead', 3);
    });

    casper.thenClick('form[name=input_fileExtension] input[name=pdf]', function() {});

    casper.wait(3000, function() {
        this.test.assertElementCount('a.lead', 2);
    });

    casper.run(function() {
        test.done();
    });
});
