document.addEventListener('DOMContentLoaded', function () {
  const urlParams = new URLSearchParams(window.location.search);
  const postId = urlParams.get('postId') || 31;
  // const postId = 4;

  if (postId) {
    fetchPostDetails(postId);
  } else {
    console.error('No postId provided in URL');
  }
});

// const token = localStorage.getItem('authToken');
const token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLthYzsiqTthLAxIiwidXNlcl9pZCI6MSwiZW1haWwiOiJ0ZXN0ZXIxQG5hdmVyLmNvbSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzI1OTgxOTM4LCJuYmYiOjE3MjU5ODE5MzgsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MSIsImV4cCI6MTcyNTk4MzczOH0.jN8R2Uf8TM5TVQ3b-1tas3x6pcHvTV3xOfV4ZwSEaxw";

function fetchPostDetails(postId) {
  const url = `http://localhost:8080/api/v1/posts/${postId}`;
  fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    }
  })
  .then(response =>
      response.json())
  .then(data => {
    console.log('Received data:', JSON.stringify(data, null, 2)); // 데이터 구조를 자세히 로그에 출력

    if (data) {
      updatePostDetails(data);
    } else {
      console.error('No data received from server');
    }
  })
  .catch(error => {
    console.error('Error fetching post details:', error);
  });
}

function updatePostDetails(post) {

  console.log('추천 수: ', post.voteCnt);

  const content = `
    <p>${ post.title || '알수 없음'}</p>
    <div class="post-info">
      <p>작성자: ${ post.userNickname || '알수 없음'}</p>
      <p>작성일: ${ post.createdAt ? new Date(post.createdAt).toLocaleString() : '알수 없음'}</p>
      <p>수정일: ${ post.modifiedAt ? new Date(post.modifiedAt).toLocaleString() : '알수 없음'}</p>
      <p>사용 언어: ${ post.languageName || '알수 없음'}</p>
      <p>${ post.platformName || '알수 없음'} ${ post.problemNumber || '알수 없음'} 번 문제</p>
      <p>알고리즘 분류: ${ post.algorithmName || '알수 없음'}</p>
      <p>${ post.viewCnt || '0'} views ${ post.voteCnt || '0'} votes</p>
    </div>
  
    <hr>
  
    <div class="post-content">${ post.content || '알수 없음'}</div>
  
    <div class="post-code">
      <pre><code>${ post.code || '알수 없음'}</code></pre>
    </div>
  
    <div class="post-actions">
      <div class="vote-button">
        <button class="vote">Vote</button>
        <p class="post-vote-cnt">${ post.voteCnt || '0'}</p>
      </div>
    </div>
  `;

  document.getElementById('post-detail').innerHTML = content

}
