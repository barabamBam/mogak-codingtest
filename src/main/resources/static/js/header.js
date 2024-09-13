const baseUrl = "http://" + window.location.host;
const token = localStorage.getItem('access_token');  // 로컬 스토리지에서 JWT 토큰을 가져옴
const userRole = checkUserRole();

function setHeader() {
    const authTag = document.querySelector("#auth");
    const loginBtn = document.createElement('button');
    const logoutBtn = document.createElement('button');
    const personalPage = document.createElement('button');
    const adminButton = document.createElement('button');

    loginBtn.setAttribute("id", "loginBtn");
    loginBtn.textContent = "로그인";

    logoutBtn.setAttribute("id", "logoutBtn");
    logoutBtn.textContent = "로그아웃";

    personalPage.setAttribute("id", "personalPage");
    personalPage.textContent = "마이페이지로 이동";

    adminButton.setAttribute("id", "adminButton");
    adminButton.textContent = "관리자 페이지로 이동";

    if(token) {
        authTag.appendChild(logoutBtn);
        if(userRole === 'USER') {
            authTag.appendChild(personalPage);
            personalPage.addEventListener('click', () =>
                location.href = baseUrl + "/html/profile/profile.html");
        }

        if (userRole === 'ADMIN') {
            authTag.appendChild(adminButton);
            adminButton.addEventListener('click', () =>
                location.href = baseUrl + "/html/admin/adminPage.html");
        }

    }
    else {
        authTag.appendChild(loginBtn);
        loginBtn.addEventListener('click', () =>
            location.href = baseUrl + "/html/auth/login.html");
    }
}

// 저장된 토큰을 가져와 권한을 확인하는 함수
function checkUserRole() {
    const payload = parseJwt(token);  // 토큰의 페이로드를 디코딩
    if (payload && payload.role) {    // 페이로드에서 권한(ROLE) 확인
        return payload.role;
    } else {
        console.error('권한 정보를 가져올 수 없습니다.');
    }
}


// Base64 디코딩 함수
function base64DecodeUnicode(str) {
    return decodeURIComponent(atob(str).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
}

// JWT 페이로드를 디코딩하는 함수
function parseJwt(token) {
    try {
        const base64Payload = token.split('.')[1];  // JWT의 페이로드 부분
        const payload = base64DecodeUnicode(base64Payload);
        return JSON.parse(payload);  // 페이로드를 JSON 객체로 반환
    } catch (e) {
        console.error("Invalid token", e);
        return null;
    }
}
