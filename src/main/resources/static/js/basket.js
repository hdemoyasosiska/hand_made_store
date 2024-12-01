function toCurrency(num) {
    return new Intl.NumberFormat("ru-RU", {
        style: "currency",
        currency: "RUB",
        minimumFractionDigits: 0,
    }).format(num);
}

let basket = {};//Корзина
let basketCount = {};//Количесвто товаров в корзине

$('document').ready(function(){
    checkCart();
    showGoods();
});

let total = 0;
let totalPriceField = document.getElementById('totalPrice');
let shoppingCart = document.getElementById('shoppingCart');

//Загрузка товаров на страницу
function showGoods(){
    let out = '';
    total = 0
    let str
    for (let key in basket){
        out += '<div class="pic-container">';
        //out += '<a href="'+basket[key].link+'">';
        out += '<div class="card__top">'
        out += '<img alt="" class="card__image" src="/static/img/'+basket[key]['image']+'">';
        out += '</div>'
        //out += '</a>';
        out += '<div class="Inform">';
        out += '<p class="card__title">'+basket[key]['name']+'</p>';
        out += '<div class="popup__product-wrap">';
        out += '<p id='+key+' class="card__prices"><b>'+toCurrency(basketCount[key]*basket[key]['price'])+'</b></p>'
        out += '<button class="minus" name='+basket[key]['name']+'>–</button>'
        str = key + "quantity"
        out += '<p id='+str+' class="quantity">'+basketCount[key]+'</p>'
        out += '<button class="plus" name='+basket[key]['name']+'>+</button>'
        out +=  '<button class="popup__product-delete" name='+basket[key]['name']+'>✖</button>';
        out += '</div>';
        out += '</div>';
        out += '</div>';
        total += basketCount[key]*basket[key]['price'];
    }

    totalPriceField.value = total
    shoppingCart.value = JSON.stringify(basket);

    /*localStorage.setItem('basket', JSON.stringify(basket));*/
    $('.price').html(toCurrency(total))
    $('.List').html(out);
    $('.plus').on('click', plusGoods);
    $('.minus').on('click', minusGoods);
    $('.popup__product-delete').on('click', function() {
        deleteGoods($(this).attr('name'));
    });
}

function plusGoods(){
    let articular = $(this).attr('name');
    console.log(articular)
    if (basketCount[articular] < basket[articular]['quantity']) {
        basketCount[articular]++;
        localStorage.setItem('basketCount', JSON.stringify(basketCount));
        total += basket[articular]['price'];
        showPrice(articular)
    }
}

function minusGoods(){
    let articular = $(this).attr('name');
    basketCount[articular]--;
    total -= basket[articular]['price'];
    showPrice(articular);

    if (basketCount[articular] == 0){
        deleteGoods(articular);
    }
}

function deleteGoods(articular){ // изменяем название переменной на articular
    delete basket[articular];
    delete basketCount[articular];
    localStorage.setItem('basket', JSON.stringify(basket));
    localStorage.setItem('basketCount', JSON.stringify(basketCount));
    showGoods();
}

function showPrice(articular){
    let cost = document.getElementById(articular);
    let id = articular + "quantity"
    let count = document.getElementById(id);

    cost.textContent = toCurrency(basketCount[articular]*basket[articular]['price'])
    count.textContent = basketCount[articular];

    $('.price').html(toCurrency(total))

    totalPriceField.value = total;
}

function checkCart(){
    if (localStorage.getItem('basket') != null) {
        basket = JSON.parse(localStorage.getItem('basket'))
        basketCount = JSON.parse(localStorage.getItem('basketCount'))
    }
}

let form = document.getElementById("basketForm");

form.addEventListener("submit", function(event) {
    event.preventDefault();

    var formData = new FormData(form);

    fetch("/basket", {
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
