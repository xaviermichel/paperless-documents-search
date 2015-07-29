exports.config = {

    seleniumAddress: 'http://0.0.0.0:4444/wd/hub',

    capabilities: {
        'browserName': 'phantomjs'
    },

    specs: ['src/test/resources/static/js/e2e/*.spec.js'],

    getPageTimeout: 120000,

    jasmineNodeOpts: {
        showColors: true,
        defaultTimeoutInterval: 120000
    }
};
