var fs = require('fs');
var path = require("path");

var CURRENT_DIR = path.resolve(".");

Utils = {

    /**
     * @name screenShotDirectory
     * @description The directory where screenshots will be created
     * @type {String}
     */
    screenShotDirectory: 'test/results/',

    /**
     * @name writeScreenShot
     * @description Write a screenshot string to file.
     * @param {String} data The base64-encoded string to write to file
     * @param {String} filename The name of the file to create (do not specify directory)
     */
    writeScreenShot: function(data, filename) {
        var stream = fs.createWriteStream(this.screenShotDirectory + filename);

        stream.write(new Buffer(data, 'base64'));
        stream.end();
    }
};


describe('Searching use case', function() {

    var mainTitle = element(by.css('.spec-title'));
    var searchInputBox = element(by.css('.spec-input-search'));
    var seachButton = element(by.css('.spec-button-search'));
    var docFoundCount = element(by.css('.spec-doc-found-count'));
    var crawlingResultStatus = element(by.css('.spec-generic-result-status'));

    beforeEach(function() {
        browser.get('http://localhost:9053/#/');
    });

    /**
     * Automatically store a screenshot for each test.
     */
    /*
    afterEach(function() {
        var currentSpec = jasmine.getEnv().currentSpec,
            passed = currentSpec.results().passed();

        browser.takeScreenshot().then(function(png) {
            browser.getCapabilities().then(function(capabilities) {
                var browserName = capabilities.caps_.browserName,
                    passFail = (passed) ? 'pass' : 'FAIL',
                    filename = browserName + ' ' + passFail + ' - ' + currentSpec.description + '.png';

                Utils.writeScreenShot(png, filename);
            });
        });
    });
    */

    it('checking page main title', function() {
        expect(mainTitle.getText()).toEqual('Simple Data Search');
    });

    it('searching dummy pattern returns no results', function() {
        searchInputBox.sendKeys('Hi');
        seachButton.click();
        expect(docFoundCount.getText()).toEqual('0');
    });

/*
	// http://127.0.0.1:9053/crawl/filesystem?edmServerHttpAddress=127.0.0.1:9053&path=/home/xavier/work/simple-data-search/edm-webapp/src/test/resources
	it('should index some content', function() {
		browser.sleep(30000);
		browser.driver.get('http://127.0.0.1:9053/crawl/filesystem?edmServerHttpAddress=127.0.0.1:9053&path=' + __dirname + '/../../..');
		browser.sleep(30000);
		expect(true).toBe(true);
	});

	it('searching existing pattern should return 1 result', function() {
		searchInputBox.sendKeys('edm');
		seachButton.click();
		expect(docFoundCount.getText()).toEqual('1');
	});
*/
});
