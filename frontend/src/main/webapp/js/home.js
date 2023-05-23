window.onload = () => {
	if (loggedUser != null || (tokenParameter != NOT_LOGGED_TOKEN && roleParameter != undefined)) {
		manageNavbar();
		getCart();
	}
};

/**
 * Builds what is written in the URL when the products button is clicked then redirects to built page.
 * <ol>
 * 	<li>Clears the URL</li>
 * 	<ol>
 * 		<li>deletes the token</li>
 * 		<li>deletes the search key</li>
 * 		<li>deletes the role</li>
 * 	</ol>
 * 	<li>Creates a variable for token and initialize it with all zeros UUID</li>
 * 	<li>Creates a variable for role</li>
 * 	<li>Checks if logged user is not null</li>
 * 	<ol>
 *  	<li>assigns the token attribute from loggedUser variable to token variable</li>
 * 		<li>assigns the role attribute from loggedUser variable to role variable</li>
 * 	</ol>
 * 	<li>Builds the URL</li>
 * 	<ol>
 * 		<li>Inserts the token</li>
 * 		<li>Inserts the search key</li>
 * 		<li>Inserts the role</li>
 * 	</ol>
 * 	<li>Redirects to the built URL</li>
 * </ol>
 *
 * @date 5/8/2023 - 9:26:21 AM
 *
 * @param {JSON} keySearchEnumParam the type of search that the user can do. (ALL, BEAUTY, SUPPLEMENTS or HEALTH)
 */
const buildURLAndRedirect = (keySearchEnumParam) => {
	dataURL.delete("token");
	dataURL.delete("key-search");
	dataURL.delete("role");

	let token = NOT_LOGGED_TOKEN;
	let role;

	if (loggedUser !== null && loggedUser !== undefined) {
		token = loggedUser.token;
		role = loggedUser.role;
	}

	if (roleParameter != null && roleParameter != undefined) {
		role = roleParameter;
	}

	if (tokenParameter != null && tokenParameter != undefined) {
		token = tokenParameter;
	}

	dataURL.append("token", token);
	dataURL.append("key-search", keySearchEnumParam);
	dataURL.append("role", role);

	window.location.href = "order_products_favorites.html?" + dataURL.toString();
};

// hack: Functions, variables and constants that repeats to all pages. Except dashboard.

/**
 * HTML <strong><em>input</em></strong> element in the form at top of the page in the navbar to storage the username.
 * @date 5/8/2023 - 10:26:58 AM
 *
 * @type {Object}
 */
const inputUsername = document.getElementById("username");
/**
 * HTML <strong><em>input</em></strong> element in the form at top of the page in the navbar to storage the password.
 * @date 5/8/2023 - 4:18:26 PM
 *
 * @type {Object}
 */
const inputPassword = document.getElementById("password");
/**
 * HTML <strong><em>anchor</em></strong> element in the navbar that has the appearance of a button to toggle the cart.
 * @date 5/8/2023 - 4:20:17 PM
 *
 * @type {Object}
 */
const cartButton = document.getElementById("cart-btn");
/**
 * HTML <strong><em>anchor</em></strong> element in the navbar that has the appearance of a button to redirect to dashboard page.
 * @date 5/8/2023 - 4:23:33 PM
 *
 * @type {Object}
 */
const dashboardButton = document.getElementById("dashboard-btn");
/**
 * HTML <strong><em>anchor</em></strong> element in the navbar that has the appearance of a button to toggle the sign in form.
 * @date 5/8/2023 - 4:29:18 PM
 *
 * @type {Object}
 */
const signinButton = document.getElementById("signin-btn");
/**
 * HTML <strong><em>anchor</em></strong> element in the navbar that has the appearance of a button to signs out the logged user.
 * @date 5/8/2023 - 4:31:06 PM
 *
 * @type {Object}
 */
const signoutButton = document.getElementById("signout-btn");
/**
 * HTML <strong><em>div</em></strong> element in the login form used to warn user about invalid credentials after the sign in button is clicked.
 * @date 5/8/2023 - 4:33:12 PM
 *
 * @type {Object}
 */
