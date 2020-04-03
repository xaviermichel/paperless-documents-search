async function escapeXpathString(str) {
    const splitedQuotes = str.replace(/'/g, `', "'", '`);
    return `concat('${splitedQuotes}', '')`;
}

function replaceByStar(str) {
    return str.replace(/./g, '*');
}

async function clickByText(page, text, selectorName = "a", index = 0, hideLogText = false) {
    const escapedText = await escapeXpathString(text);
    if (hideLogText) {
        let hiddenText = replaceByStar(text);
        console.log(`Looking for $x("//${selectorName}[contains(text(), ${hiddenText})] #${index}")`);
    } else {
        console.log(`Looking for $x("//${selectorName}[contains(text(), ${escapedText})] #${index}")`);
    }
    const linkHandlers = await page.$x(`//${selectorName}[contains(text(), ${escapedText})]`);
    if (index === -1) {
        index = linkHandlers.length - 1;
    }
    await linkHandlers[index].click();
}

async function hoverByText(page, text, selectorName = "a", index = 0) {
    const escapedText = await escapeXpathString(text);
    console.log(`Looking for $x("//${selectorName}[contains(text(), ${escapedText})] #${index}")`);
    const linkHandlers = await page.$x(`//${selectorName}[contains(text(), ${escapedText})]`);
    if (index === -1) {
        index = linkHandlers.length - 1;
    }
    await linkHandlers[index].hover();
}

async function allowDownloadInData(page) {
    await page._client.send('Page.setDownloadBehavior', {
        behavior: 'allow',
        downloadPath: './data'
    });
}

async function clearInput(page, selector) {
    await page.focus(selector);
    await page.$eval(selector, el => el.setSelectionRange(0, el.value.length));
    return page.keyboard.press('Backspace');
}

module.exports.escapeXpathString = escapeXpathString;
module.exports.clickByText = clickByText;
module.exports.hoverByText = hoverByText;
module.exports.allowDownloadInData = allowDownloadInData;
module.exports.clearInput = clearInput;

