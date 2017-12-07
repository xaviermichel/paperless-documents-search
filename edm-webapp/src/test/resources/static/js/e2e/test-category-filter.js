casper.test.begin('Search - category filter should works', 2, function suite(test) {

    casper.start(BASE_URL, function() {});

    casper.then(function() {
        this.fill('#searchForm', {
            'pattern': 'demo'
        });
    });

    casper.wait(3000, function() {
        this.test.assertElementCount('a.lead', 3);
    });

    casper.thenClick('form[name=input_category] input[name=demo_category_2]', function() {});

    casper.wait(3000, function() {
        this.test.assertElementCount('a.lead', 2);
    });

    casper.run(function() {
        test.done();
    });
});
