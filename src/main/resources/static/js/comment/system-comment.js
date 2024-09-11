(function() {
  function initSystemComment() {
    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('postId') || 31;
    fetchSystemComment(postId);
  }

  const token = localStorage.getItem('access_token');

  function fetchSystemComment(postId) {
    const url = `http://localhost:8080/api/v1/posts/${postId}/system-comments`;
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
      console.log('Received system comment data:', JSON.stringify(data, null, 2));
      if(!data.data) {
        return;
      }
      else if (data && data.data) {
        displaySystemComment(data.data);
      } else {
        throw new Error('Data is not in the expected format');
      }
    })
    .catch(error => {
      console.error('There was a problem fetching the system comment:', error);
      document.getElementById('system-comment').innerHTML = '<p class="system-comment-error">시스템 댓글을 불러오는 데 실패했습니다.</p>';
    });
  }

  function displaySystemComment(system_comment) {
    console.log('System comment data:', JSON.stringify(system_comment, null, 2));

    if (!system_comment) {
      console.error('System comment data is undefined or null');
      document.getElementById('system-comment').innerHTML = '<p class="system-comment-error">시스템 댓글 데이터가 없습니다.</p>';
      return;
    }

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

  // 전역 객체에 함수 노출
  window.initSystemComment = initSystemComment;
})();