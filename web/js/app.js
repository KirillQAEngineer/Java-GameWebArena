// Адрес REST API для работы с персонажами.
const API_URL = "/api/characters";

// Получаем элементы HTML, с которыми будет работать JavaScript.
const charactersList = document.getElementById("characters-list");
const characterForm = document.getElementById("character-form");
const characterIdInput = document.getElementById("character-id");
const formTitle = document.getElementById("form-title");
const submitButton = document.getElementById("submit-button");
const cancelButton = document.getElementById("cancel-button");
const refreshButton = document.getElementById("refresh-button");
const notification = document.getElementById("notification");

// Находим поля формы по их идентификаторам.
const nameInput = document.getElementById("name");
const healthInput = document.getElementById("health");
const attackInput = document.getElementById("attack");
const experienceInput = document.getElementById("experience");
const levelInput = document.getElementById("level");

// Загружает список персонажей с backend.
async function loadCharacters() {
    try {
        // Отправляем GET-запрос на получение всех персонажей.
        const response = await fetch(API_URL);

        // Проверяем HTTP-статус ответа.
        if (!response.ok) {
            throw new Error("Не удалось загрузить персонажей.");
        }

        // Преобразуем JSON-ответ в JavaScript-массив.
        const characters = await response.json();

        // Отображаем полученных персонажей на странице.
        renderCharacters(characters);
    } catch (error) {
        // Показываем пользователю сообщение об ошибке.
        showNotification(error.message, "error");
    }
}