const signinError = document.getElementsByClassName("error-signin")[0];

/**
 * HTML <strong><em>anchor</em></strong> element in the navbar that links the current user to favourites page.
 * @date 5/17/2023 - 9:02:40 AM
 *
 * @type {Object}
 */
const favouritesLink = document.getElementsByClassName("get-favourites");
/**
 * HTML <strong><em>anchor</em></strong> element in the navbar that links the current user to history page.
 * @date 5/17/2023 - 9:04:39 AM
 *
 * @type {Object}
 */
const historyLink = document.getElementsByClassName("get-history");
/**
 * HTML <strong><em>anchor</em></strong> element in the navbar that has the appearance of a button to redirect to edit profile page.
 * @date 5/17/2023 - 9:05:51 AM
 *
 * @type {Object}
 */
const editProfileButton = document.getElementById("edit-profile-btn");
/**
 * The way to get things of URL.
 * @date 5/7/2023 - 9:51:20 AM
 *
 * @type {URLSearchParams}
 */
const parameters = new URLSearchParams(window.location.search);
/**
 * <strong><em>span</em></strong> element that contains the amount of element in cart
 * @date 5/17/2023 - 10:20:16 AM
 *
 * @type {Object}
 */
const badge = document.getElementsByClassName("badge")[0];
/**
 * UUID to be get from URL.
 * @date 5/8/2023 - 5:08:34 PM
 *
 * @type {string}
 */
const tokenParameter = parameters.get("token");
/**
 * User role to be added in the URL parameters.
 * @date 5/8/2023 - 2:27:35 PM
 *
 * @type {string}
 */
let roleParameter = parameters.get("role");
/**
 * Storages the logged user in the home page.
 * @date 5/8/2023 - 4:36:25 PM
 *
 * @type {JSON}
 */
let loggedUser;

/**
 * Shows/hides buttons in the navbar according to the sign in action.
 * @date 5/8/2023 - 9:22:11 AM
 */
const manageNavbar = () => {
	inputUsername.value = "";
	inputPassword.value = "";
	cartButton.classList.remove("disappear");
	signinForm.classList.remove("active");
	signinButton.classList.add("disappear");
	signoutButton.classList.remove("disappear");
	editProfileButton.classList.remove("disappear");

	for (const link of favouritesLink) {
		link.classList.remove("disappear");
	}

	for (const link of historyLink) {
		link.classList.remove("disappear");
	}

	if ((loggedUser != undefined && loggedUser.role == role.ADMINISTRATOR) ||
		(roleParameter != undefined && roleParameter == role.ADMINISTRATOR)) {
		dashboardButton.classList.remove("disappear");
	}
};

/**
 * Signs in a user into the system.
 * @date 5/6/2023 - 8:13:57 PM
 *
 * @async
 * @returns {JSON} the logged user
 */
const signin = async () => {
	const usernameValue = inputUsername.value;
	const passwordValue = inputPassword.value;
	const usernameTrim = usernameValue.trim();
	const passwordTrim = passwordValue.trim();

	if (
		usernameValue != "" &&
		passwordValue != "" &&
		usernameTrim.length > 0 &&
		passwordTrim.length > 0
	) {
		const headers = new Headers();
		headers.append("username", usernameTrim);
		headers.append("password", passwordTrim);

		await fetch(
			urlBase + "/user/signin",
			fetchContentFactoryWithoutBodyMultipleHeaders(
				requestMethods.POST,
				headers,
			),
		)
			.then((response) => {
				if (response.ok) {
					return response.json();
				} else {
					signinError.classList.remove("disappear");
					setTimeout(() => {
						signinError.classList.add("disappear");
					}, 2000);
				}
			})
			.then((user) => {
				loggedUser = user;

				dataURL.delete("token");
				dataURL.delete("role");

				dataURL.append("token", loggedUser.token);
				dataURL.append("role", loggedUser.role);

				window.location.href = "home.html?" + dataURL.toString();

				if (loggedUser != undefined && loggedUser != null) {
					manageNavbar();
				}
			});
	} else {
		signinError.classList.remove("disappear");
		setTimeout(() => {
			signinError.classList.add("disappear");
		}, 2000);
	}
};

