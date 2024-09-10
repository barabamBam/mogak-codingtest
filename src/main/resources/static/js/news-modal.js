let currentPage = 0;
const pageSize = 10;

document.addEventListener('DOMContentLoaded', () => {
  const newsModal = document.getElementById('news-modal');
  const newsInfoModal = document.getElementById('news-info-modal');
  const bellBtn = document.getElementById('news-bell-btn');
  const closeButtons = document.getElementsByClassName('close');
  const loadMoreBtn = document.getElementById('load-more-btn');

  bellBtn.onclick = () => {
    newsModal.style.display = 'block';
    currentPage = 0;
    document.getElementById('news-list').innerHTML = '';
    fetchNews();
  }

  for (let closeBtn of closeButtons) {
    closeBtn.onclick = () => {
      newsModal.style.display = 'none';
      newsInfoModal.style.display = 'none';
    }
  }

  window.onclick = (event) => {
    if (event.target == newsModal) {
      newsModal.style.display = 'none';
    }
    if (event.target == newsInfoModal) {
      newsInfoModal.style.display = 'none';
    }
  }

  loadMoreBtn.onclick = fetchNews;
});

document.addEventListener('DOMContentLoaded', () => {
  const newsModal = document.getElementById('news-modal');
  const newsInfoModal = document.getElementById('news-info-modal');
  const bellBtn = document.getElementById('news-bell-btn');
  const closeButtons = document.getElementsByClassName('close');
  const loadMoreBtn = document.getElementById('load-more-btn');
  const backBtn = document.getElementById('news-info-back-btn');

  bellBtn.onclick = () => {
    newsModal.style.display = 'block';
    currentPage = 0;
    document.getElementById('news-list').innerHTML = '';
    fetchNews();
  }

  for (let closeBtn of closeButtons) {
    closeBtn.onclick = () => {
      newsModal.style.display = 'none';
      newsInfoModal.style.display = 'none';
    }
  }

  window.onclick = (event) => {
    if (event.target == newsModal) {
      newsModal.style.display = 'none';
    }
    if (event.target == newsInfoModal) {
      newsInfoModal.style.display = 'none';
    }
  }

  loadMoreBtn.onclick = fetchNews;

  backBtn.onclick = () => {
    newsInfoModal.style.display = 'none';
    newsModal.style.display = 'block';
  }
});



function fetchNews() {
  const token = localStorage.getItem('authToken');
  const url = `http://localhost:8081/api/v1/news/list?page=${currentPage}&size=${pageSize}`;

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
  .then(responseData => {
    console.log('Received data:', responseData);
    if (responseData && responseData.data) {
      const data = responseData.data;
      displayNews(data.content);
      currentPage++;
      const loadMoreBtn = document.getElementById('load-more-btn');
      if (data.last) {
        loadMoreBtn.style.display = 'none';
      } else {
        loadMoreBtn.style.display = 'block';
      }
    } else {
      throw new Error('Data structure is not as expected');
    }
  })
  .catch(error => {
    console.error('There was a problem fetching the news:', error);
    document.getElementById('news-list').innerHTML = '<p>알림을 불러오는 데 실패했습니다.</p>';
  });
}

function displayNews(newsItems) {
  const newsList = document.getElementById('news-list');

  if (!Array.isArray(newsItems)) {
    console.error('newsItems is not an array:', newsItems);
    newsList.innerHTML = '<p>알림을 불러오는 데 실패했습니다.</p>';
    return;
  }

  if (newsItems.length === 0) {
    newsList.innerHTML = '<p>알림이 없습니다.</p>';
    return;
  }

  newsItems.forEach(news => {
    const newsItem = document.createElement('div');
    newsItem.className = `news-item ${news.viewed ? 'viewed' : 'unviewed'}`;
    newsItem.textContent = news.title;
    newsItem.onclick = () => fetchNewsDetails(news.id);
    newsList.appendChild(newsItem);
  });
}

function fetchNewsDetails(newsId) {
  const token = localStorage.getItem('authToken');
  const url = `http://localhost:8081/api/v1/news/${newsId}`;

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
  .then(responseData => {
    if (responseData && responseData.data) {
      displayNewsDetails(responseData.data);
      updateNewsItemStatus(newsId, true);
    } else {
      throw new Error('Data structure is not as expected');
    }
  })
  .catch(error => {
    console.error('There was a problem fetching the news details:', error);
  });
}

function updateNewsItemStatus(newsId, viewed) {
  const newsItem = document.querySelector(`.news-item[data-id="${newsId}"]`);
  if (newsItem) {
    newsItem.classList.remove('unviewed');
    newsItem.classList.add('viewed');
  }
}

function displayNews(newsItems) {
  const newsList = document.getElementById('news-list');

  if (!Array.isArray(newsItems)) {
    console.error('newsItems is not an array:', newsItems);
    newsList.innerHTML = '<p>알림을 불러오는 데 실패했습니다.</p>';
    return;
  }

  if (newsItems.length === 0) {
    newsList.innerHTML = '<p>알림이 없습니다.</p>';
    return;
  }

  newsItems.forEach(news => {
    const newsItem = document.createElement('div');
    newsItem.className = `news-item ${news.viewed ? 'viewed' : 'unviewed'}`;
    newsItem.textContent = news.title;
    newsItem.setAttribute('data-id', news.id);
    newsItem.onclick = () => fetchNewsDetails(news.id);
    newsList.appendChild(newsItem);
  });
}

function displayNewsDetails(news) {
  document.getElementById('news-info-title').textContent = news.title;
  document.getElementById('news-info-content').textContent = news.content;
  // document.getElementById('news-info-type').textContent = `${news.type}`;
  document.getElementById('news-info-created-at').textContent = `${new Date(news.createdAt).toLocaleString()}`;

  const relatedBtn = document.getElementById('news-info-related-btn');
  if (news.hasRelatedContent) {
    relatedBtn.style.display = 'block';
    relatedBtn.onclick = () => {
      console.log('Related content ID:', news.relatedContentId);
    };
  } else {
    relatedBtn.style.display = 'none';
  }

  document.getElementById('news-modal').style.display = 'none';
  document.getElementById('news-info-modal').style.display = 'block';
}