const expect = require('expect-puppeteer');
const { setDefaultOptions } = require('expect-puppeteer');
setDefaultOptions({ timeout: 5000 });
const fs = require('fs');
const path = require('path');

async function waitForFileToDownload(downloadPath) {
    console.log('Waiting to download file...');
    let filename;
    while (!filename || filename.endsWith('.crdownload')) {
        filename = fs.readdirSync(downloadPath)[0];
        await new Promise(resolve => setTimeout(resolve, 500));
    }
    return filename;
}

async function download(page, selector) {
    const downloadPath = path.resolve(__dirname, 'data');
    console.log(`Downloading file to : ${downloadPath}`);
    await page._client.send('Page.setDownloadBehavior', { behavior: 'allow', downloadPath: downloadPath });
    await expect(page).toClick(selector);
    let filename = await waitForFileToDownload(downloadPath);
    return path.resolve(downloadPath, filename);
}

module.exports.waitForFileToDownload = waitForFileToDownload;
module.exports.download = download;

