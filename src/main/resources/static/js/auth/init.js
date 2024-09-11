export function init() {
    document.addEventListener('DOMContentLoaded', async () => {
        const headerResponse = await fetch('../header/header.html');
        const headerHtml = await headerResponse.text();
        const headerContainer = document.createElement('div');
        headerContainer.classList.add('container');
        headerContainer.innerHTML = headerHtml;
        document.querySelector('header').appendChild(headerContainer);
    })
};