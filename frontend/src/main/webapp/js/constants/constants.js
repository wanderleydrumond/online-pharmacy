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
 * Generates the content of a fetch when it has no body and just one header.
 * @date 5/8/2023 - 4:44:41 PM
 *
 * @param {JSON} requestMethod
 * @param {Headers} header
 * @returns {{ method: any; "Content-Type": "application/json"; headers: any; }}
 */
function fetchContentFactoryWithoutBody(requestMethod, header) {
	return {
		method: requestMethod,
		"Content-Type": contentTypeJson,
		headers: header,
	};
}

function fetchContentFactoryWithBody(requestMethod, dataBody, header) {
	console.log("Entrei no fetchContentFactoryWithBody");
	// console.log(tokenParameter);
	console.log(dataBody);
	return {
		method: requestMethod,
		body: JSON.stringify(dataBody),
		headers: {
			"Content-Type": contentTypeJson,
			headers: header,
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

// ENUMS

const section = {
	BEAUTY: "BEAUTY",
	HEALTH: "HEALTH",
	SUPPLEMENTS: "SUPPLEMENTS",
};

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