// Создаёт карточки персонажей на основе полученного массива.
function renderCharacters(characters) {
    // Очищаем старое содержимое списка перед новой отрисовкой.
    charactersList.replaceChildren();

    // Показываем сообщение, если сервер вернул пустой список.
    if (characters.length === 0) {
        const emptyMessage = document.createElement("p");
        emptyMessage.className = "empty-message";
        emptyMessage.textContent = "Персонажей пока нет.";
        charactersList.appendChild(emptyMessage);
        return;
    }

    // Создаём отдельную карточку для каждого персонажа.
    characters.forEach((character) => {
        // Создаём основной контейнер и сохраняем тестовые атрибуты.
        const card = document.createElement("article");
        card.className = "character-card";
        card.dataset.testid = "character-card";
        card.dataset.characterId = character.id;

        // Определяем тип портрета по имени персонажа.
        const normalizedName = String(character.name).toLowerCase();
        let characterClass = "adventurer";
        let characterRole = "ADVENTURER";

        if (normalizedName.includes("warrior")) {
            characterClass = "warrior";
            characterRole = "WARRIOR";
        } else if (normalizedName.includes("mage")) {
            characterClass = "mage";
            characterRole = "MAGE";
        }

        // Создаём верхнюю часть карточки с портретом и именем.
        const header = document.createElement("div");
        header.className = "character-header";

        const portrait = document.createElement("img");
        portrait.className = "character-avatar";
        portrait.src = `/assets/characters/${characterClass}.svg`;
        portrait.alt = `Портрет персонажа ${character.name}`;

        const heading = document.createElement("div");
        heading.className = "character-heading";

        const title = document.createElement("h3");
        title.textContent = character.name;

        const role = document.createElement("span");
        role.className = "character-class";
        role.textContent = characterRole;

        heading.append(title, role);
        header.append(portrait, heading);
        card.appendChild(header);

        // Показываем идентификатор персонажа отдельно от характеристик.
        const idLabel = document.createElement("div");
        idLabel.className = "character-id-label";
        idLabel.textContent = `HERO ID: ${character.id}`;
        card.appendChild(idLabel);

        // Создаём сетку игровых характеристик.
        const stats = document.createElement("div");
        stats.className = "character-stats";

        // Формируем список характеристик с подписями и значениями.
        const statValues = [
            { label: "HP", value: character.health },
            { label: "ATK", value: character.attack },
            { label: "EXP", value: character.experience },
            { label: "LVL", value: character.level }
        ];

        statValues.forEach((stat) => {
            const item = document.createElement("div");
            item.className = "stat-item";

            const label = document.createElement("span");
            label.className = "stat-label";
            label.textContent = stat.label;

            const value = document.createElement("strong");
            value.className = "stat-value";
            value.textContent = stat.value;

            item.append(label, value);
            stats.appendChild(item);
        });

        card.appendChild(stats);

        // Создаём визуальную шкалу здоровья.
        const healthSection = document.createElement("div");
        healthSection.className = "progress-section";

        const healthLabel = document.createElement("div");
        healthLabel.className = "progress-label";
        healthLabel.textContent = `HEALTH: ${character.health}`;

        const healthTrack = document.createElement("div");
        healthTrack.className = "progress-track";

        const healthFill = document.createElement("div");
        healthFill.className = "progress-fill health-fill";

        // Ограничиваем ширину шкалы диапазоном от 0 до 100 процентов.
        const healthPercent = Math.max(0, Math.min(Number(character.health), 100));
        healthFill.style.width = `${healthPercent}%`;

        healthTrack.appendChild(healthFill);
        healthSection.append(healthLabel, healthTrack);
        card.appendChild(healthSection);

        // Создаём визуальную шкалу опыта.
        const experienceSection = document.createElement("div");
        experienceSection.className = "progress-section";

        const experienceLabel = document.createElement("div");
        experienceLabel.className = "progress-label";
        experienceLabel.textContent = `EXPERIENCE: ${character.experience}`;

        const experienceTrack = document.createElement("div");
        experienceTrack.className = "progress-track";

        const experienceFill = document.createElement("div");
        experienceFill.className = "progress-fill experience-fill";

        // Для отображения опыта используем шкалу от 0 до 100.
        const experiencePercent = Math.max(
            0,
            Math.min(Number(character.experience), 100)
        );
        experienceFill.style.width = `${experiencePercent}%`;

        experienceTrack.appendChild(experienceFill);
        experienceSection.append(experienceLabel, experienceTrack);
        card.appendChild(experienceSection);

        // Создаём контейнер кнопок управления персонажем.
        const actions = document.createElement("div");
        actions.className = "card-actions";

        // Кнопка редактирования сохраняет прежний test id и обработчик.
        const editButton = document.createElement("button");
        editButton.type = "button";
        editButton.textContent = "Редактировать";
        editButton.dataset.testid = "edit-button";

        editButton.addEventListener("click", () => {
            editCharacter(character);
        });

        // Кнопка удаления также сохраняет прежний test id и обработчик.
        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.textContent = "Удалить";
        deleteButton.className = "delete-button";
        deleteButton.dataset.testid = "delete-button";

        deleteButton.addEventListener("click", () => {
            deleteCharacter(character.id);
        });

        // Добавляем кнопки и завершаем сборку карточки.
        actions.append(editButton, deleteButton);
        card.appendChild(actions);
        charactersList.appendChild(card);
    });
}


// Отправляет POST-запрос и создаёт нового персонажа.
async function createCharacter(character) {
    // Выполняем запрос на создание персонажа.
    const response = await fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(character)
    });

    // Если сервер вернул ошибку, формируем понятное сообщение.
    if (!response.ok) {
        throw new Error(await getErrorMessage(response));
    }

    // Возвращаем созданного персонажа из ответа сервера.
    return response.json();
}

// Отправляет PUT-запрос и обновляет существующего персонажа.
async function updateCharacter(id, character) {
    // Формируем адрес конкретного персонажа.
    const url = `${API_URL}/${id}`;

    // Отправляем запрос на обновление.
    const response = await fetch(url, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(character)
    });

    // Проверяем результат обновления.
    if (!response.ok) {
        throw new Error(await getErrorMessage(response));
    }

    // Возвращаем обновлённые данные.
    return response.json();
}

// Отправляет DELETE-запрос и удаляет персонажа.
async function deleteCharacter(id) {
    // Запрашиваем подтверждение удаления.
    const confirmed = window.confirm("Удалить этого персонажа?");

    // Если пользователь отменил действие, ничего не делаем.
    if (!confirmed) {
        return;
    }

    try {
        // Отправляем DELETE-запрос для выбранного ID.
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE"
        });

        // Проверяем успешность удаления.
        if (!response.ok) {
            throw new Error(await getErrorMessage(response));
        }

        // Показываем сообщение об успешном удалении.
        showNotification("Персонаж успешно удалён.", "success");

        // Повторно загружаем список с сервера.
        await loadCharacters();
    } catch (error) {
        // Показываем сообщение об ошибке.
        showNotification(error.message, "error");
    }
}

