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
let visitors;
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
/**
 * HTML <strong><em>anchor</em></strong> element in the navbar and in the logo that links the current user to home page.
 * @date 5/18/2023 - 2:03:06 PM
 *
 * @type {Object}
 */
const home = document.getElementsByClassName("home");
const visitorsTable = document.getElementById("visitors");
const approveAllButton = document.getElementById("approve-all")
const sectionsSelect = document.getElementsByName("section")[0];
const inputProductName = document.getElementById("product-name");
const inputPrice = document.getElementById("product-price");
const inputImage = document.getElementById("product-image");
const create = document.getElementById("create-product-button");

window.onload = () => {
    getDashboardData();
    getSections();
}

/**
 * Gets the all informations regarding dashboard.
 * @date 5/18/2023 - 1:57:56 PM
 *
 * @async
 * @returns {JSON} the object that contains all the dashboard data
 */
const getDashboardData = async () => {
    await fetch(urlBase + "/user/dashboard",
        fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter)).then((response) => {
            if (response.ok) {
                return response.json();
            } else {
                console.error("Error getting dashboard");
            }
        }).then((dashboard) => {
            visitors = dashboard.visitorsDTO;
            loadCards(dashboard);

            loadVisitors();
        });
};

/**
 * Clears the current URL, builds a new one with token and role then redirect to home.
 * @date 5/18/2023 - 2:00:12 PM
 */
const buildURLAndRedirectToHome = () => {
    dataURL.delete("token");

    dataURL.append("token", tokenParameter);
    dataURL.append("role", role.ADMINISTRATOR);

    window.location.href = "home.html?" + dataURL.toString();
};


/**
 * Fills the respective data inside each card.
 * @date 5/18/2023 - 1:49:50 PM
 *
 * @param {JSON} dashboard object that contains all informations regarding this page
 */
const loadCards = (dashboard) => {
    totalClients.innerText = dashboard.totalClients;
    totalProducts.innerText = dashboard.totalProducts;
    totalCarts.innerText = dashboard.totalCarts;
    totalSignIns.innerText = dashboard.totalSignIns;
    totalPurchases.innerText = dashboard.totalValueConcludedOrders.toFixed(2).toString() + "€";
    currentMonth.innerText = dashboard.totalValueConcludedOrdersCurrentMonth.toFixed(2).toString() + "€";
    lastMonth.innerText = dashboard.totalValueConcludedOrdersLastMonth.toFixed(2).toString() + "€";
}
const loadVisitors = () => {
    while (visitorsTable.children.length > 1) {
        visitorsTable.removeChild(visitorsTable.children[1]);
    }

    visitors.forEach((visitorElement, index) => {
        const elementRow = document.createElement("tr");
        // <td>mark grayson</td>
        const visitorName = document.createElement("td");
        visitorName.innerText = visitorElement.name;
        // <td></td>
        const visitorOption = document.createElement("td");
        // <a href="#" class="btn"></a>
        const approveButton = document.createElement("a");
        approveButton.id = index;
        approveButton.href = "#";
        approveButton.classList.add("btn");
        approveButton.addEventListener('click', async (event) => {
            await approve(visitorElement, false);
        });
        // <i class="fa-solid fa-check"></i>
        const checkIcon = document.createElement("i");
        checkIcon.classList.add("fa-solid");
        checkIcon.classList.add("fa-check");

        approveButton.appendChild(checkIcon);
        visitorOption.appendChild(approveButton);
        elementRow.appendChild(visitorName);
        elementRow.appendChild(visitorOption);
        visitorsTable.appendChild(elementRow);
    });
}

approveAllButton.addEventListener('click', async (event) => {
    for (const visitorElement of visitors) {
        await approve(visitorElement, true);
    }
    manageScreen();
});

/**
 * Gets the dashboard data and centers the screen to the table.
 * @date 5/18/2023 - 7:06:49 PM
 */
const manageScreen = () => {
    getDashboardData();
    visitorsTable.scrollIntoView({ behavior: "instant", block: "center" });
}

async function approve(visitorElement, isApproveAll) {
    const urlWithQueryParametersApprove = new URL(urlBase + "/user/approve");
    urlWithQueryParametersApprove.searchParams.append("id", visitorElement.id);

    await fetch(
        urlWithQueryParametersApprove,
        fetchContentFactoryWithoutBody(requestMethods.PUT, tokenParameter)
    )
        .then((response) => {
            if (response.ok) {
                if (!isApproveAll) {
                    manageScreen();
                }
            }
        });
}

/**
 * Fills options elements inside select element with the sections from the database.
 * @date 5/18/2023 - 7:03:41 PM
 */
const getSections = () => {
    fetch(urlBase + "/product/all-sections", fetchContentFactoryWithoutBody(requestMethods.GET)).then((response) => {
        if (response.ok) {
            return response.json();
        }
    }).then((sections) => {
        sections.forEach(sectionElement => {
            // <option value="health">Health</option>
            const option = document.createElement("option");
            option.value = sectionElement;
            option.innerText = sectionElement;
            sectionsSelect.appendChild(option);
        });
    });
};

const createProduct = () => {
    const productNameValue = inputProductName.value.trim();
    const priceValue = inputPrice.value.trim();
    const imageValue = inputImage.value.trim();
    const sectionChoose = sectionsSelect.options[sectionsSelect.selectedIndex].text.toUpperCase();

    let body = {
        "name": productNameValue,
        "image": imageValue,
        "price": priceValue,
        "section": sectionChoose
    }
    fetch(urlBase + "/product/create", fetchContentFactoryWithBody(requestMethods.POST, body, tokenParameter)).then((response) => {
        if (response.ok) {
            inputProductName.value = "";
            inputImage.value = "";
            inputPrice.value = "";
            sectionsSelect.options[0];
        }
        return response.json();
    }).then((product) => {
        console.log(product);
    }).catch((error) => console.log(error));
};

create.addEventListener('click', (event) => {
    debugger;
    createProduct();
});