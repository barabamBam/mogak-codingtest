function getPostIdFromUrl() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('postId') || 1;
}

function fetchComments(postId) {
  const token = localStorage.getItem('authToken');
  const url = `http://localhost:8081/api/v1/posts/${postId}/comments/list`;

  console.log("댓글 목록");

  fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(err => { throw err; });
    }
    return response.json();
  })
  .then(data => {
    if (data && data.data) {
      displayComments(data.data);
    } else {
      throw new Error('Data is not in the expected format');
    }
  })
  .catch(error => {
    console.error('There was a problem fetching the comments:', error);
    document.querySelector('.comment-list').innerHTML = `<p class="comment-error">${error.errorMessage || '댓글 목록을 불러오는 데 실패했습니다.'}</p>`;
  });
}function displayComments(comments) {
  const commentList = document.querySelector('.comment-list');

  if (!comments || comments.length === 0) {
    commentList.innerHTML = '<p class="comment-error">댓글이 없습니다.</p>';
    return;
  }

  const commentsHtml = comments.map(comment => `
    <div class="comment-item" data-comment-id="${comment.id}">
      <div class="comment-info">
        <span class="author">${comment.nickname}</span>
        <span class="date">${formatDate(comment.createdAt)}</span>
      </div>
      <div class="comment-content">${comment.comment}</div>
      <div class="comment-edit-form" style="display: none;">
        <textarea class="edit-comment-textarea">${comment.comment}</textarea>
        <button class="update-comment-btn">수정</button>
        <button class="cancel-edit-btn">취소</button>
      </div>
      <div class="comment-actions">
        <button class="edit-comment-btn">수정</button>
        <button class="delete-comment-btn">삭제</button>
      </div>
    </div>
  `).join('');

  commentList.innerHTML = commentsHtml;
  addCommentActionListeners();
}

function addCommentActionListeners() {
  document.querySelectorAll('.edit-comment-btn').forEach(button => {
    button.addEventListener('click', handleEditComment);
  });
  document.querySelectorAll('.delete-comment-btn').forEach(button => {
    button.addEventListener('click', handleDeleteComment);
  });
  document.querySelectorAll('.update-comment-btn').forEach(button => {
    button.addEventListener('click', handleUpdateComment);
  });
  document.querySelectorAll('.cancel-edit-btn').forEach(button => {
    button.addEventListener('click', handleCancelEdit);
  });
}

function handleEditComment(event) {
  const commentItem = event.target.closest('.comment-item');
  commentItem.querySelector('.comment-content').style.display = 'none';
  commentItem.querySelector('.comment-edit-form').style.display = 'block';
  commentItem.querySelector('.comment-actions').style.display = 'none';
}

function handleUpdateComment(event) {
  const commentItem = event.target.closest('.comment-item');
  const commentId = commentItem.dataset.commentId;
  const newContent = commentItem.querySelector('.edit-comment-textarea').value;
  updateComment(commentId, newContent);
}

function handleCancelEdit(event) {
  const commentItem = event.target.closest('.comment-item');
  commentItem.querySelector('.comment-content').style.display = 'block';
  commentItem.querySelector('.comment-edit-form').style.display = 'none';
  commentItem.querySelector('.comment-actions').style.display = 'block';
}

function updateComment(commentId, newContent) {
  const postId = getPostIdFromUrl();
  const token = localStorage.getItem('authToken');
  const url = `http://localhost:8081/api/v1/posts/${postId}/comments/${commentId}`;

  fetch(url, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
    body: JSON.stringify({ content: newContent })
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(err => { throw err; });
    }
    return response.json();
  })
  .then(data => {
    console.log('Comment updated successfully:', data);
    fetchComments(postId);  // 댓글 목록 새로고침
  })
  .catch(error => {
    console.error('There was a problem updating the comment:', error);
    alert(error.errorMessage || '댓글 수정에 실패했습니다. 다시 시도해 주세요.');
  });
}

function handleDeleteComment(event) {
  const commentItem = event.target.closest('.comment-item');
  const commentId = commentItem.dataset.commentId;

  if (confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
    deleteComment(commentId);
  }
}

function deleteComment(commentId) {
  const postId = getPostIdFromUrl();
  const token = localStorage.getItem('authToken');
  const url = `http://localhost:8081/api/v1/posts/${postId}/comments/${commentId}`;

  fetch(url, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(err => { throw err; });
    }
    return response.json();
  })
  .then(data => {
    console.log('Comment deleted successfully:', data);
    fetchComments(postId);  // 댓글 목록 새로고침
  })
  .catch(error => {
    console.error('There was a problem deleting the comment:', error);
    alert(error.errorMessage || '댓글 삭제에 실패했습니다. 다시 시도해 주세요.');
  });
}

function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

function setupCommentForm(postId) {
  const submitButton = document.querySelector('.comment-save-btn');
  if (submitButton) {
    submitButton.addEventListener('click', function(e) {
      e.preventDefault();
      const textarea = document.querySelector('.comment-write');
      if (textarea) {
        const comment = textarea.value.trim();
        if (comment) {
          submitComment(postId, comment);
        }
      } else {
        console.error('Comment textarea not found');
      }
    });
    console.log('Comment submit button found and event listener attached');
  } else {
    console.error('Comment submit button not found');
  }
}

function submitComment(postId, comment) {
  const token = localStorage.getItem('authToken');
  const url = `http://localhost:8081/api/v1/posts/${postId}/comments`;

  console.log("댓글 작성");

  fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
    body: JSON.stringify({ content: comment })
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(err => { throw err; });
    }
    return response.json();
  })
  .then(data => {
    console.log('Comment submitted successfully:', data);
    // 댓글 목록 새로고침
    fetchComments(postId);
    // 텍스트 영역 비우기
    const textarea = document.querySelector('.comment-write');
    if (textarea) {
      textarea.value = '';
    }
  })
  .catch(error => {
    console.error('There was a problem submitting the comment:', error);
    let errorMessage = '댓글 작성에 실패했습니다. 다시 시도해 주세요.';
    if (error.errorMessage) {
      errorMessage = error.errorMessage;
    }
    alert(errorMessage);
  });
}function initializeComments() {
  const postId = getPostIdFromUrl();
  fetchComments(postId);
  setupCommentForm(postId);
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initializeComments);
} else {
  initializeComments();
}

const observer = new MutationObserver((mutations) => {
  mutations.forEach((mutation) => {
    if (mutation.type === 'childList') {
      const addedNodes = mutation.addedNodes;
      addedNodes.forEach((node) => {
        if (node.nodeType === Node.ELEMENT_NODE && node.classList.contains('comment-form')) {
          setupCommentForm(getPostIdFromUrl());
        }
      });
    }
  });
});

observer.observe(document.body, { childList: true, subtree: true });