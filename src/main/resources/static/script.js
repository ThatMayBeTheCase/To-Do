const form = document.querySelector('#todo-form');
const input = document.querySelector('#todo-input');
const list = document.querySelector('#todo-list');
const counter = document.querySelector('#todo-counter');
const filters = document.querySelectorAll('.filters button');

const API_URL = '/api/tasks';
const USERS_URL = '/api/users';
const CATEGORIES_URL = '/api/categories';

let tasks = [];
let currentFilter = 'all';
let defaultUserId = null;
let defaultCategoryId = null;

loadTasks();

form.addEventListener('submit', async event => {
    event.preventDefault();
    const text = input.value.trim();
    if (!text) return;

    const createdTask = await request(API_URL, {
        method: 'POST',
        body: JSON.stringify({
            title: text,
            description: '',
            completed: false,
            userId: defaultUserId,
            categoryId: defaultCategoryId
        })
    });

    tasks.push({
        id: createdTask.id,
        text: createdTask.title,
        description: createdTask.description,
        completed: createdTask.completed,
        userId: createdTask.userId,
        categoryId: createdTask.categoryId
    });

    render();
    form.reset();
});

list.addEventListener('click', async event => {
    const { target } = event;
    const item = target.closest('li');

    if (!item) return;

    const id = Number(item.dataset.id);
    const task = tasks.find(task => task.id === id);

    if (!task) return;

    if (target.matches('[data-action="toggle"]')) {
        const updatedTask = await request(`${API_URL}/${id}`, {
            method: 'PUT',
            body: JSON.stringify({
                title: task.text,
                description: task.description,
                completed: !task.completed,
                userId: task.userId,
                categoryId: task.categoryId
            })
        });

        tasks = tasks.map(task =>
            task.id === id ? mapTask(updatedTask) : task
        );

        render();
    }

    if (target.matches('[data-action="delete"]')) {
        await request(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        tasks = tasks.filter(task => task.id !== id);
        render();
    }
});

filters.forEach(button => {
    button.addEventListener('click', () => {
        filters.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');
        currentFilter = button.dataset.filter;
        render();
    });
});

async function loadTasks() {
    await ensureDefaultData();

    const data = await request(API_URL);

    tasks = data.map(mapTask);

    render();
}

async function ensureDefaultData() {
    let users = await request(USERS_URL);
    let categories = await request(CATEGORIES_URL);

    if (users.length === 0) {
        const user = await request(USERS_URL, {
            method: 'POST',
            body: JSON.stringify({
                name: 'Demo User',
                email: 'demo@example.com'
            })
        });

        users = [user];
    }

    if (categories.length === 0) {
        const category = await request(CATEGORIES_URL, {
            method: 'POST',
            body: JSON.stringify({
                name: 'General'
            })
        });

        categories = [category];
    }

    defaultUserId = users[0].id;
    defaultCategoryId = categories[0].id;
}

function mapTask(task) {
    return {
        id: task.id,
        text: task.title,
        description: task.description,
        completed: task.completed,
        userId: task.userId,
        categoryId: task.categoryId
    };
}

function render() {
    const filtered = tasks.filter(task => {
        if (currentFilter === 'active') return !task.completed;
        if (currentFilter === 'completed') return task.completed;
        return true;
    });

    list.innerHTML = filtered
        .map(
            task => `
            <li data-id="${task.id}" class="${task.completed ? 'completed' : ''}">
                <span>${escapeHtml(task.text)}</span>
                <div>
                    <button data-action="toggle">
                        ${task.completed ? 'Undo' : 'Done'}
                    </button>
                    <button data-action="delete">Delete</button>
                </div>
            </li>`
        )
        .join('');

    const remaining = tasks.filter(task => !task.completed).length;
    counter.textContent = `${tasks.length} task${tasks.length !== 1 ? 's' : ''} · ${remaining} left`;
}

async function request(url, options = {}) {
    const response = await fetch(url, {
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        },
        ...options
    });

    if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
    }

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function escapeHtml(value) {
    return value
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}
