/**
 * The way to get things of URL.
 * @date 5/7/2023 - 9:51:20 AM
 *
 * @type {URLSearchParams}
 */
const parameters = new URLSearchParams(window.location.search);
const titlePageEnum = {
	ALL_OR_BY_SECTION: "our ",
	FAVORITES: "my ",
	ORDER: "order "
};
/**
 * Key search to be get from URL.
 * @date 5/8/2023 - 5:06:00 PM
 *
 * @type {string}
 */
let keySearchParameter = parameters.get("key-search");
/**
 * The product list available.
 * @date 5/8/2023 - 2:29:36 PM
 *
 * @type {[JSON]}
 */
let productList = [];
/**
 * Product id that will the assigned to the div that encapsulates it to maintain
 * the page centralized in the product when like/unlike or favorite/unfavorite is clicked.
 * @date 5/11/2023 - 10:40:51 AM
 *
 * @type {number}
 */
let productIdAuxiliary = null;
/**
 * Object that will be passed through header of the request.
 * @date 5/8/2023 - 2:22:34 PM
 *
 * @type {Headers}
 */
let header;

/**
 * div that contains the first half product list.
 * @date 5/11/2023 - 10:35:14 AM
 *
 * @type {Object}
 */
const productsHalf1Div = document.getElementById("products-half-1");
/**
 * div that contains the last half product list.
 * @date 5/11/2023 - 10:37:35 AM
 *
 * @type {Object}
 */
const productsHalf2Div = document.getElementById("products-half-2");

const titlePage = document.getElementById("title-page");
const cartDiv = document.getElementsByClassName("shopping-cart")[0];
const searchButton = document.getElementById("search-btn");

window.onload = () => {
	if (keySearchParameter == keySearchEnum.ORDER) {
		searchButton.classList.add("disappear");
	}

	if (loggedUser != null || (tokenParameter != NOT_LOGGED_TOKEN && roleParameter != undefined)) {
		manageNavbar();
		getCart();
	}
	fetchProducts();
};

/**
 * Loads the products according to the provided search key.
 * <ol>
 * 	<li>Creates the variable that verifies if this product is liked or marked as favorite and initialise it as false</li>
 * 	<li>Verifies if the token on the URL belongs to a logged user</li>
 * 	<ol>
 * 		<li>Assigns true to the variable which will check if if that product was liked or marked as favorite by the logged user</li>
 * 	</ol>
 * 	<li>If the URL "sey-search" parameter ha the value:</li>
 * 	<ul>
 * 		<li>ALL:</li>
 * 		<ol>
 * 			<li>Creates a constant that contains the whole endpoint URL</li>
 * 			<li>Adds the query parameter that verifies the like or favorite</li>
 * 			<li>Assigns a new instance of Headers object to header variable</li>
 * 			<li>Adds to the header the token that came by URL</li>
 * 			<li>Fetches the correspondent backend get method</li>
 * 				<ol>
 * 					<li>Gets the method response</li>
 * 						<ul>
 * 							<li>if The backend answers 200:</li>
 * 							<ul>
 * 								<li>returns all products</li>
 * 							</ul>
 * 							<li>Otherwise</li>
 * 							<ul>
 * 								<li>logs the error</li>
 * 							</ul>
 * 						</ul>
 * 					<li>Gets the method content</li>
 * 						<ol>
 * 							<li>Assigns the content value to the global variable</li>
 * 						</ol>
 * 				</ol>
 * 		</ol>
 * 		<li>BEAUTY/SUPPLEMENTS/HEALTH:</li>
 * 			<ol>
 * 				<li>Creates a constant that contains the whole endpoint URL</li>
 * 				<li>Adds the query parameter that verifies the like or favorite</li>
 * 				<li>Adds the query parameter that identifies the section that that product list belongs</li>
 * 				<li>Assigns a new instance of Headers object to header variable</li>
 * 				<li>Adds to the header the token that came by URL</li>
 * 				<li>Fetches the correspondent backend get method</li>
 * 				<ol>
 * 					<li>Gets the method response</li>
 * 						<ul>
 * 							<li>if The backend answers 200:</li>
 * 							<ul>
 * 								<li>returns all products from the given section</li>
 * 							</ul>
 * 							<li>Otherwise</li>
 * 							<ul>
 * 								<li>logs the error</li>
 * 							</ul>
 * 						</ul>
 * 					<li>Gets the method content</li>
 * 						<ol>
 * 							<li>Assigns the content value to the global variable</li>
 * 						</ol>
 * 				</ol>
 * 			</ol>
 *   	<li>FAVOURITES:</li>
 * 			<ol>
 * 				<li>Creates a constant that contains the whole endpoint URL</li>
 * 				<li>Adds the query parameter that verifies the like or favorite</li>
 * 				<li>Adds the query parameter that identifies the section that that product list belongs</li>
 * 				<li>Assigns a new instance of Headers object to header variable</li>
 * 				<li>Adds to the header the token that came by URL</li>
 * 				<li>Fetches the correspondent backend get method</li>
 * 				<ol>
 * 					<li>Gets the method response</li>
 * 						<ul>
 * 							<li>if The backend answers 200:</li>
 * 							<ul>
 * 								<li>returns all products from the favorites section</li>
 * 							</ul>
 * 							<li>Otherwise</li>
 * 							<ul>
 * 								<li>logs the error</li>
 * 							</ul>
 * 						</ul>
 * 					<li>Gets the method content</li>
 * 						<ol>
 * 							<li>Assigns the content value to the global variable</li>
 * 						</ol>
 * 				</ol>
 * 			</ol>
 *   	<li>ORDER:</li>
 * 		TODO: to be done
 * 	</ul>
 * 	<li>calls divideArrays function</li>
 * </ol>
 * @date 5/13/2023 - 1:44:30 PM
 *
 * @async
 * @returns {[JSON]} product list according to the given key search parameter
 */
