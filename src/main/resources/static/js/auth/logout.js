import { getBaseUrl } from "./apiConfig";

const logoutBtn = document.querySelector('#logoutBtn');
logoutBtn.addEventListener('click', async () => {
    const url = getBaseUrl() + "/api/v1/auth/logout";
    await fetch(url, {
        method: 'POST'
    });
    localStorage.removeItem('access_token');
    location.href = "/"
});