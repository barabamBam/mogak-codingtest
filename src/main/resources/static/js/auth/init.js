export function init() {
    document.addEventListener('DOMContentLoaded', function () {
        // 컴포넌트 로드
        Promise.any([
            fetch('../../../html/header/header.html').then(response => response.text())
        ]).then(header => {
            document.getElementById('header').innerHTML = header;

        });
    })
};