const fetchProducts = async () => {
	const titleSpan = document.createElement("span");
	let verify = false;

	if (tokenParameter != NOT_LOGGED_TOKEN) {
		verify = true;
	}

	switch (keySearchParameter) {
		case keySearchEnum.ALL:
			titlePage.innerText = titlePageEnum.ALL_OR_BY_SECTION;
			titleSpan.innerText = "products";

			const urlWithQueryParametersAll = new URL(urlBase + "/product/all");
			urlWithQueryParametersAll.searchParams.append("verify", verify);

			await fetch(
				urlWithQueryParametersAll,
				fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter),
			)
				.then((response) => {
					if (response.ok) {
						return response.json();
					}
				})
				.then((productList) => {
					this.productList = productList;
				});
			break;

		case keySearchEnum.BEAUTY:
		case keySearchEnum.HEALTH:
		case keySearchEnum.SUPPLEMENTS:

			let section = keySearchParameter;


			titlePage.innerText = titlePageEnum.ALL_OR_BY_SECTION;
			titleSpan.innerText = section.toLowerCase();

			const urlWithQueryParametersSection = new URL(urlBase + "/product/all-by");
			urlWithQueryParametersSection.searchParams.append("verify", verify);
			urlWithQueryParametersSection.searchParams.append("section", section);

			await fetch(
				urlWithQueryParametersSection,
				fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter),
			)
				.then((response) => {
					if (response.ok) {
						return response.json();
					} else {
						console.error("Error fetching products by section");
					}
				})
				.then((productList) => {
					this.productList = productList;
				});
			break;
		case keySearchEnum.FAVOURITES:
			titlePage.innerText = titlePageEnum.FAVORITES;
			titleSpan.innerText = keySearchEnum.FAVOURITES.toLowerCase();

			const urlWithQueryParametersFavourites = new URL(
				urlBase + "/product/favourites",
			);
			urlWithQueryParametersFavourites.searchParams.append("verify", verify);

			await fetch(
				urlWithQueryParametersFavourites,
				fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter),
			)
				.then((response) => {
					if (response.ok) {
						return response.json();
					} else {
						console.error("Error fetching favourite products");
					}
				})
				.then((productList) => {
					this.productList = productList;
				});
			break;
		case keySearchEnum.ORDER:
			titlePage.innerText = titlePageEnum.ORDER;
			titleSpan.innerText = "details";

			header = new Headers();
			header.append("token", tokenParameter);

			const urlWithQueryParametersOrderDetails = new URL(urlBase + "/order/by");
			urlWithQueryParametersOrderDetails.searchParams.append("verify", verify);
			urlWithQueryParametersOrderDetails.searchParams.append("orderId", orderIdParameter);

			await fetch(
				urlWithQueryParametersOrderDetails,
				fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter)
			)
				.then((response) => {
					if (response.ok) {
						return response.json();
					}
				})
				.then((orderDetails) => {
					productList = orderDetails.productsDTO;
					this.productList = productList;
				});
			break;
		default:
			break;
	}

	titlePage.appendChild(titleSpan);
	divideArrays(this.productList);
};
/**
 * Divides the product list in two. @function fetchProducts auxiliary function.
 * <ol>
 * 	<li>Creates two variables that will house half array each</li>
 * 	<li>Creates a variable that defines the point that array will by cut in case of odd amount of elements</li>
 * 	<li>Divides the provided array</li>
 * 	<ol>
 * 		<li>Fills the created arrays in order to respect the cutoff between them</li>
 * 	</ol>
 * 	<li>Calls loadProducts function to fill those products in DOM accordingly</li>
 * </ol>
 * @param {[JSON]} productList the array to be divide
 */
