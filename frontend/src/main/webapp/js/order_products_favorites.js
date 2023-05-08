/**
 * The way to get things of URL.
 * @date 5/7/2023 - 9:51:20 AM
 *
 * @type {URLSearchParams}
 */
const parameters = new URLSearchParams(window.location.search);
/**
 * Object that will be passed through header of the request.
 * @date 5/8/2023 - 2:22:34 PM
 *
 * @type {Headers}
 */
const header = new Headers();
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

window.onload = async () => {
	let verify = false;
	header.append("token", tokenParameter);

	if (tokenParameter != NOT_LOGGED_TOKEN) {
		verify = true;
	}

	switch (keySearchParameter) {
		case keySearchEnum.ALL:
			let urlWithQueryParametersAll = new URL(urlBase + "/product/all");
			urlWithQueryParametersAll.searchParams.append("verify", verify);

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
				});
			break;
		case keySearchEnum.BEAUTY:
		case keySearchEnum.HEALTH:
		case keySearchEnum.SUPPLEMENTS:
			let section = keySearchParameter;
			let urlWithQueryParametersSection = new URL(urlBase + "/product/all-by");
			urlWithQueryParametersSection.searchParams.append("verify", verify);
			urlWithQueryParametersSection.searchParams.append("section", section);
			await fetch(
				urlWithQueryParametersSection,
				fetchContentFactoryWithoutBody(requestMethods.GET),
				header,
			)
				.then((response) => {
					if (response.ok) {
						return response.json();
					} else {
						console.log("Error");
					}
				})
				.then((productListBySection) => {
					this.productList = productListBySection;
					console.log(this.productList);
				});
			break;
		case keySearchEnum.FAVOURITES:
			// chamar fetch dos favoritos do logged user
			// await fetch(urlBase + '/product/all', fetchContentFactoryWithoutBody(requestMethods.GET))
			// 	.then((response) => {})
			// 	.then((productList) => {});
			break;
		case keySearchEnum.ORDER:
			// chamar fetch dos detalhes de um determinado pedido
			break;
		default:
			break;
	}
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
 *
 * @date 5/6/2023 - 8:27:49 PM
 *
 * @async
 * @returns {boolean} true if the user is logged out, false otherwise
 */
const signout = async (token) => {
	await fetch(
		urlBase + "/user/signout",
		fetchContentFactoryWithoutBody(requestMethods.POST, token),
	).then((response) => {
		if (response.ok) {
			cartButton.classList.add("disappear");
			signinButton.classList.remove("disappear");
			signoutButton.classList.add("disappear");
			dashboardButton.classList.add("disappear");
		} else {
			console.log("Sign out failed");
		}
	});
};

document.getElementById("signout-btn").addEventListener("click", signout);
