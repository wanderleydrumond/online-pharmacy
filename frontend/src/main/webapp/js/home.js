const inputUsername = document.getElementById("username");
const inputPassword = document.getElementById("password");
const cartButton = document.getElementById("cart-btn");
const dashboardButton = document.getElementById("dashboard-btn");
const signinButton = document.getElementById("signin-btn");
const signoutButton = document.getElementById("signout-btn");
const signinError = document.querySelector(".error");

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
					inputUsername.value = "";
					inputPassword.value = "";
					cartButton.classList.remove("disappear");
					signinForm.classList.remove("active");
					signinButton.classList.add("disappear");
					signoutButton.classList.remove("disappear");
					return response.json();
				} else {
					signinError.classList.remove("disappear");
					setTimeout(() => {
						signinError.classList.add("disappear");
					}, 2000);
				}
			})
			.then((user) => {
				if (user.role == role.ADMINISTRATOR) {
					dashboardButton.classList.remove("disappear");
				}
				loggedUser = user;
			});
	} else {
		signinError.classList.remove("disappear");
		setTimeout(() => {
			signinError.classList.add("disappear");
		}, 2000);
	}
};

document.getElementById("signin").addEventListener("click", signin);