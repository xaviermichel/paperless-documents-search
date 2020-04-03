const puppeteer = require('puppeteer');
const pupeeteerUtils = require('../commons.puppeteer');
const pupeeteerDownloadUtils = require('./download.puppeteer');
const log = require('./logger').logger;

if (process.argv.length <= 5) {
    log.error("Usage: " + __filename + " ca_user ca_pass");
    process.exit(-1);
}

var caUser = new String(process.argv[2]);
var caPass = new String(process.argv[3]);
var dateFrom = new String(process.argv[4]);
var dateTo = new String(process.argv[5]);

log.info(`Getting data for user ${caUser}`);

(async () => {

    const browser = await puppeteer.launch({
        executablePath: '/bin/chromium',
        headless: true,
        slowMo: 250
    });
    const page = await browser.newPage();
    await page.setViewport({
        width: 1920,
        height: 800
    });

    log.info("Going to CA website");
    await page.goto('https://www.credit-agricole.fr/ca-franchecomte/particulier/acceder-a-mes-comptes.html');

    log.info("Filling form");
    await page.waitForSelector("input[name=CCPTE]");
    await page.type('input[name=CCPTE]', caUser, {delay: 25});
    await pupeeteerUtils.clickByText(page, 'Entrer mon code personnel', 'button');

    async function fillPasswordGrid(passwordArray) {
        for (const e of passwordArray) {
            //console.log(`clicking on ${e}`);
            await pupeeteerUtils.clickByText(page, e, 'div', -1, true);
        }
    }

    await page.waitFor(1000);
    await fillPasswordGrid(caPass.split(''));
    await page.click('#validation');

    await page.waitForNavigation();

    log.info("Going on téléchargement page");
    await page.goto('https://www.credit-agricole.fr/ca-franchecomte/particulier/operations/operations-courantes/telechargement.html#!/');

    log.info("Choosing accounts");
    await pupeeteerUtils.clickByText(page, 'Compte de Dépôt', 'strong', 0);
    await pupeeteerUtils.clickByText(page, 'Compte de Dépôt', 'strong', 1);

    log.info("Choosing CSV format");
    await page.click('button[data-id=input1]'); // open select
    await page.waitFor(1000);
    await pupeeteerUtils.clickByText(page, 'CSV', 'span');

    log.info("Choosing custom period");
    await page.click('date-picker[label=Du]');

    log.info('Filling form');
    await pupeeteerUtils.clearInput(page, '#dateFrom');
    await page.type('#dateFrom', dateFrom, {delay: 25});
    await pupeeteerUtils.clearInput(page, '#dateTo');
    await page.type('#dateTo', dateTo, {delay: 25});

    log.info("Downloading file");
    await pupeeteerDownloadUtils.download(page, '.FormActions-btn.FormActions-btn--submit');

    await browser.close();
})();
