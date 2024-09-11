import { getBaseUrl } from "./apiConfig.js";
import { showError } from "./error.js";
import * as Jwt from "./jwt.js";
import { init } from "./init.js";

init();
const container = document.querySelector('.container');
const loginForm = document.querySelector('#loginForm');
const emailInput = document.querySelector('#emailInput');
const passwordInput = document.querySelector('#passwordInput');

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const url = getBaseUrl() + '/api/v1/auth/login';
    const email = emailInput.value;
    const password = passwordInput.value;

    let response = await fetch(url,
        {
            body: JSON.stringify({ email, password }),
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: 'include'
        }
    );

    if (!response.ok) {
        showError(container, response);
    } else {
        const json = await response.json();
        const accessToken = json.access_token;
        Jwt.setAccessToken(accessToken);
        location.href = '/';
    }
});