document.getElementById("signin").addEventListener("click", signin);

/**
 * Signs out the current user.
 * <ol>
 * 	<li>Fetches the sign out endpoint</li>
 * 	<li>Gets the endpoint response</li>
 * 	<ol>
 * 		<li>Checks if the HTTP response code is 200</li>
 * 		<ol>
 * 			<li>removes the cart button</li>
 * 			<li>adds the sign in button</li>
 * 			<li>removes the sign out button</li>
 * 			<li>removes the dashboard button</li>
 * 		</ol>
 * 		<li>Checks if the HTTP response code is different than 200</li>
 * 		<ol>
 * 			<li>logs the message</li>
 * 		</ol>
 * 	</ol>
 * </ol>
 * @date 5/6/2023 - 8:27:49 PM
 *
 * @async
 * @returns {boolean} true if the user is logged out, false otherwise
 */
const signout = async () => {
	let token = loggedUser ? loggedUser.token : tokenParameter;

	if (token != null && token != undefined && token != "") {
		await fetch(
			urlBase + "/user/signout",
			fetchContentFactoryWithoutBody(requestMethods.POST, token),
		).then((response) => {
			if (response.ok) {
				cartButton.classList.add("disappear");
				signinButton.classList.remove("disappear");
				signoutButton.classList.add("disappear");
				dashboardButton.classList.add("disappear");

				dataURL.delete("token");
				dataURL.delete("role");

				window.location.href = "home.html?" + dataURL.toString();

				for (const link of favouritesLink) {
					link.classList.add("disappear");
				}
			} else {
				console.error("Sign out failed");
			}
		});
	} else {
		console.error("No token detected");
	}
};

document.getElementById("signout-btn").addEventListener("click", signout);

/**
 * Gets the only non concluded order (cart) from the logged user.
 * @date 5/16/2023 - 4:11:10 PM
 *
 * @async
 * @returns {JSON} object order that contains: 
 */
const getCart = async () => {
	let token = loggedUser ? loggedUser.token : tokenParameter;

	await fetch(
		urlBase + "/order/cart",
		fetchContentFactoryWithoutBody(requestMethods.GET, token)).then((response) => {
			if (response.ok) {
				return response.json();
			} else {
				console.error("Error on getOrderByToken (getCart)");
			}
		}).then((cart) => {
			handleBadge(cart.productsDTO ? cart.productsDTO.length : 0);

			if (cart.id != null && cart.id != undefined) {
				loadCartItem(cart);
			}
		});
};

/**
 * Displays all the elements that make up the cart
 * @date 5/16/2023 - 4:07:58 PM
 *
 * @param {[JSON]} cart list of products that the current order contains
 */
