let form = document.getElementById("basketForm");

form.addEventListener("submit", function(event) {
    event.preventDefault();

    var formData = new FormData(form);

    fetch("/individual_order", {
        method: "POST",
        body: formData // Передаем данные из формы в теле запроса
    })
        .then(response => {
            if (response.ok) {
                console.log("Данные успешно отправлены на сервер");
                localStorage.removeItem('basketCount');
                localStorage.removeItem('basket');
                location.reload();
            } else {
                console.error("Произошла ошибка при отправке данных на сервер");
            }
        })
        .catch(error => {
            console.error("Ошибка при отправке данных на сервер:", error);
        });
});
