let basket = {}; // Инициализация пустого объекта
let basketCount = {}; // Инициализация пустого объекта

function changeButtonColor1(button) {
    var articul = $(button).attr('quantity')
    if (basket[articul] == undefined){
        button.style.background =  'linear-gradient(to right, #800080, #ffc0cb)';
        button.innerHTML = 'Корзина';
    }
    else{
        button.style.background = 'grey';
        button.innerHTML = 'Добавлено';
    }
}

let sortedCart = {};
let cart = {};

$(document).ready(function(){
    getTotalLength(function(newLength) {
        length = newLength;
        loadGoods(0);
    });
});

function getTotalLength(callback) {
    let xhr = new XMLHttpRequest();
    let url = '/getLength';
    xhr.open('GET', url, true);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                let newLength = parseInt(xhr.responseText);
                callback(newLength);
            } else {
                console.error('Request failed with status:', xhr.status);
            }
        }
    };
    xhr.send();
}

function loadData(callback, page_number, countPerPage) {
    let xhr = new XMLHttpRequest();
    let url = '/getData?page_number=' + page_number + '&countPerPage=' + countPerPage;
    xhr.open('GET', url, true);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            let response = JSON.parse(xhr.responseText);
            callback(response.content);
        }
    };
    xhr.send();
}

let length = 0;

function checkLocalStorage(){
    if (localStorage.getItem('sortedCart') != null)
        sortedCart = JSON.parse(localStorage.getItem('sortedCart'))

    if (Object.keys(sortedCart).length === 0)
        sortedCart = Object.assign({}, cart);

    sortedCart = Object.assign({}, cart);
}

let special = {
    "66666": {
        "name": "SMTHsmth",
        "size": 20,
        "color": "white",
        "material": "iron",
        "price": 2799,
        "quantity": 3,
        "image": "img/shakal.jpg",
        "link": "product_page.html"
    }
}

function loadGoods(page_number){
    $("button.buy").remove();

    let countPerPage = $("#count_1").val();
    countPerPage = parseInt(countPerPage);
    if (isNaN(countPerPage)) {
        countPerPage = 3;
    }

    loadData(function(data) {
        cart = data;
        checkLocalStorage();
        checkGoods();
        generateProductList(page_number, countPerPage); // Вызов функции обновления контента здесь
    }, page_number, countPerPage);


}

function generateProductList(page_number, countPerPage) {

    sortedCart = onChange(page_number)

    let out = '';

    if (length < countPerPage)
        countPerPage = length

    let pagesCount = Math.ceil(length / countPerPage);

    for (let i = 0; i < Math.min(countPerPage, Object.keys(sortedCart).length); i++) {
        out += '<div class="pic-container">';
        out += '<div class="card__top">'
        out += '<a href="'+sortedCart[i]['link']+'" class="card__image">';
        out += '<img src="/static/img/'+sortedCart[i]['image']+'"  width="200" height="200">';
        out += '</a>';
        out += '</div>'

        out += '<div class="Inform">';
        out += '<p class="card__title">'+sortedCart[i]['name']+'</p>';
        out += '</div>';
        out += '<div class="Inform">';
        out += '<p class="card__prices"><b>'+toCurrency(sortedCart[i]['price'])+'</b></p>'
        out += '<p id="quantity">'+(sortedCart[i]['quantity'])+' шт.</p>'
        out += '<button name="'+sortedCart[i]['name']+'" class="buy" onclick="changeButtonColor(this)">В корзину</button>';
        out += '</div>';
        out += '</div>';
    }

    $('#product_list').html(out);

    $('button.buy').click();
    $('button.buy').on('click', addToCart);

    $('button.buy').each(function() {
        changeButtonColor(this);
    });

    // remove all existing paging buttons
    $("button.pgn-bnt-styled").remove();

    //add paging buttons
    for (let i = 0; i < pagesCount; i++) {
        let button_tag = "<button>" + (i + 1) + ("</button>");
        let btn = $(button_tag)
            .attr('id', "paging_button_" + i)
            .attr('onclick', "loadGoods(" + i + ")")
            .addClass('pgn-bnt-styled');
        $('#paging_buttons').append(btn);
    }

    // make current page
    if (page_number !== null) {
        let identifier = "#paging_button_" + page_number;
        $(identifier).css("color", "red").css("font-weight", "bold");
    } else {
        $("#paging_button_0").css("color", "red").css("font-weight", "bold");
    }
}

