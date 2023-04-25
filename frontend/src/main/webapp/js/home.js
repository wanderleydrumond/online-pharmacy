document.getElementById("teste").addEventListener("click", teste);

const urlBase = "http://localhost:8080/backend/pharmacy/";

function teste(params) {
	const header = new Headers();
	const token = "ac4cb5c8-818d-461f-a1be-e1759fbbfdc4";

	header.append("token", token);

	const fetchContent = {
		method: "GET",
		"Content-Type": "application/json",
		headers: header,
		mode: "cors",
	};

	fetch(urlBase + "user/dashboard", fetchContent)
		.then((response) => {
			// Pego o status da resposta da requisição. Ex.: 403, 200, 404...
			console.log("Entrei no then");

			console.log(response.ok);
			console.log(response.status);

			if (response.ok) {
				console.log("Dashboard OK!");
				// Estou esperando apenas o token. Se eu estivesse esperando o objeto seria response.json
				return response.json();
			} else {
				// Aqui que eu informo ao usuário que ele não conseguiu fazer o login
				console.log("Dashboard falhou");
			}
		})
		.then((dashboardData) => {
			console.log("Segundo then");
			// Pego o que veio no corpo da resposta da requisição.
			console.log(dashboardData);
		});
}
