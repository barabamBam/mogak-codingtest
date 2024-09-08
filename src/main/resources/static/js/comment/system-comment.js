document.addEventListener('DOMContentLoaded', function () {

  // 시스템 댓글 불러오기
  // const postId = getPostIdFromUrl(); // URL에서 postId를 가져오는 함수 (별도로 구현 필요)
  // const urlParams = new URLSearchParams(window.location.search);
  // const postId = urlParams.get('postId');
  const postId = 3; // TODO 동적으로 변경
  fetchSystemComment(postId);
});

// 로컬 스토리지에서 토큰을 가져온다.
const token = localStorage.getItem('authToken');
// if (!token) {
//   window.location.href = '';
// }

function fetchSystemComment(postId) {
  const url = `http://localhost:8081/api/v1/posts/${postId}/system-comments`
  fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    }
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    console.log('Received data:', JSON.stringify(data, null, 2)); // 데이터 구조를 자세히 로그에 출력
    // console.log('Received data:', responseData); // 받아온 데이터를 로그에 출력

    // responseData = data.data;

    if (data && data.data) {
      displaySystemComment(data.data);
    } else {
      throw new Error('Data is not in the expected format');
    }
  })
  .catch(error => {
    console.error('There was a problem fetching the system comment:', error);
    document.getElementById(
        'system-comment').innerHTML = '<p class="system-comment-error">시스템 댓글을 불러오는 데 실패했습니다.</p>';
  });
}

function displaySystemComment(system_comment) {
  console.log('System comment data:', JSON.stringify(system_comment, null, 2)); // 시스템 댓글 데이터를 로그에 출력

  if (!system_comment) {
    console.error('System comment data is undefined or null');
    document.getElementById(
        'system-comment').innerHTML = '<p class="system-comment-error">시스템 댓글 데이터가 없습니다.</p>';
    return;
  }

  // 줄바꿈 문자를 <br> 태그로 변환하는 함수
  function nl2br(str) {
    return str.replace(/\n/g, '<br>');
  }

  const content = `

    <p>${system_comment.nickname || 'Anonymous'}</p>
    <div class="system-comment-details">
      <div class="system-comment-info">
        <p>생성일: ${system_comment.createdAt ? new Date(system_comment.createdAt).toLocaleString() : 'N/A'}</p>
      </div>
    
      <div class="report">
        <details class="code-report">
         <summary>코드 리포트</summary>
          <p class="code-report-details">${nl2br(system_comment.codeReport || 'No code report available')}</p>
        </details>
        <details class="problem-report">
          <summary>문제 리포트</summary>
          <p class="problem-report-details">${nl2br(system_comment.problemReport || 'No problem report available')}</p>
        </details>
      </div>
    </div>
  `;

  document.getElementById('system-comment').innerHTML = content;
}