const loadCartItem = (cart) => {

	cleanCart();

	if (cart.productsDTO) {
		cart.productsDTO.forEach(productInCartElement => {
			// <div class="box">
			const cartItem = document.createElement("div");
			cartItem.classList.add("box");
			// <i class="fa-solid fa-trash">
			const trashIcon = document.createElement("i");

			trashIcon.classList.add("fa-solid");
			trashIcon.classList.add("fa-trash");
			trashIcon.addEventListener("click", async () => {
				const urlWithQueryParametersRemoveProduct = new URL(urlBase + "/order/product-by");
				urlWithQueryParametersRemoveProduct.searchParams.append("orderId", cart.id);
				urlWithQueryParametersRemoveProduct.searchParams.append("productId", productInCartElement.id);

				let token = loggedUser ? loggedUser.token : tokenParameter;
				await fetch(
					urlWithQueryParametersRemoveProduct,
					fetchContentFactoryWithoutBody(requestMethods.DELETE, token),
				)
					.then((response) => {
						if (response.ok) {
							return response.json();
						} else {
							console.error("Error fetching product by id");
						}
					})
					.then((cart) => {
						loadCartItem(cart);
						handleBadge(cart.productsDTO ? cart.productsDTO.length : 0);
					});
			});
			// <img src="../images/national-watermelon-day(sm).png" alt="">
			const productImage = document.createElement("img");
			productImage.src = productInCartElement.image;
			productImage.alt = productInCartElement.name;
			// <div class="content">
			const productInformations = document.createElement("div");
			productInformations.classList.add("content");
			// <h3>watermelon</h3>
			const productName = document.createElement("h3");
			productName.innerText = productInCartElement.name;
			// <span class="price">€4.99/-</span>
			const productPrice = document.createElement("span");
			productPrice.classList.add("price");
			productPrice.innerText = productInCartElement.price;

			productInformations.appendChild(productName);
			productInformations.appendChild(productPrice);
			cartItem.appendChild(trashIcon);
			cartItem.appendChild(productImage);
			cartItem.appendChild(productInformations);
			cartDiv.appendChild(cartItem);
		});

		const total = document.createElement("div");
		total.classList.add("total");
		total.innerText = "Total : " + cart.totalValue.toFixed(2).toString() + "€";

		const checkout = document.createElement("a");
		checkout.classList.add("btn");
		checkout.href = "#";
		checkout.innerText = "checkout";
		checkout.addEventListener("click", async () => {
			const urlWithQueryParametersCheckout = new URL(urlBase + "/order/finish");
			urlWithQueryParametersCheckout.searchParams.append("id", cart.id);

			let token = loggedUser ? loggedUser.token : tokenParameter;

			await fetch(
				urlWithQueryParametersCheckout,
				fetchContentFactoryWithoutBody(requestMethods.PUT, token),
			)
				.then((response) => {
					if (response.ok) {
						return response.json();
					} else {
						console.error("Error fetching finished order");
					}
				})
				.then((order) => {
					if (order.isConcluded) {
						cleanCart();
						handleBadge(0);
					}
				});
		});

		cartDiv.appendChild(total);
		cartDiv.appendChild(checkout);
	}
};

/**
 * Removes all elements inside div cart.
 * @date 5/17/2023 - 10:17:54 AM
 */
function cleanCart() {
	if (cartDiv.children) { //TODO: esta verificação é necessária? Se calhar, basta o while já funciona.
		while (cartDiv.children.length > 0) {
			cartDiv.removeChild(cartDiv.children[0]);
		}
	}
}

/**
 * Displays the badge according to amount of digits.
 * @date 5/16/2023 - 3:54:09 PM
 *
 * @param {number} amount of products
 */
function handleBadge(amount) {
	badge.innerText = amount;
	if (badge.classList.contains("disappear")) {
		badge.classList.remove("disappear");
	}

	if (amount < 10) {
		badge.classList.add("single-digit");
	} else {
		badge.classList.add("two-digits");
	}
}

dashboardButton.addEventListener("click", () => {
	let token;

	if (loggedUser) {
		token = loggedUser.token;
	}

	if (tokenParameter != NOT_LOGGED_TOKEN) {
		token = tokenParameter;
	}

	if (roleParameter && roleParameter == role.ADMINISTRATOR || loggedUser && loggedUser.role == role.ADMINISTRATOR) {
		dataURL.delete("token");

		dataURL.append("token", token);

		window.location.href = "dashboard.html?" + dataURL.toString();
	}
});

// NAVBAR

let shoppingCart = document.querySelector(".shopping-cart");
document.querySelector("#cart-btn").onclick = () => {
	shoppingCart.classList.toggle("active");
	signinForm.classList.remove("active");
	navbar.classList.remove("active");
};

let signinForm = document.querySelector(".signin-form");
document.querySelector("#signin-btn").onclick = () => {
	shoppingCart.classList.remove("active");
	signinForm.classList.toggle("active");
	navbar.classList.remove("active");
};