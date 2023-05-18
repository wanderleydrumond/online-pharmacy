/**
 * The way to get things of URL.
 * @date 5/7/2023 - 9:51:20 AM
 *
 * @type {URLSearchParams}
 */
const parameters = new URLSearchParams(window.location.search);
/**
 * UUID to be get from URL.
 * @date 5/8/2023 - 5:08:34 PM
 *
 * @type {string}
 */
const tokenParameter = parameters.get("token");
let dashboard;
/**
 * HTML <strong><em>h1</em></strong> element that contains the amount of clients existent in database.
 * @date 5/17/2023 - 4:34:44 PM
 *
 * @type {Object}
 */
const totalClients = document.getElementById("total-clients");
/**
 * HTML <strong><em>h1</em></strong> element that contains the amount of products existent in database.
 * @date 5/17/2023 - 4:35:46 PM
 *
 * @type {Object}
 */
const totalProducts = document.getElementById("total-products");
/**
 * HTML <strong><em>h1</em></strong> element that contains the amount of orders non concluded existent in database.
 * @date 5/17/2023 - 4:36:31 PM
 *
 * @type {Object}
 */
const totalCarts = document.getElementById("total-carts");
/**
 * HTML <strong><em>h1</em></strong> element that contains the amount of sign ins performed in system.
 * @date 5/17/2023 - 4:37:25 PM
 *
 * @type {Object}
 */
const totalSignIns = document.getElementById("total-sign-ins");
/**
 * HTML <strong><em>h1</em></strong> element that contains the total value of purchases performed in system.
 * @date 5/17/2023 - 4:42:05 PM
 *
 * @type {Object}
 */
const totalPurchases = document.getElementById("concluded-orders");
/**
 * HTML <strong><em>h1</em></strong> element that contains the total value of purchases performed in current month in system.
 * @date 5/17/2023 - 4:43:57 PM
 *
 * @type {Object}
 */
const currentMonth = document.getElementById("current-month");
/**
 * HTML <strong><em>h1</em></strong> element that contains the total value of purchases performed in last month in system.
 * @date 5/17/2023 - 4:45:27 PM
 *
 * @type {Object}
 */
const lastMonth = document.getElementById("last-month");
const home = document.getElementsByClassName("home");

window.onload = () => {
    getDashboardData();
}

const getDashboardData = async () => {
    await fetch(urlBase + "/user/dashboard",
        fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter)).then((response) => {
            if (response.ok) {
                return response.json();
            } else {
                console.error("Error getting dashboard");
            }
        }).then((dashboard) => {
            this.dashboard = dashboard;
            totalClients.innerText = dashboard.totalClients;
            totalProducts.innerText = dashboard.totalProducts;
            totalCarts.innerText = dashboard.totalCarts;
            totalSignIns.innerText = dashboard.totalSignIns;
            totalPurchases.innerText = dashboard.totalValueConcludedOrders.toFixed(2).toString() + "€";
            currentMonth.innerText = dashboard.totalValueConcludedOrdersCurrentMonth.toFixed(2).toString() + "€";
            lastMonth.innerText = dashboard.totalValueConcludedOrdersLastMonth.toFixed(2).toString() + "€";
        });
};

const buildURLAndRedirectToHome = () => {
    dataURL.delete("token");

    dataURL.append("token", tokenParameter);
    dataURL.append("role", role.ADMINISTRATOR);

    window.location.href = "home.html?" + dataURL.toString();
};