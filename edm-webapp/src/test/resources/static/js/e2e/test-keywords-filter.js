casper.test.begin('Search - relative keywords makes filtering', 2, function suite(test) {

    casper.start(BASE_URL, function() {});

    casper.then(function() {
        this.fill('#searchForm', {
            'pattern': 'demo'
        });
    });

    casper.wait(3000, function() {
        this.test.assertElementCount('a.lead', 3);
    });

    casper.thenClick(x('//a[contains(text(),"' + 'doc' + '")]'), function() {});

    casper.wait(3000, function() {
        this.test.assertElementCount('a.lead', 1);
    });

    casper.run(function() {
        test.done();
    });
});
