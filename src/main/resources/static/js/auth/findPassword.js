import { getBaseUrl } from "./apiConfig.js";
import { showError } from "./error.js";
import { init } from "./init.js";

init();
const container = document.querySelector('.container');
const emailInput = document.querySelector('#emailInput');
const form = document.querySelector('#findForm');
form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const url = getBaseUrl() + '/api/v1/auth/reset-password-mail';
    const email = emailInput.value;

    const response = await fetch(url,
        {
            body: JSON.stringify({ email: email }),
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            }
        }
    );

    if (!response.ok) {
        showError(container, response);
    } else {
        alert('비밀번호 초기화 메일을 보냈습니다');
    }
});