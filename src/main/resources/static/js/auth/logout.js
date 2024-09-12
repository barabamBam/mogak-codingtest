setTimeout(function () {
    logoutBtn.addEventListener('click', async () => {
        const url = baseUrl + "/api/v1/auth/logout";
        await fetch(url, {
            method: 'POST'
        });
        localStorage.removeItem('access_token');
        location.href = baseUrl + "/api/v1/posts/list";
    });
}, 2000);