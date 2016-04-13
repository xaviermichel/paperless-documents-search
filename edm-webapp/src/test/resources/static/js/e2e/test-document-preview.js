casper.test.begin('Search - document preview should contains all needed informations', 7, function suite(test) {

    casper.start(BASE_URL, function() {});

    casper.then(function() {
        this.fill('#searchForm', {
            'pattern': 'demo doc'
        });
    });

    casper.wait(1000, function() {
        this.test.assertElementCount('a.lead', 1, 'There is one result in page');
        this.test.assertExists(".spec-search-result .spec-search-icon.icone-sprite.word");
        this.test.assertSelectorHasText('.spec-search-result .spec-search-title', 'demo_doc');
        this.test.assertSelectorHasText('.spec-search-result .spec-search-path', '/host_mount_point/demo_2/demo_doc.docx');
        this.test.assertSelectorHasText('.spec-search-result .spec-search-date', '/' /* '07/04/2016' one day js will have a real date object... */);
        this.test.assertSelectorHasText('.spec-search-result .spec-search-label', 'demo_category_2');
        this.test.assertSelectorHasText('.spec-search-result .spec-search-content', 'This is a demo doc');
    });

    casper.run(function() {
        test.done();
    });
});
