export function setAccessToken(token) {
    if (typeof token !== "string") {
        console.assert(false);
    } else {
        localStorage.setItem('access_token', token);
    }
}

export function getAccessTokenOrNull() {
    return localStorage.getItem('access_token');
}

export function setAuthorizeHeader(headers = {}) {
    const accessToken = getAccessTokenOrNull();
    if (accessToken) {
        headers['Authorization'] = `Bearer ${accessToken}`;
    }
    return headers;
}


export async function refresh() {
    const accessToken = getAccessTokenOrNull();
    if (!accessToken) {
        console.error('access token not exist');
    } else {
        const url = getBaseUrl() + '/api/v1/auth/refresh'
        const response = await fetch(url, {
            method: 'POST',
            body: JSON.stringify({ 'access_token': accessToken }),
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const json = await response.json();

        if (!response.ok) {
            console.error(json.reason);
            alert("토큰이 만료됬습니다. 로그아웃 후 다시 로그인해주세요.");
        }
    }
}

