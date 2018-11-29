const puppeteer = require('puppeteer');
const pupeeteerUtils = require('../commons.puppeteer');
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
        executablePath: '/usr/bin/chromium-browser',
        headless: true,
        slowMo: 250
    });
    const page = await browser.newPage();
    await page.setViewport({
        width: 1920,
        height: 600
    });

    browser.on('targetcreated', async(target) => {
        log.verbose(`Created target type ${target.type()} , url ${target.url()}`);
        if (target.type() !== 'page') {
            return;
        }
        let newPage = await target.page();
        await pupeeteerUtils.allowDownloadInData(newPage);
        log.verbose(`Download allowed`);
    });

    log.info("Going to CA website");
    await page.goto('https://www.ca-franchecomte.fr/');
    await pupeeteerUtils.clickByText(page, 'Accéder à mes comptes', 'span');

    await page.waitForNavigation();

    log.info("Filling form");
    await page.waitForSelector("input[name=CCPTE]");
    await page.type('input[name=CCPTE]', caUser, {delay: 25});
    async function fillPasswordGrid(passwordArray) {
        for (const e of passwordArray) {
            //console.log(`clicking on ${e}`);
            await pupeeteerUtils.clickByText(page, e, 'a', 0, true);
        }
    }
    await fillPasswordGrid(caPass.split(''));
    await pupeeteerUtils.clickByText(page, 'Confirmer');

    await page.waitForNavigation();

    log.info("Going on téléchargement page");
    await pupeeteerUtils.clickByText(page, 'Téléchargement', 'a');

    await page.waitForNavigation();

    log.info("Choosing CSV format");
    await page.select('#TEL1_TYPE', '3');

    log.info("Choosing custom period");
    await page.click('.radioBt[value=INTERVAL]');

    log.info('Filling form');
    await pupeeteerUtils.clearInput(page, 'input[name=TEL1_D_J]');
    await page.type('input[name=TEL1_D_J]', dateFrom.split('/')[0], {delay: 25});
    await pupeeteerUtils.clearInput(page, 'input[name=TEL1_D_M]');
    await page.type('input[name=TEL1_D_M]', dateFrom.split('/')[1], {delay: 25});
    await pupeeteerUtils.clearInput(page, 'input[name=TEL1_D_A]');
    await page.type('input[name=TEL1_D_A]', dateFrom.split('/')[2], {delay: 25});
    await pupeeteerUtils.clearInput(page, 'input[name=TEL1_F_J]');
    await page.type('input[name=TEL1_F_J]', dateTo.split('/')[0], {delay: 25});
    await pupeeteerUtils.clearInput(page, 'input[name=TEL1_F_M]');
    await page.type('input[name=TEL1_F_M]', dateTo.split('/')[1], {delay: 25});
    await pupeeteerUtils.clearInput(page, 'input[name=TEL1_F_A]');
    await page.type('input[name=TEL1_F_A]', dateTo.split('/')[2], {delay: 25});

    log.info("Validating form");
    await pupeeteerUtils.clickByText(page, 'Confirmer', 'a');

    await page.waitForNavigation();

    log.info("Downloading file");
    await pupeeteerUtils.clickByText(page, 'Télécharger', 'a');

    log.info("Wait for download to be finished before closing browser");
    await page.waitFor(20000);
    await browser.close();
})();
