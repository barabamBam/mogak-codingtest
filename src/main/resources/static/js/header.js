const baseUrl = "http://" + window.location.host;

const token = localStorage.getItem('access_token');

setTimeout(function() {
    const authTag = document.querySelector("#auth");

    var loginBtn = document.createElement('button');
    loginBtn.setAttribute("id", "loginBtn");
    loginBtn.textContent = "로그인";

    var logoutBtn = document.createElement('button');
    logoutBtn.setAttribute("id", "logoutBtn");
    logoutBtn.textContent = "로그아웃";

    var personalPage = document.createElement('button');
    personalPage.setAttribute("id", "personalPage");
    personalPage.textContent = "마이페이지로 이동"

    if(token) {
        authTag.appendChild(logoutBtn);
        authTag.appendChild(personalPage);
        personalPage.addEventListener('click', () =>
            location.href = baseUrl + "/html/profile/profile.html")
    }
    else {
        authTag.appendChild(loginBtn);
        loginBtn.addEventListener('click', () =>
            location.href = baseUrl + "/html/auth/login.html");
    }
}, 300);