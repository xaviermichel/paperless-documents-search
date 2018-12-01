const puppeteer = require('puppeteer');
const pupeeteerUtils = require('../commons.puppeteer');
const log = require('./logger').logger;

if (process.argv.length <= 3) {
    log.error("Usage: " + __filename + " ca_user ca_pass");
    process.exit(-1);
}

var caUser = new String(process.argv[2]);
var caPass = new String(process.argv[3]);

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

    log.info("Going on e-document page");
    await pupeeteerUtils.clickByText(page, 'Consultation', 'a');

    log.info("Expending document list");
    await page.waitForXPath("//a[contains(text(), concat('RELEVES DE COMPTES', ''))]");
    await pupeeteerUtils.clickByText(page, 'RELEVES DE COMPTES', 'a');

    browser.on('targetcreated', async(target) => {
        log.verbose(`Created target type ${target.type()} , url ${target.url()}`);
        if (target.type() !== 'page') {
            return;
        }
        let newPage = await target.page();
        await pupeeteerUtils.allowDownloadInData(newPage);
        log.verbose(`Download allowed`);
    });

    log.info("Opening the download link");
    await page.click('img[title="Téléchargez votre document"]');

    log.info("Wait for download to be finished before closing browser");
    await page.waitFor(20000);
    await browser.close();
})();