function changeButtonColor(button) {
    let articul = $(button).attr('name')
}

function toCurrency(num) {
    return new Intl.NumberFormat("ru-RU", {
        style: "currency",
        currency: "RUB",
        minimumFractionDigits: 0,
    }).format(num);
}

//Добавление в корзину с помощью localStorage
function addToCart(){
    let articular = $(this).attr('name')





    if (articular === undefined)
        return;
    if (basket[articular] === undefined) {
        for (let i = 0; i < Object.keys(sortedCart).length; i++) {
            if (sortedCart[i]['name'] === articular) {
                basket[articular] = sortedCart[i];
                basketCount[articular] = 1;
                break;
            }
        }
    }

    else{
        delete basket[articular];
        delete basketCount[articular];
    }


    localStorage.setItem('basket', JSON.stringify(basket));
    localStorage.setItem('basketCount', JSON.stringify(basketCount));

    changeButtonColor(this);

    var cartNumElement = document.getElementById("cart_num");
    var keys = Object.keys(basketCount);
    var count = keys.length;
    console.log(count, keys)
    cartNumElement.innerHTML = count.toString();

}

function checkGoods(){
    if (localStorage.getItem('basket') != null) {
        basket = JSON.parse(localStorage.getItem('basket'))
    }

    if (localStorage.getItem('basketCount') != null) {
        basketCount = JSON.parse(localStorage.getItem('basketCount'))
    }



    var cartNumElement = document.getElementById("cart_num");
    var keys = Object.keys(basketCount);
    var count = keys.length;
    console.log(count, keys)
    cartNumElement.innerHTML = count.toString();
}

let dropdownList = document.getElementById("typeSorting");

dropdownList.onchange = function() {
    let currentPageNumber = getCurrentPageNumber(); // Получаем текущий номер страницы
    onChange(currentPageNumber); // Вызываем функцию onChange с передачей текущего номера страницы
    loadGoods(currentPageNumber); // Вызываем функцию loadGoods с передачей текущего номера страницы
};

onChange()




function onChange(page_number) {
    let value = dropdownList.value;

    if (value === "increase"){
        sortCartByPrice(true);
    }
    else if(value === "decrease"){
        sortCartByPrice(false);

    }
    else{
        sortedCart = Object.assign({}, cart);
    }

    return sortedCart;
}

function sortCartByPrice(flag) {
    var keyArray = Object.keys(sortedCart);
    let Price;
    for (let i = 0; i < keyArray.length - 1; i++){
        Price = keyArray[i];
        for (let j = i + 1; j < keyArray.length; j++){
            if (flag){
                if (sortedCart[Price].price > sortedCart[keyArray[j]].price)
                    Price = keyArray[j];
            }else{
                if (sortedCart[Price].price < sortedCart[keyArray[j]].price)
                    Price = keyArray[j];
            }
        }

        if (Price !== keyArray[i])
            swap(sortedCart, Price, keyArray[i]);
    }

}

function swap(sortedCart, key1, key2){
    [sortedCart[key1], sortedCart[key2]] = [sortedCart[key2], sortedCart[key1]]
}

function getCurrentPageNumber() {
    let currentPageNumber = 0; // По умолчанию номер страницы 0

    // Определение текущего номера страницы на основе стилей кнопок пагинации
    $("button.pgn-bnt-styled").each(function(index) {
        if ($(this).css("font-weight") === "bold") {
            currentPageNumber = index;
            return false; // Прерываем цикл после нахождения текущей страницы
        }
    });

    return currentPageNumber;
}
