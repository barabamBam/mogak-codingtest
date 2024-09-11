export async function showError(container, response) {
    let json = await response.json()
    const error = document.querySelector('.error') ?? document.createElement('p');
    error.classList.add('error');
    error.textContent = json.reason;
    container.insertAdjacentElement('afterbegin', error);
}