const divideArrays = (productList) => {
	if (productList) {
		let arrayProductsHalf1 = [],
			arrayProductsHalf2 = [];
		let cutoff = Math.round(productList.length / 2);

		productList.forEach((element, index) => {
			if (cutoff <= index) {
				arrayProductsHalf1.push(element);
			} else {
				arrayProductsHalf2.push(element);
			}
		});

		loadProducts(arrayProductsHalf1, productsHalf1Div);
		loadProducts(arrayProductsHalf2, productsHalf2Div);

		if (productIdAuxiliary != null && productIdAuxiliary != undefined) {
			document
				.getElementById(productIdAuxiliary)
				.scrollIntoView({ behavior: "instant", block: "center" });
		}
	}
};

/**
 * Mounts the half of products on the screen according to the key search page.
 * @date 5/25/2023 - 8:59:00 AM
 *
 * @param {[Object]} arrayHalf the data that will be loaded
 * @param {HTMLElement} divHalf element that contains other elements that will be mounted dynamically
 */
const loadProducts = (arrayHalf, divHalf) => {
	while (divHalf.children.length > 0) {
		divHalf.removeChild(divHalf.children[0]);
	}

	arrayHalf.forEach((productElement) => {
		// <div class="swiper-slide box"></div>
		const productDiv = document.createElement("div");
		productDiv.classList.add("swiper-slide");
		productDiv.classList.add("box");
		productDiv.id = productElement.id;
		// <a href="../html/product_details.html">
		const productLink = document.createElement("a");
		productLink.href = "#";
		// <img src="../images/whey-protein.webp" alt="">
		const productImage = document.createElement("img");
		productImage.src = productElement.image;

		productLink.appendChild(productImage);
		productLink.addEventListener("click", () => {
			dataURL.delete("token");
			dataURL.delete("key-search");
			dataURL.delete("role");
			dataURL.delete("id");

			dataURL.append("token", tokenParameter);
			dataURL.append("role", roleParameter);
			dataURL.append("id", productElement.id);

			window.location.href = "product_details.html?" + dataURL.toString();
		});
		// <h3>whey gold standard</h3>
		const productName = document.createElement("h3");
		productName.innerText = productElement.name;

		// <div class="price">4.99€/- - 10.99€/-</div>
		const productPrice = document.createElement("div");
		productPrice.classList.add("price");
		productPrice.innerText = productElement.price;

		// <div class="likes"></div>
		const likesDiv = document.createElement("div");
		likesDiv.classList.add("likes");
		// <p>beauty</p>
		const sectionName = document.createElement("p");
		sectionName.innerText = productElement.section;
		// <p>30
		const productReputation = document.createElement("p");
		productReputation.innerText = productElement.totalLikes + "  ";

		const spaces = document.createTextNode("\u2003");
		// <a href="#"></a>
		const likeLink = document.createElement("a");
		likeLink.href = "#";

		// <i class="fa-regular fa-thumbs-up"></i>
		const likeIcon = document.createElement("i");

		if (
			tokenParameter == NOT_LOGGED_TOKEN ||
			!productElement.hasLoggedUserLiked
		) {
			likeIcon.classList.remove("fa-solid");
			likeIcon.classList.add("fa-regular");
		} else {
			likeIcon.classList.remove("fa-regular");
			likeIcon.classList.add("fa-solid");
		}
		likeIcon.classList.add("fa-thumbs-up");

		likeLink.appendChild(likeIcon);

		if (tokenParameter != NOT_LOGGED_TOKEN) {
			likeLink.onclick = async () => {
				if (!productElement.hasLoggedUserLiked) {
					const urlWithQueryParametersAll = new URL(urlBase + "/product/like");
					urlWithQueryParametersAll.searchParams.append(
						"id",
						productElement.id,
					);
					await fetch(
						urlWithQueryParametersAll,
						fetchContentFactoryWithoutBody(requestMethods.PUT, tokenParameter),
					)
						.then((response) => {
							if (response.ok) {
								productIdAuxiliary = productElement.id;
								return response.text();
							} else {
								console.error("Error on like");
							}
						})
						.then((liked) => {

							fetchProducts();
						});
				} else {
					const urlWithQueryParametersAll = new URL(urlBase + "/product/unlike");
					urlWithQueryParametersAll.searchParams.append(
						"id",
						productElement.id,
					);

					await fetch(
						urlWithQueryParametersAll,
						fetchContentFactoryWithoutBody(requestMethods.PUT, tokenParameter),
					)
						.then((response) => {
							if (response.ok) {
								productIdAuxiliary = productElement.id;
								return response.text();
							} else {
								console.error("Error on unlike");
							}
						})
						.then((unliked) => {

							fetchProducts();
						});
				}
			};
		}

		// <a href="#">
		const favoriteLink = document.createElement("a");
		favoriteLink.href = "#";

		// <i class="fa-solid fa-heart"></i>
		const favoriteIcon = document.createElement("i");

		if (
			tokenParameter == NOT_LOGGED_TOKEN ||
			!productElement.hasLoggedUserFavorited
		) {
			favoriteIcon.classList.remove("fa-solid");
			favoriteIcon.classList.add("fa-regular");
		} else {
			favoriteIcon.classList.remove("fa-regular");
			favoriteIcon.classList.add("fa-solid");
		}
		favoriteIcon.classList.add("fa-heart");

		favoriteLink.appendChild(favoriteIcon);

		productReputation.appendChild(likeLink);

		if (tokenParameter != NOT_LOGGED_TOKEN) {
			favoriteLink.onclick = async () => {
				if (!productElement.hasLoggedUserFavorited) {
					const urlWithQueryParametersAll = new URL(
						urlBase + "/product/favourite",
					);
					urlWithQueryParametersAll.searchParams.append(
						"id",
						productElement.id,
					);

					await fetch(
						urlWithQueryParametersAll,
						fetchContentFactoryWithoutBody(requestMethods.PUT, tokenParameter),
					)
						.then((response) => {
							if (response.ok) {
								productIdAuxiliary = productElement.id;
								return response.text();
							} else {
								console.error("Error on favorite");
							}
						})
						.then((favorited) => {

							fetchProducts();
						});
				} else {
					const urlWithQueryParametersAll = new URL(
						urlBase + "/product/unfavourite",
					);
					urlWithQueryParametersAll.searchParams.append(
						"id",
						productElement.id,
					);

					await fetch(
						urlWithQueryParametersAll,
						fetchContentFactoryWithoutBody(requestMethods.PUT, tokenParameter),
					)
						.then((response) => {
							if (response.ok) {
								productIdAuxiliary = productElement.id;
								return response.text();
							} else {
								console.error("Error on unfavourite");
							}
						})
						.then((unfavourite) => {

							fetchProducts();
						});
				}
			};
		}

		productReputation.appendChild(spaces);
		productReputation.appendChild(favoriteLink);

		likesDiv.appendChild(sectionName);
		likesDiv.appendChild(productReputation);

		const addToCart = document.createElement("a");

		addToCart.classList.add("btn");
		addToCart.innerText = "add to cart";

		// Ação do botão "add to cart": criar eventListener -> se não estiver logado, abre o form de login, se estiver, fazer o fetch de inserir o produto no carrinho
		addToCart.addEventListener("click", () => {
			const urlWithQueryParametersOrder = new URL(urlBase + "/order/by");
			urlWithQueryParametersOrder.searchParams.append("productId", productElement.id);

			if (tokenParameter == NOT_LOGGED_TOKEN) {
				signinForm.classList.toggle("active");
			} else {
				fetch(urlWithQueryParametersOrder,
					fetchContentFactoryWithoutBody(
						requestMethods.PUT,
						tokenParameter
					)
				).then((response) => {
					if (response.ok) {
						return response.json();
					} else {
						console.error("error on fetching cart");
					}
				}).then((cart) => {
					if (cart.id != null && cart.id != undefined && cart.productsDTO) {
						loadCartItem(cart);
						handleBadge(cart.productsDTO ? cart.productsDTO.length : 0);
					}

				});
			}
		});

		productDiv.appendChild(productLink);
		productDiv.appendChild(productName);
		productDiv.appendChild(productPrice);
		productDiv.appendChild(likesDiv);
		productDiv.appendChild(addToCart);

		divHalf.appendChild(productDiv);
	});
};