// Заполняет форму данными персонажа для редактирования.
function editCharacter(character) {
    // Сохраняем ID, чтобы понять, что выполняется обновление.
    characterIdInput.value = character.id;

    // Заполняем все поля текущими значениями персонажа.
    nameInput.value = character.name;
    healthInput.value = character.health;
    attackInput.value = character.attack;
    experienceInput.value = character.experience;
    levelInput.value = character.level;

    // Меняем заголовок формы.
    formTitle.textContent = "Редактировать персонажа";

    // Меняем текст основной кнопки.
    submitButton.textContent = "Сохранить изменения";

    // Показываем кнопку отмены редактирования.
    cancelButton.hidden = false;

    // Перемещаем форму в область видимости пользователя.
    characterForm.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

// Возвращает форму в режим создания персонажа.
function resetForm() {
    // Сбрасываем значения всех полей.
    characterForm.reset();

    // Очищаем ID, чтобы следующий запрос был POST.
    characterIdInput.value = "";

    // Возвращаем исходный заголовок формы.
    formTitle.textContent = "Создать персонажа";

    // Возвращаем исходный текст кнопки.
    submitButton.textContent = "Создать персонажа";

    // Скрываем кнопку отмены.
    cancelButton.hidden = true;
}

// Показывает уведомление об успехе или ошибке.
function showNotification(message, type) {
    // Устанавливаем текст сообщения.
    notification.textContent = message;

    // Устанавливаем CSS-класс типа уведомления.
    notification.className = `notification ${type}`;
}

// Получает текст ошибки от backend, если он присутствует.
async function getErrorMessage(response) {
    // Сначала пробуем прочитать ответ как JSON.
    try {
        const errorBody = await response.json();

        // Используем сообщение backend, если оно доступно.
        if (errorBody.message) {
            return errorBody.message;
        }

        // Если сообщение отсутствует, показываем HTTP-статус.
        return `Ошибка сервера: HTTP ${response.status}`;
    } catch {
        // Если тело ответа не JSON, возвращаем общий текст ошибки.
        return `Ошибка сервера: HTTP ${response.status}`;
    }
}

// Обрабатывает отправку формы создания или редактирования.
characterForm.addEventListener("submit", async (event) => {
    // Отменяем стандартную перезагрузку страницы браузером.
    event.preventDefault();

    // Собираем данные формы в объект Character.
    const character = {
        name: nameInput.value.trim(),
        health: Number(healthInput.value),
        attack: Number(attackInput.value),
        experience: Number(experienceInput.value),
        level: Number(levelInput.value)
    };

    // Читаем ID: пустое значение означает создание нового персонажа.
    const id = characterIdInput.value;

    try {
        // Если ID есть, обновляем персонажа через PUT.
        if (id) {
            await updateCharacter(id, character);
            showNotification("Персонаж успешно обновлён.", "success");
        } else {
            // Если ID нет, создаём персонажа через POST.
            await createCharacter(character);
            showNotification("Персонаж успешно создан.", "success");
        }

        // Возвращаем форму в исходное состояние.
        resetForm();

        // Загружаем актуальный список с backend.
        await loadCharacters();
    } catch (error) {
        // Показываем ошибку, не очищая введённые пользователем данные.
        showNotification(error.message, "error");
    }
});

// Обрабатываем нажатие кнопки отмены редактирования.
cancelButton.addEventListener("click", () => {
    // Возвращаем форму в режим создания.
    resetForm();
});

// Обрабатываем нажатие кнопки обновления списка.
refreshButton.addEventListener("click", () => {
    // Повторно запрашиваем список персонажей у backend.
    loadCharacters();
});

// Загружаем персонажей сразу после открытия страницы.
loadCharacters();
