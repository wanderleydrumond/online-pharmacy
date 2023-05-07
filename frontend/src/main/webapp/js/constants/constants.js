const contentTypeJson = "application/json";

/**
 * The way to put things in URL.
 * @date 5/7/2023 - 9:51:20 AM
 *
 * @type {URLSearchParams}
 */
const parameters = new URLSearchParams(window.location.search);
let dataURL = new URLSearchParams();
let loggedUser;
/**
 * URL system base
 */
const urlBase = "http://localhost:8080/backend/pharmacy";

/**
 * Enumaration that contains all http request method types.
 * 
 * @see https://www.sohamkamani.com/javascript/enums/
 */
const requestMethods = {
	POST: "POST",
	GET: "GET",
	DELETE: "DELETE",
	PUT: "PUT",
};

/* Para enviar dados pelo cabeçalho da requisição, caso seja mais do que um valor, preciso criar um objeto Headers e deverá ser feito
 .append dos respectivos valores (ver signin.js). Porém se for apenas um valor ele é enviado diretamente como objeto, como abaixo.*/
function fetchContentFactoryWithoutBody(requestMethod) {
	const token = loggedUser.token;
	return {
		method: requestMethod,
		"Content-Type": contentTypeJson,
		headers: {
			token,
		},
	};
}

function fetchContentFactoryWithBody(requestMethod, dataBody) {
	console.log("Entrei no fetchContentFactoryWithBody");
	console.log(token);
	console.log(dataBody);
	return {
		method: requestMethod,
		body: JSON.stringify(dataBody),
		headers: {
			"Content-Type": contentTypeJson,
			token,
			Accept: "*/*",
		},
	};
}

function fetchContentFactoryWithoutBodyMultipleHeaders(requestMethod, headers) {
	// Caso seja necessário informar o CORS: mode:"cors"
	return {
		method: requestMethod,
		"Content-Type": contentTypeJson,
		headers: headers,
		mode: "cors",
	};
}

/**
 * Logs out the current user.
 * @date 5/6/2023 - 8:27:49 PM
 *
 * @async
 * @returns {boolean} true if the user is logged out, false otherwise
 */
const signout = async () => {
	await fetch(
		urlBase + "/user/signout",
		fetchContentFactoryWithoutBody(requestMethods.POST),
	).then((response) => {
		if (response.ok) {
			cartButton.classList.add("disappear");
			signinButton.classList.remove("disappear");
			signoutButton.classList.add("disappear");
			dashboardButton.classList.add("disappear");
			loggedUser = null;
		} else {
			console.log("Signout failed");
		}
	});
};

document.getElementById("signout-btn").addEventListener("click", signout);
