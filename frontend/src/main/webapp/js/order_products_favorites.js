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

window.onload = async () => {
	await fetchProducts();
};

const fetchProducts = async () => {
	let verify = false;

	if (tokenParameter != NOT_LOGGED_TOKEN) {
		verify = true;
	}

	switch (keySearchParameter) {
		case keySearchEnum.ALL:
			let urlWithQueryParametersAll = new URL(urlBase + "/product/all");
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
			let urlWithQueryParametersSection = new URL(urlBase + "/product/all-by");
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
			let urlWithQueryParametersFavourites = new URL(urlBase + "/product/favourites");
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
		// <img src="../images/whey-protein.webp" alt="">
		const productImage = document.createElement("img");
		productImage.src = productElement.image;
		productLink.appendChild(productImage);
		productLink.addEventListener("click", () => {
			console.log("productElement.id", productElement.id);
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
					let urlWithQueryParametersAll = new URL(urlBase + "/product/like");
					urlWithQueryParametersAll.searchParams.append(
						"id",
						productElement.id,
					);
					console.log("urlWithQueryParametersAll", urlWithQueryParametersAll);
					await fetch(
						urlWithQueryParametersAll,
						fetchContentFactoryWithoutBody(requestMethods.PUT, header),
					)
						.then((response) => {
							if (response.ok) {
								productIdAuxiliary = productElement.id;
								return response.text();
							} else {
								console.log("Error");
							}
						})
						.then((liked) => {
							console.log("liked", liked);
							fetchProducts();
						});
				} else {
					let urlWithQueryParametersAll = new URL(urlBase + "/product/unlike");
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
								console.log("Error");
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
		// <i class="fa-solid fa-heart"></i>
		const favoriteIcon = document.createElement("i");

		favoriteIcon.classList.add("fa-regular"); // TODO somente se não tiver dado o like ou se não estiver logado
		favoriteIcon.classList.add("fa-heart");

		favoriteLink.appendChild(favoriteIcon);
		productReputation.appendChild(likeLink);
		productReputation.appendChild(spaces);
		productReputation.appendChild(favoriteLink);

		likesDiv.appendChild(sectionName);
		likesDiv.appendChild(productReputation);

		const addToCart = document.createElement("a");

		addToCart.classList.add("btn");
		addToCart.innerHTML = "add to cart";

		productDiv.appendChild(productLink);
		productDiv.appendChild(productName);
		productDiv.appendChild(productPrice);
		productDiv.appendChild(likesDiv);
		productDiv.appendChild(addToCart);

		divHalf.appendChild(productDiv);
	});
};

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
	if (
		tokenParameter != null &&
		tokenParameter != undefined &&
		tokenParameter != ""
	) {
		header = new Headers();
		header.append("token", tokenParameter);
		await fetch(
			urlBase + "/user/signout",
			fetchContentFactoryWithoutBody(requestMethods.POST, header),
		).then((response) => {
			if (response.ok) {
				cartButton.classList.add("disappear");
				signinButton.classList.remove("disappear");
				signoutButton.classList.add("disappear");
				dashboardButton.classList.add("disappear");
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
