/**
 * The way to get things of URL.
 * @date 5/7/2023 - 9:51:20 AM
 *
 * @type {URLSearchParams}
 */
const parameters = new URLSearchParams(window.location.search);
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

window.onload = () => {
	if (loggedUser != null || (tokenParameter != NOT_LOGGED_TOKEN && roleParameter != undefined)) {
		manageNavbar();
	}
	fetchProducts();
};

/**
 * Loads the products according to the provided search key.
 * <ol>
 * 	<li>Creates the variable that verifies if this product is liked or marked as favorite and initialise it as false</li>
 * 	
 * </ol>
 * @date 5/13/2023 - 1:44:30 PM
 *
 * @async
 * @returns {[JSON]} product list according to the given key search parameter
 */
const fetchProducts = async () => {
	let verify = false;

	if (tokenParameter != NOT_LOGGED_TOKEN) {
		verify = true;
	}

	switch (keySearchParameter) {
		case keySearchEnum.ALL:
			const urlWithQueryParametersAll = new URL(urlBase + "/product/all");
			urlWithQueryParametersAll.searchParams.append("verify", verify);

			header = new Headers();
			header.append("token", tokenParameter);
			console.log("header", header.get("token"));
			await fetch(
				urlWithQueryParametersAll,
				fetchContentFactoryWithoutBody(requestMethods.GET, header),
			)
				.then((response) => {
					if (response.ok) {
						return response.json();
					} else {
						console.log("Error");
					}
				})
				.then((productList) => {
					this.productList = productList;
					console.log("this.productList", this.productList);
				});
			break;

		case keySearchEnum.BEAUTY:
		case keySearchEnum.HEALTH:
		case keySearchEnum.SUPPLEMENTS:
			let section = keySearchParameter;
			const urlWithQueryParametersSection = new URL(urlBase + "/product/all-by");
			urlWithQueryParametersSection.searchParams.append("verify", verify);
			urlWithQueryParametersSection.searchParams.append("section", section);

			header = new Headers();
			header.append("token", tokenParameter);
			console.log("headerSection", header.get("token"));

			await fetch(
				urlWithQueryParametersSection,
				fetchContentFactoryWithoutBody(requestMethods.GET),
				header,
			)
				.then((response) => {
					if (response.ok) {
						return response.json();
					} else {
						console.error("Error");
					}
				})
				.then((productList) => {
					this.productList = productList;
					console.log("this.productList", this.productList);
				});
			break;
		case keySearchEnum.FAVOURITES:
			const urlWithQueryParametersFavourites = new URL(
				urlBase + "/product/favourites",
			);
			urlWithQueryParametersFavourites.searchParams.append("verify", verify);

			header = new Headers();
			header.append("token", tokenParameter);
			console.log("header", header.get("token"));
			await fetch(
				urlWithQueryParametersFavourites,
				fetchContentFactoryWithoutBody(requestMethods.GET, header),
			)
				.then((response) => {
					if (response.ok) {
						return response.json();
					} else {
						console.log("Error");
					}
				})
				.then((productList) => {
					this.productList = productList;
					console.log("this.productList", this.productList);
				});
			break;
		case keySearchEnum.ORDER:
			header = new Headers();
			header.append("token", tokenParameter);
			// chamar fetch dos detalhes de um determinado pedido
			break;
		default:
			break;
	}

	divideArrays(this.productList);
};

const divideArrays = (productList) => {
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
};

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
			header = new Headers();
			header.append("token", tokenParameter);
			likeLink.onclick = async () => {
				if (!productElement.hasLoggedUserLiked) {
					const urlWithQueryParametersAll = new URL(urlBase + "/product/like");
					urlWithQueryParametersAll.searchParams.append(
						"id",
						productElement.id,
					);
					console.log(
						"urlWithQueryParametersAll in like",
						urlWithQueryParametersAll,
					);
					await fetch(
						urlWithQueryParametersAll,
						fetchContentFactoryWithoutBody(requestMethods.PUT, header),
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
							console.log("liked", liked);
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
						fetchContentFactoryWithoutBody(requestMethods.PUT, header),
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
							console.log("unliked", unliked);
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
			header = new Headers();
			header.append("token", tokenParameter);

			favoriteLink.onclick = async () => {
				if (!productElement.hasLoggedUserFavorited) {
					const urlWithQueryParametersAll = new URL(
						urlBase + "/product/favourite",
					);
					urlWithQueryParametersAll.searchParams.append(
						"id",
						productElement.id,
					);
					console.log(
						"urlWithQueryParametersAll in favourites",
						urlWithQueryParametersAll,
					);

					await fetch(
						urlWithQueryParametersAll,
						fetchContentFactoryWithoutBody(requestMethods.PUT, header),
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
							console.log("Favorited", favorited);
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
						fetchContentFactoryWithoutBody(requestMethods.PUT, header),
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
							console.log("unfavourite", unfavourite);
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

			const header = new Headers();
			header.append("token", tokenParameter);

			if (tokenParameter == NOT_LOGGED_TOKEN) {
				signinForm.classList.toggle("active");
			} else {
				fetch(urlWithQueryParametersOrder,
				fetchContentFactoryWithoutBody(
					requestMethods.PUT,
					header
					)
				).then((response) => {
					if (response.ok) {
						return response.json();
					} else {
						console.error("error on fetching order product");
					}
				}).then((order) => {
					// TODO: inserir os elementos referentes a cada produto no carrinho
					console.log("order", order);
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
 * HTML <strong><em>paragraph</em></strong> element in the login form used to warn user about invalid credentials after the sign in button is clicked.
 * @date 5/8/2023 - 4:33:12 PM
 *
 * @type {Object}
 */
const signinError = document.querySelector(".error-signin");
const favouritesLink = document.querySelectorAll(".get-favourites");
/**
 * UUID to be get from URL.
 * @date 5/8/2023 - 5:08:34 PM
 *
 * @type {string}
 */
let tokenParameter = parameters.get("token");
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

	for (const link of favouritesLink) {
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
		const header = new Headers();
		header.append("token", token);
		await fetch(
			urlBase + "/user/signout",
			fetchContentFactoryWithoutBody(requestMethods.POST, header),
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