document.addEventListener('DOMContentLoaded', function() {
  // 컴포넌트 로드
  fetch('../header/header.html')
  .then(response => response.text())
  .then(html => document.getElementById('header').innerHTML = html);

  fetch('post-detail.html')
  .then(response => response.text())
  .then(html => document.getElementById('post-detail').innerHTML = html);

  fetch('system-comment.html')
  .then(response => response.text())
  .then(html => document.getElementById('system-comment').innerHTML = html);

  fetch('comment.html')
  .then(response => response.text())
  .then(html => document.getElementById('comment-section').innerHTML = html);

  // 추천/비추천 기능
  document.body.addEventListener('click', function(e) {
    if (e.target.classList.contains('upvote')) {
      alert('추천되었습니다!');
    } else if (e.target.classList.contains('downvote')) {
      alert('비추천되었습니다!');
    }
  });

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
});