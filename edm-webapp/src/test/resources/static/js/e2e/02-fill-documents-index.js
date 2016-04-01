casper.test.begin('Indexation - index some documents witch embedded crawler', 2, function suite(test) {

    casper.start(BASE_URL, function() {});

    casper.thenOpen(BASE_URL + "/crawl/filesystem?path=/host_mount_point/demo_1&sourceName=demo_source_1&categoryName=demo_category_1", function() {});

    casper.waitForText('OK', function() {
        this.test.pass("I can index 'demo_1' documents");
    }, function onTimeout() {
        this.test.fail("I couldn't index 'demo_1' documents");
    }, 30000);

    casper.thenOpen(BASE_URL + "/crawl/filesystem?path=/host_mount_point/demo_2&sourceName=demo_source_2&categoryName=demo_category_2", function() {});

    casper.waitForText('OK', function() {
        this.test.pass("I can index 'demo_2' documents");
    }, function onTimeout() {
        this.test.fail("I couldn't index 'demo_2' documents");
    }, 30000);

    casper.run(function() {
        test.done();
    });
});
