const parameters = new URLSearchParams(window.location.search);
let keySearchParameter = parameters.get("key-search");
let tokenParameter = parameters.get("token");
let roleParameter = parameters.get("role");
let productList = [];
const header = new Headers();

window.onload = async () => {
	let verify = false;
	header.append("token", tokenParameter);

	if (tokenParameter != NOT_LOGGED_TOKEN) {
		verify = true;
	}

	switch (keySearchParameter) {
		case keySearchEnum.ALL:
			// chamar fetch do getAll()
			let urlWithQueryParameters = new URL(urlBase + "/product/all");
			urlWithQueryParameters.searchParams.append("verify", verify);
			
			await fetch(
				urlWithQueryParameters,
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
					console.log(this.productList);
				});
			break;
		case keySearchEnum.BEAUTY:
		case keySearchEnum.HEALTH:
		case keySearchEnum.SUPPLEMENTS:
			let section = keySearchParameter;
			// chamar fetch do getAllBySection()
			// await fetch(urlBase + '/product/all', fetchContentFactoryWithoutBody(requestMethods.GET))
			// 	.then((response) => {})
			// 	.then((productList) => {});
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
 * Logs out the current user.
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
			console.log("Signout failed");
		}
	});
};

document.getElementById("signout-btn").addEventListener("click", signout);
