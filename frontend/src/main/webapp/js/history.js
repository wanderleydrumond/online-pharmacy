/**
 * The way to get things of URL.
 * @date 5/23/2023 - 3:56:22 PM
 *
 * @type {URLSearchParams}
 */
const parameters = new URLSearchParams(window.location.search);
/**
 * UUID to be get from URL.
 * @date 5/23/2023 - 3:55:59 PM
 *
 * @type {string}
 */
const tokenParameter = parameters.get("token");
/**
 * Role to be get from URL.
 * @date 5/24/2023 - 11:35:05 AM
 *
 * @type {string}
 */
const roleParameter = parameters.get("role");
/**
 * HTML <code><div></code> that contains the first half order list.
 * @date 5/23/2023 - 4:08:51 PM
 *
 * @type {HTMLElement}
 */
const ordersHalf1Div = document.getElementById("orders-half-1");
/**
 * * HTML <code><div></code> that contains the last half order list.
 * @date 5/23/2023 - 4:08:28 PM
 *
 * @type {HTMLElement}
 */
const ordersHalf2Div = document.getElementById("orders-half-2");
const home = document.getElementsByClassName("homepage");
let orderId;

window.onload = () => {
    getHistoryData();
}

const getHistoryData = () => {
    fetch(urlBase + "/order/all",
        fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter)).then((response) => {
            if (response.ok) {
                return response.json();
            }
        }).then((orders) => {
            divideArrays(orders);
        });
};

const divideArrays = (orderList) => {
    if (orderList) {
        let arrayOrdersHalf1 = [],
            arrayOrdersHalf2 = [];
        let cutoff = Math.round(orderList.length / 2);

        orderList.forEach((element, index) => {
            if (cutoff <= index) {
                arrayOrdersHalf1.push(element);
            } else {
                arrayOrdersHalf2.push(element);
            }
        });

        loadOrders(arrayOrdersHalf1, ordersHalf1Div);
        loadOrders(arrayOrdersHalf2, ordersHalf2Div);

        if (orderId != null && orderId != undefined) {
            document
                .getElementById(orderId)
                .scrollIntoView({ behavior: "instant", block: "center" });
        }
    }
};

const loadOrders = (arrayHalf, divHalf) => {
    while (divHalf.children.length > 0) {
        divHalf.removeChild(divHalf.children[0]);
    }

    arrayHalf.forEach(orderElement => {
        // <div class="swiper-slide box"></div>
        const orderDiv = document.createElement("div");
        orderDiv.classList.add("swiper-slide");
        orderDiv.classList.add("box");
        orderId = orderDiv.id = orderElement.id;
        // <h3>24/04/2023</h3>
        const orderDate = document.createElement("h3");

        const dateFromDatabase = new Date(orderElement.lastUpdate);
        const formattingOptions = {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
        };
        const formattedDate = dateFromDatabase.toLocaleString('en-GB', formattingOptions);
        orderDate.innerText = formattedDate;

        // <div class="price">101.99€</div>
        const orderValue = document.createElement("div");
        orderValue.classList.add("price");
        const formattedPrice = orderElement.totalValue.toLocaleString('pt-PT', {
            style: 'currency',
            currency: 'EUR'
        });
        orderValue.innerText = formattedPrice;
        // <a href="../html/order_details.html" class="btn">see details</a>
        const seeDetails = document.createElement("a");
        seeDetails.href = "#";
        seeDetails.classList.add("btn");
        seeDetails.innerText = "see details";
        seeDetails.addEventListener('click', () => {
            dataURL.delete("token");
            dataURL.delete("role");

            dataURL.append("token", tokenParameter);
            dataURL.append("id", orderElement.id);
            dataURL.append("role", roleParameter);
            dataURL.append("key-search", keySearchEnum.ORDER);

            window.location.href = "order_products_favorites.html?" + dataURL.toString();
        });

        orderDiv.appendChild(orderDate);
        orderDiv.appendChild(orderValue);
        orderDiv.appendChild(seeDetails);
        divHalf.appendChild(orderDiv);
    });
};

for (const homeLink of home) {
    homeLink.addEventListener('click', () => {
        dataURL.delete("token");
        dataURL.delete("role");

        dataURL.append("token", tokenParameter);
        dataURL.append("role", roleParameter);

        window.location.href = "home.html?" + dataURL.toString();
    });
}