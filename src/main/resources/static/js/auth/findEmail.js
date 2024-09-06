import { getBaseUrl } from "./apiConfig.js";
import { showError } from "./error.js";
import { init } from "./init.js";

init();
const container = document.querySelector('.container');
const nameInput = document.querySelector('#nameInput');
const nicknameInput = document.querySelector('#nicknameInput');
const form = document.querySelector('#findForm');

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = nameInput.value;
    const nickname = nicknameInput.value;

    const url = getBaseUrl() + '/api/v1/auth/find-email';
    const response = await fetch(url + `?name=${name}&nickname=${nickname}`, {
        method: 'GET'
    });

    if (!response.ok) {
        showError(container, response);
    } else {
        const json = await response.json();
        alert('이메일: ' + json.email);
    }
});