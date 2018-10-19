const puppeteer = require('puppeteer');
const pupeeteerUtils = require('../commons.puppeteer.js');

if (process.argv.length <= 3) {
    console.log("Usage: " + __filename + " bouygues_user bouygues_pass");
    process.exit(-1);
}

var bouyguesUser = new String(process.argv[2]);
var bouyguesPass = new String(process.argv[3]);

console.log(`Getting data for user ${bouyguesUser}`);

(async () => {

    const browser = await puppeteer.launch({
        executablePath: '/usr/bin/chromium-browser',
        /*headless: false,*/
        slowMo: 250
    });
    const page = await browser.newPage();
    await page.setViewport({
        width: 1920,
        height: 600
    });

    console.info("=== Going to page");
    await page.goto('https://www.bouyguestelecom.fr/');
    await pupeeteerUtils.clickByText(page, 'Mon compte', 'div');
    await pupeeteerUtils.clickByText(page, 'Accéder à votre compte', 'a', 1);

    await page.waitForNavigation();

    console.info("=== Filling form");
    await page.waitForSelector("#username");
    await page.type('#username', bouyguesUser, {delay: 25});
    await page.type('#password', bouyguesPass, {delay: 25});
    await page.click('#bt_valider');

    await page.waitForNavigation();

    console.info("=== Going on bill page");
    await page.waitForXPath("//span[contains(text(), concat('Mes factures', ''))]");
    await pupeeteerUtils.clickByText(page, 'Mes factures', 'span', 1);
    await page.waitForXPath("//a[contains(text(), concat('Historique des paiements', ''))]");
    await pupeeteerUtils.clickByText(page, 'Historique des paiements', 'a', 1);
    await page.waitForXPath("//span[contains(text(), concat('Voir les factures', ''))]");
    await pupeeteerUtils.clickByText(page, 'Voir les factures', 'span', 0);

    await page.waitForXPath("//span[contains(text(), concat('Télécharger', ''))]");

    await pupeeteerUtils.allowDownloadInData(page);
    await pupeeteerUtils.clickByText(page, 'Télécharger', 'span', 0);

    await browser.close();
})();
