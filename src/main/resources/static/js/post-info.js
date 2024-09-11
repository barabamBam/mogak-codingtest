document.addEventListener('DOMContentLoaded', function() {
  // 컴포넌트 로드
  Promise.all([
    fetch('../header/header.html').then(response => response.text()),
    fetch('post-detail.html').then(response => response.text()),
    fetch('system-comment.html').then(response => response.text()),
    fetch('comment.html').then(response => response.text())
  ]).then(([header, postDetail, systemComment, comment]) => {
    document.getElementById('header').innerHTML = header;
    document.getElementById('post-detail').innerHTML = postDetail;
    document.getElementById('system-comment').innerHTML = systemComment;
    document.getElementById('comment-section').innerHTML = comment;

    // 모든 컴포넌트가 로드된 후 system-comment.js의 초기화 함수 호출
    if (typeof window.initSystemComment === 'function') {
      window.initSystemComment();
    } else {
      console.error('initSystemComment function not found');
    }
  });

  // 추천/비추천 기능
  // document.body.addEventListener('click', function(e) {
  //   if (e.target.classList.contains('upvote')) {
  //     alert('추천되었습니다!');
  //   } else if (e.target.classList.contains('downvote')) {
  //     alert('비추천되었습니다!');
  //   }
  // });

  // 댓글 작성 기능
  const commentForm = document.querySelector('.comment-form');
  if (commentForm) {
    commentForm.addEventListener('submit', function(e) {
      e.preventDefault();
      const textarea = this.querySelector('textarea');
      if (textarea.value.trim()) {
        alert('댓글이 작성되었습니다: ' + textarea.value);
        textarea.value = '';
      }
    });
  }

  // 수정 버튼 클릭 이벤트 처리
  const editButton = document.getElementById('post-edit-btn');
  if (editButton) {
    editButton.addEventListener('click', function() {
      const urlParams = new URLSearchParams(window.location.search);
      const postId = urlParams.get('postId');
      if (postId) {
        window.location.href = `post-modify.html?postId=${postId}`;
      } else {
        console.error('No postId found');
      }
    });
  }
  const deleteButton = document.getElementById('post-delete-btn');
  if (deleteButton) {
    deleteButton.addEventListener('click', deletePost);
  }

});

function deletePost() {
  const urlParams = new URLSearchParams(window.location.search);
  const postId = urlParams.get('postId');

  if (!postId) {
    alert('게시글 ID를 찾을 수 없습니다.');
    return;
  }

  if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
    return;
  }

  // const token = localStorage.getItem('authToken');
  const token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLthYzsiqTthLAxIiwidXNlcl9pZCI6MSwiZW1haWwiOiJ0ZXN0ZXIxQG5hdmVyLmNvbSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzI1OTgyNjczLCJuYmYiOjE3MjU5ODI2NzMsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MSIsImV4cCI6MTcyNTk4NDQ3M30.tux31PzPQgN_j3dPj5AAF4see2NE1xkR70U3O0DT1JA";

  fetch(`http://localhost:8080/api/v1/posts/${postId}`, {
    method: 'DELETE',
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
    alert(data.message);
    window.location.href = '/mogakcote/src/main/resources/templates/post/list.html';
  })
  .catch(error => {
    console.error('Error:', error);
    alert('게시글 삭제 중 오류가 발생했습니다.');
  });
}