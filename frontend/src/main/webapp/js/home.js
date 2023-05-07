const inputUsername = document.getElementById("username");
const inputPassword = document.getElementById("password");
const cartButton = document.getElementById("cart-btn");
const dashboardButton = document.getElementById("dashboard-btn");
const signinButton = document.getElementById("signin-btn");
const signoutButton = document.getElementById("signout-btn");
const signinError = document.querySelector(".error");
let loggedUser;

window.onload = () => {
	if (loggedUser != null) {
		manageNavbar();
	}
};

const manageNavbar = () => {
	inputUsername.value = "";
	inputPassword.value = "";
	cartButton.classList.remove("disappear");
	signinForm.classList.remove("active");
	signinButton.classList.add("disappear");
	signoutButton.classList.remove("disappear");

	if (loggedUser.role == role.ADMINISTRATOR) {
		dashboardButton.classList.remove("disappear");
	}
};

/**
 * Logs in a user into the system.
 *
 * @date 5/6/2023 - 8:13:57 PM
 *
 * @async
 * @returns {json} the logged user
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
				manageNavbar();
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

const buildURL = (keySearchEnumParam) => {
	dataURL.delete("token");
	dataURL.delete("key-search");
	dataURL.delete("role");

	let token = NOT_LOGGED_TOKEN, role;
	if (loggedUser !== null && loggedUser !== undefined) {
		token = loggedUser.token;
		role = loggedUser.role;
	}

	dataURL.append("token", token);
	dataURL.append("key-search", keySearchEnumParam);
	dataURL.append("role", role);
	//Tira da página atual e direciona para página informada
	window.location.href = "order_products_favorites.html?" + dataURL.toString();
};
