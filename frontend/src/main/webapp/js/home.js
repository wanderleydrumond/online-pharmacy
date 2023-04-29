// Necessários para funcionalidade do site
let searchForm = document.querySelector('.search-form');
document.querySelector('#search-btn').onclick = () => {
	searchForm.classList.toggle('active');
	shoppingCart.classList.remove('active');
	loginForm.classList.remove('active');
	navbar.classList.remove('active');
}

let shoppingCart = document.querySelector('.shopping-cart');
document.querySelector('#cart-btn').onclick = () => {
	searchForm.classList.remove('active');
	shoppingCart.classList.toggle('active');
	loginForm.classList.remove('active');
	navbar.classList.remove('active');
}

let loginForm = document.querySelector('.login-form');
document.querySelector('#login-btn').onclick = () => {
	searchForm.classList.remove('active');
	shoppingCart.classList.remove('active');
	loginForm.classList.toggle('active');
	navbar.classList.remove('active');
}

let navbar = document.querySelector('.navbar');
document.querySelector('#menu-btn').onclick = () => {
	searchForm.classList.remove('active');
	shoppingCart.classList.remove('active');
	loginForm.classList.remove('active');
	navbar.classList.toggle('active');
}
window.onscroll = () => {
	searchForm.classList.remove('active');
	shoppingCart.classList.remove('active');
	loginForm.classList.remove('active');
	navbar.classList.remove('active');
}

var swiper = new Swiper(".product-slider", {
	loop: true,
	spaceBetween: 20,
	autoplay: {
		delay: 7500,
		disableOnInteraction: false,
	},
	breakpoints: {
	  0: {
		slidesPerView: 1,
	  },
	  768: {
		slidesPerView: 2,
	  },
	  1020: {
		slidesPerView: 3,
	  },
	},
  });

  var swiper = new Swiper(".review-slider", {
	loop: true,
	spaceBetween: 20,
	autoplay: {
		delay: 7500,
		disableOnInteraction: false,
	},
	breakpoints: {
	  0: {
		slidesPerView: 1,
	  },
	  768: {
		slidesPerView: 2,
	  },
	  1020: {
		slidesPerView: 3,
	  },
	},
  });







// Daqui para baixo foram testes de conectividade entre o front e o back
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
