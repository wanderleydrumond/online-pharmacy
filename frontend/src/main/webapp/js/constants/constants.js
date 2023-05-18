/**
 * A generic token when a user assigned to a non logged user
 */
const NOT_LOGGED_TOKEN = "00000000-0000-0000-0000-000000000000";
/**
 * The requisition content type is json.
 * @date 5/7/2023 - 3:59:56 PM
 *
 * @type {"application/json"}
 */
const contentTypeJson = "application/json";
/**
 * The way to write, delete or edit URL.
 * @date 5/7/2023 - 3:58:36 PM
 *
 * @type {URLSearchParams}
 */
let dataURL = new URLSearchParams();
/**
 * URL system base
 */
const urlBase = "http://localhost:8080/backend/pharmacy";

/**
 * Fills the fetch content when it has no body and one single header.
 * @date 5/8/2023 - 4:44:41 PM
 *
 * @param {JSON} requestMethod the http method type of the request
 * @param {Headers} header of the request
 * @returns {JSON | [JSON]} the request response body
 */
function fetchContentFactoryWithoutBody(requestMethod, token) {
	const header = new Headers();
	header.append("token", token);

	return {
		method: requestMethod,
		"Content-Type": contentTypeJson,
		headers: header,
	};
}

/**
 * Fills the fetch content when it has body and one single header.
 * @date 5/15/2023 - 3:31:57 PM
 *
 * @param {string} requestMethod the http method type of the request
 * @param {JSON} dataBody the body content
 * @param {Headers} header of the request
 * @returns {JSON | [JSON]} the request response body
 */
function fetchContentFactoryWithBody(requestMethod, dataBody, token) {
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

/**
 * Fills the fetch content when it has no body and multiple headers.
 * @date 5/15/2023 - 3:28:22 PM
 *
 * @param {string} requestMethod the http method type of the request
 * @param {Headers} headers of the request
 * @returns {JSON} the request response body
 */
function fetchContentFactoryWithoutBodyMultipleHeaders(requestMethod, headers) {
	// Caso seja necessário informar o CORS: mode:"cors"
	return {
		method: requestMethod,
		"Content-Type": contentTypeJson,
		headers: headers,
		mode: "cors",
	};
}

// hack: ENUMS

/**
 * All sections that a a product can belong to.
 * @date 5/15/2023 - 3:25:56 PM
 *
 * @type {JSON}
 */
const section = {
	BEAUTY: "BEAUTY",
	HEALTH: "HEALTH",
	SUPPLEMENTS: "SUPPLEMENTS",
};

/**
 * All roles that an user can have.
 * @date 5/15/2023 - 3:27:17 PM
 *
 * @type {JSON}
 */
const role = {
	ADMINISTRATOR: "ADMINISTRATOR",
	CLIENT: "CLIENT",
	VISITOR: "VISITOR",
};

/**
 * Enumeration that contains all http request method types.
 *
 * @see https://www.sohamkamani.com/javascript/enums/
 */
const requestMethods = {
	POST: "POST",
	GET: "GET",
	DELETE: "DELETE",
	PUT: "PUT",
};

/**
 * Enumeration that contains all sections.
 *
 * @see https://www.sohamkamani.com/javascript/enums/
 */
const keySearchEnum = {
	ALL: "ALL",
	BEAUTY: section.BEAUTY,
	SUPPLEMENTS: section.SUPPLEMENTS,
	HEALTH: section.HEALTH,
	FAVOURITES: "FAVOURITES",
	ORDER: "ORDER",
};