/**
 * Clears the current URL, builds a new one with token and role then redirect to home.
 * <ol>
 * 	<li>Clears the parameters to be added to URL</li>
 * 	<li>Add new parameters to URL</li>
 * 	<li>Add those parameters to the URL and redirect to it</li>
 * </ol>
 * @date 5/16/2023 - 4:26:56 PM
 */
const buildURLAndRedirectToHome = () => {
	dataURL.delete("token");
	dataURL.delete("role");

	dataURL.append("token", tokenParameter);
	dataURL.append("role", roleParameter);

	window.location.href = "home.html?" + dataURL.toString();
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
 * HTML <strong><em>anchor</em></strong> element in the navbar that has the appearance of a button links the logged user to their edit profile page.
 * @date 5/15/2023 - 10:22:00 PM
 *
 * @type {*}
 */
const editProfileButton = document.getElementById("edit-profile-btn");
/**
 * HTML <strong><em>paragraph</em></strong> element in the login form used to warn user about invalid credentials after the sign in button is clicked.
 * @date 5/8/2023 - 4:33:12 PM
 *
 * @type {Object}
 */
const signinError = document.getElementsByClassName("error-signin");
/**
 * HTML <strong><em>anchor</em></strong> element in the navbar that links the current user to favourites page.
 * @date 5/15/2023 - 10:16:33 PM
 *
 * @type {Object}
 */
const favouritesLink = document.getElementsByClassName("get-favourites");
/**
 * HTML <strong><em>anchor</em></strong> element in the navbar that links the current user to history page.
 * @date 5/15/2023 - 10:20:41 PM
 *
 * @type {Object}
 */
const historyLink = document.getElementsByClassName("get-history");
/**
 * HTML <strong><em>span</em></strong> element that contains the amount of element in cart
 * @date 5/17/2023 - 10:20:16 AM
 *
 * @type {Object}
 */
const badge = document.getElementsByClassName("badge")[0];
/**
 * HTML <strong><em>input text</em></strong> element that holds the product name search content.
 * @date 5/25/2023 - 4:47:55 PM
 *
 * @type {HTMLElement}
 */
const productSearchInput = document.getElementById("search-box");
/**
 * HTML <strong><em>label</em></strong> element in the navbar that has the appearance of a magnifier.
 * @date 5/25/2023 - 3:14:46 PM
 *
 * @type {HTMLElement}
 */
const search = document.getElementById("search-button");
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
const roleParameter = parameters.get("role");
/**
 * Order id to be get from URL.
 * @date 5/24/2023 - 4:46:24 PM
 *
 * @type {String}
 */
const orderIdParameter = parameters.get("id");
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
				dataURL.delete("key-search");
				dataURL.delete("role");

				dataURL.append("token", loggedUser.token);
				dataURL.append("key-search", keySearchParameter);
				dataURL.append("role", loggedUser.role);

				window.location.href = "order_products_favorites.html?" + dataURL.toString();

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
							console.error("Error fetching order non concluded (cart)");
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
						console.error("Error fetching finished orders");
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
	while (cartDiv.children.length > 0) {
		cartDiv.removeChild(cartDiv.children[0]);
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

for (const historyElement of historyLink) {
	historyElement.addEventListener('click', () => {
		dataURL.delete("token");
		dataURL.delete("key-search");
		dataURL.delete("role");

		dataURL.append("token", tokenParameter);
		dataURL.append("role", roleParameter);

		window.location.href = "history.html?" + dataURL.toString();
	});
}

search.addEventListener('click', () => {
	verify = false;
	if (tokenParameter != NOT_LOGGED_TOKEN) {
		verify = true;
	}

	let productSearchInputValue = productSearchInput.value.trim();
	const urlWithQueryParametersAllByName = new URL(urlBase + "/product/all-by-");

	urlWithQueryParametersAllByName.searchParams.append("verify", verify);
	urlWithQueryParametersAllByName.searchParams.append("name", productSearchInputValue);

	fetch(urlWithQueryParametersAllByName, fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter))
		.then((response) => {
			if (response.ok) {
				return response.json();
			}
		})
		.then((productList) => {
			this.productList = productList;
			divideArrays(this.productList);
		});
});

// NAVBAR

let searchForm = document.querySelector(".search-form");
document.querySelector("#search-btn").onclick = () => {
	searchForm.classList.toggle("active");
	shoppingCart.classList.remove("active");
	signinForm.classList.remove("active");
	navbar.classList.remove("active");
};

let shoppingCart = document.querySelector(".shopping-cart");
document.querySelector("#cart-btn").onclick = () => {
	searchForm.classList.remove("active");
	shoppingCart.classList.toggle("active");
	signinForm.classList.remove("active");
	navbar.classList.remove("active");
};

let signinForm = document.querySelector(".signin-form");
document.querySelector("#signin-btn").onclick = () => {
	searchForm.classList.remove("active");
	shoppingCart.classList.remove("active");
	signinForm.classList.toggle("active");
	navbar.classList.remove("active");
};